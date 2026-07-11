package com.cpic.barsms.bpm.domain.serviceimpl;

import com.cpic.barsms.bpm.common.enums.BatchStepEnum;
import com.cpic.barsms.bpm.common.exception.BizBatchException;
import com.cpic.barsms.bpm.domain.dto.BatchGenerateResultDTO;
import com.cpic.barsms.bpm.domain.service.BpmBatchExecLogService;
import com.cpic.barsms.bpm.domain.service.BpmDeliverableService;
import com.cpic.barsms.bpm.domain.service.BpmNodeInstanceService;
import com.cpic.barsms.bpm.domain.service.DailyTaskGeneratorService;
import com.cpic.barsms.bpm.domain.service.MonthlyTaskGeneratorService;
import com.cpic.barsms.bpm.domain.service.OfficeMappingService;
import com.cpic.barsms.bpm.domain.service.ReferenceNodeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BatchGenerateTxServiceImplTest {

    private static final Date T_DAY = new Date();
    private static final Date T_DAY_BASE = new Date(T_DAY.getTime() - 86400000L);
    private static final Long SCENE_ID = 1L;
    private static final String VERSION = "v1";
    private static final String PREFIX = "";
    private static final Long LOG_ID = 100L;

    @Mock private MonthlyTaskGeneratorService monthlyTaskGeneratorService;
    @Mock private DailyTaskGeneratorService dailyTaskGeneratorService;
    @Mock private OfficeMappingService officeMappingService;
    @Mock private ReferenceNodeService referenceNodeService;
    @Mock private BpmDeliverableService bpmDeliverableService;
    @Mock private BpmNodeInstanceService bpmNodeInstanceService;
    @Mock private BpmBatchExecLogService bpmBatchExecLogService;

    @InjectMocks
    private BatchGenerateTxServiceImpl service;

    @Test
    void doGenerate_runsAllStepsInOrderAndReturnsCounts() {
        when(bpmBatchExecLogService.existsSuccess(T_DAY, VERSION, PREFIX)).thenReturn(false);
        when(bpmBatchExecLogService.startLog(T_DAY, VERSION, PREFIX)).thenReturn(LOG_ID);
        when(bpmNodeInstanceService.deleteByTargetMonth(T_DAY, SCENE_ID, PREFIX)).thenReturn(10);
        when(bpmNodeInstanceService.countByTargetMonth(T_DAY, SCENE_ID, PREFIX)).thenReturn(5);
        when(officeMappingService.generate(T_DAY, T_DAY_BASE, SCENE_ID, PREFIX, VERSION)).thenReturn(3);
        when(bpmDeliverableService.generate(T_DAY, T_DAY_BASE, VERSION, SCENE_ID, PREFIX)).thenReturn(2);

        BatchGenerateResultDTO result = service.doGenerate(T_DAY, T_DAY_BASE, SCENE_ID, VERSION, PREFIX);

        assertEquals(5, result.getInstanceCount());
        assertEquals(3, result.getOfficeCount());
        assertEquals(2, result.getDeliverableCount());

        InOrder inOrder = inOrder(bpmBatchExecLogService, bpmNodeInstanceService,
                monthlyTaskGeneratorService, dailyTaskGeneratorService,
                officeMappingService, referenceNodeService, bpmDeliverableService);

        inOrder.verify(bpmBatchExecLogService).existsSuccess(T_DAY, VERSION, PREFIX);
        inOrder.verify(bpmBatchExecLogService).startLog(T_DAY, VERSION, PREFIX);
        inOrder.verify(bpmBatchExecLogService).updateStep(LOG_ID, BatchStepEnum.CLEAR_DATA);
        inOrder.verify(bpmNodeInstanceService).deleteByTargetMonth(T_DAY, SCENE_ID, PREFIX);
        inOrder.verify(bpmBatchExecLogService).updateStep(LOG_ID, BatchStepEnum.INSERT_MONTHLY);
        inOrder.verify(monthlyTaskGeneratorService).generate(T_DAY, T_DAY_BASE, VERSION, SCENE_ID, PREFIX);
        inOrder.verify(bpmBatchExecLogService).updateStep(LOG_ID, BatchStepEnum.INSERT_DAILY);
        inOrder.verify(dailyTaskGeneratorService).generate(T_DAY, T_DAY_BASE, VERSION, SCENE_ID, PREFIX);
        inOrder.verify(bpmBatchExecLogService).updateStep(LOG_ID, BatchStepEnum.GENERATE_OFFICE);
        inOrder.verify(officeMappingService).generate(T_DAY, T_DAY_BASE, SCENE_ID, PREFIX, VERSION);
        inOrder.verify(bpmBatchExecLogService).updateStep(LOG_ID, BatchStepEnum.MARK_UNMATCHED);
        inOrder.verify(officeMappingService).markLogicDelete(T_DAY, SCENE_ID, PREFIX);
        inOrder.verify(bpmBatchExecLogService).updateStep(LOG_ID, BatchStepEnum.SET_READONLY);
        inOrder.verify(referenceNodeService).updateReadOnlyStatus(T_DAY, SCENE_ID, PREFIX);
        inOrder.verify(bpmBatchExecLogService).updateStep(LOG_ID, BatchStepEnum.SET_REFERENCE);
        inOrder.verify(referenceNodeService).updateReferenceNodes(T_DAY, SCENE_ID, PREFIX);
        inOrder.verify(bpmBatchExecLogService).updateStep(LOG_ID, BatchStepEnum.GENERATE_DELIVERABLE);
        inOrder.verify(bpmDeliverableService).generate(T_DAY, T_DAY_BASE, VERSION, SCENE_ID, PREFIX);
        inOrder.verify(bpmBatchExecLogService).markSuccess(LOG_ID, 5, 3, 2);

        verify(bpmBatchExecLogService, never()).markFailed(any(), any(), any());
    }

    @Test
    void doGenerate_marksFailedAndRethrowsWhenStepThrows() {
        when(bpmBatchExecLogService.existsSuccess(T_DAY, VERSION, PREFIX)).thenReturn(false);
        when(bpmBatchExecLogService.startLog(T_DAY, VERSION, PREFIX)).thenReturn(LOG_ID);
        when(bpmNodeInstanceService.deleteByTargetMonth(T_DAY, SCENE_ID, PREFIX)).thenReturn(10);
        doThrow(new RuntimeException("日历数据缺失")).when(dailyTaskGeneratorService)
                .generate(T_DAY, T_DAY_BASE, VERSION, SCENE_ID, PREFIX);

        // catch 块记录失败日志后原样向上抛出，不包装
        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> service.doGenerate(T_DAY, T_DAY_BASE, SCENE_ID, VERSION, PREFIX));

        assertEquals("日历数据缺失", thrown.getMessage());
        verify(bpmBatchExecLogService).markFailed(eq(LOG_ID), eq(""), anyString());
        verify(bpmBatchExecLogService, never()).markSuccess(any(), anyInt(), anyInt(), anyInt());
    }

    @Test
    void doGenerate_rejectsWhenAlreadySucceeded() {
        when(bpmBatchExecLogService.existsSuccess(T_DAY, VERSION, PREFIX)).thenReturn(true);

        BizBatchException thrown = assertThrows(BizBatchException.class,
                () -> service.doGenerate(T_DAY, T_DAY_BASE, SCENE_ID, VERSION, PREFIX));

        assertTrue(thrown.getMessage().contains("已生成过"));
        verify(bpmBatchExecLogService, never()).startLog(any(), any(), any());
    }

    @Test
    void doGenerate_throwsWhenNoInstanceGenerated() {
        when(bpmBatchExecLogService.existsSuccess(T_DAY, VERSION, PREFIX)).thenReturn(false);
        when(bpmBatchExecLogService.startLog(T_DAY, VERSION, PREFIX)).thenReturn(LOG_ID);
        when(bpmNodeInstanceService.deleteByTargetMonth(T_DAY, SCENE_ID, PREFIX)).thenReturn(0);
        when(bpmNodeInstanceService.countByTargetMonth(T_DAY, SCENE_ID, PREFIX)).thenReturn(0);

        assertThrows(BizBatchException.class,
                () -> service.doGenerate(T_DAY, T_DAY_BASE, SCENE_ID, VERSION, PREFIX));

        verify(bpmBatchExecLogService).markFailed(eq(LOG_ID), eq(""), anyString());
    }
}
