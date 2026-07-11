package com.cpic.barsms.bpm.domain.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cpic.barsms.bpm.common.constants.BatchConstants;
import com.cpic.barsms.bpm.infra.mapper.BpmNodeDepartmentMapper;
import com.cpic.barsms.bpm.infra.mapper.BpmNodeInstanceFormatMapper;
import com.cpic.barsms.bpm.infra.mapper.BpmNodeInstanceMapper;
import com.cpic.barsms.bpm.infra.mapper.BpmNodeInstanceOfficeMapper;
import com.cpic.barsms.bpm.infra.model.entity.BpmNodeInstance;
import com.cpic.barsms.bpm.infra.model.entity.BpmNodeInstanceFormat;
import com.cpic.barsms.bpm.infra.model.entity.BpmNodeInstanceOffice;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfficeMappingServiceImplTest {

    private static final Date T_DAY = new Date();
    private static final Long SCENE_ID = 7L;
    private static final String PREFIX = "";
    private static final String VERSION = "v1";

    @Mock private BpmNodeInstanceMapper bpmNodeInstanceMapper;
    @Mock private BpmNodeInstanceOfficeMapper bpmNodeInstanceOfficeMapper;
    @Mock private BpmNodeInstanceFormatMapper bpmNodeInstanceFormatMapper;
    @Mock private BpmNodeDepartmentMapper bpmNodeDepartmentMapper;

    @InjectMocks
    private OfficeMappingServiceImpl service;

    @Test
    void generate_parsesPipeAndCommaIntoOfficeRows() {
        BpmNodeInstance instance = new BpmNodeInstance();
        instance.setId(1L);
        instance.setNodeCode("N1");
        instance.setDataOrgLevel("分");            // levelIndex = 1
        instance.setOrgBranch("B001");             // 非 ALL → resolveOrgLevel 走到分公司分支
        instance.setOrgCenBranch(BatchConstants.ORG_ALL);
        instance.setOrgBusiBranch(BatchConstants.ORG_ALL);
        when(bpmNodeInstanceMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(instance));

        BpmNodeInstanceFormat template = new BpmNodeInstanceFormat();
        template.setNodeCode("N1");
        // 第 1 段（分公司）officeName 有两个值、deptName 有两个值
        template.setOfficeName("总办|分办A,分办B|中办|支办");
        template.setDeptName("总务|财务,行政|中务|支务");
        when(bpmNodeInstanceFormatMapper.selectTemplates(
                any(), anyString(), any(), anyString(), any()))
                .thenReturn(Collections.singletonList(template));

        when(bpmNodeDepartmentMapper.selectDeptCode(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenAnswer(inv -> "DCODE-" + inv.getArgument(0));

        int count = service.generate(T_DAY, new Date(), SCENE_ID, PREFIX, VERSION);

        assertEquals(2, count);

        ArgumentCaptor<List<BpmNodeInstanceOffice>> captor = ArgumentCaptor.forClass(List.class);
        verify(bpmNodeInstanceOfficeMapper).deleteByInstanceIds(eq(Collections.singletonList(1L)));
        verify(bpmNodeInstanceOfficeMapper).batchInsert(captor.capture());

        List<BpmNodeInstanceOffice> rows = captor.getValue();
        assertEquals(2, rows.size());
        assertEquals("分办A", rows.get(0).getOfficeName());
        assertEquals("DCODE-分办A", rows.get(0).getOfficeCode());
        assertEquals("财务", rows.get(0).getDeptName());
        assertEquals("DCODE-财务", rows.get(0).getDeptCode());
        assertEquals(BatchConstants.CREATE_BY_ADM, rows.get(0).getCreateBy());
        assertEquals(BatchConstants.DELETE_FLAG_NORMAL, rows.get(0).getDeleteFlag());

        assertEquals("分办B", rows.get(1).getOfficeName());
        assertEquals("行政", rows.get(1).getDeptName());
    }

    @Test
    void generate_returnsZeroWhenNoInstance() {
        when(bpmNodeInstanceMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        int count = service.generate(T_DAY, new Date(), SCENE_ID, PREFIX, VERSION);

        assertEquals(0, count);
        verify(bpmNodeInstanceOfficeMapper, never()).batchInsert(any());
    }

    @Test
    void generate_skipsInstanceWithoutMatchingTemplate() {
        BpmNodeInstance instance = new BpmNodeInstance();
        instance.setId(2L);
        instance.setNodeCode("MISSING");
        instance.setDataOrgLevel("总");
        instance.setOrgBranch(BatchConstants.ORG_ALL);
        instance.setOrgCenBranch(BatchConstants.ORG_ALL);
        instance.setOrgBusiBranch(BatchConstants.ORG_ALL);
        when(bpmNodeInstanceMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(instance));
        when(bpmNodeInstanceFormatMapper.selectTemplates(
                any(), anyString(), any(), anyString(), any()))
                .thenReturn(Collections.emptyList());

        int count = service.generate(T_DAY, new Date(), SCENE_ID, PREFIX, VERSION);

        assertEquals(0, count);
        verify(bpmNodeInstanceOfficeMapper, never()).batchInsert(any());
    }
}
