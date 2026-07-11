package com.cpic.barsms.bpm.domain.serviceimpl;

import com.cpic.barsms.bpm.common.constants.BatchConstants;
import com.cpic.barsms.bpm.common.constants.BatchProperties;
import com.cpic.barsms.bpm.common.enums.OrgLevelEnum;
import com.cpic.barsms.bpm.common.enums.ProcDateTypeEnum;
import com.cpic.barsms.bpm.infra.dto.OrgRelationDTO;
import com.cpic.barsms.bpm.infra.mapper.BpmNodeInstanceFormatMapper;
import com.cpic.barsms.bpm.infra.mapper.BpmNodeInstanceMapper;
import com.cpic.barsms.bpm.infra.mapper.BpmNodeTypeMapper;
import com.cpic.barsms.bpm.infra.mapper.BpmOrgInfoMapper;
import com.cpic.barsms.bpm.infra.model.entity.BpmNodeInstance;
import com.cpic.barsms.bpm.infra.model.entity.BpmNodeInstanceFormat;
import com.cpic.barsms.bpm.infra.model.entity.BpmOrgInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MonthlyTaskGeneratorServiceImplTest {

    private static final Date T_DAY = new Date();
    private static final Date T_DAY_BASE = new Date(T_DAY.getTime() - 86400000L);
    private static final Long SCENE_ID = 7L;
    private static final String VERSION = "v1";
    private static final String PREFIX = "";

    @Mock private BpmNodeInstanceMapper bpmNodeInstanceMapper;
    @Mock private BpmNodeInstanceFormatMapper bpmNodeInstanceFormatMapper;
    @Mock private BpmOrgInfoMapper bpmOrgInfoMapper;
    @Mock private BpmNodeTypeMapper bpmNodeTypeMapper;

    private final BatchProperties batchProperties = new BatchProperties();

    @InjectMocks
    private MonthlyTaskGeneratorServiceImpl service;

    @BeforeEach
    void setUp() {
        batchProperties.setHeadOfficeCode("00000000000000");
        batchProperties.setHeadOfficeName("集团总公司");
        batchProperties.setDefaultPriority("高");
        batchProperties.setDefaultChannel("PAA");
        batchProperties.setDefaultCustomFieldSchemeId("1");
        ReflectionTestUtils.setField(service, "batchProperties", batchProperties);
    }

    @Test
    void generate_producesCartesianProductPerOrgLevel() {
        BpmNodeInstanceFormat hqTemplate = template("HQ_CODE", "总公司");
        BpmNodeInstanceFormat branchTemplate = template("BR_CODE", "分公司");

        // 根据机构层级 like 参数返回不同模板
        when(bpmNodeInstanceFormatMapper.selectTemplates(
                eq(T_DAY_BASE), eq(VERSION), eq(ProcDateTypeEnum.MONTHLY), anyString(), eq(PREFIX)))
                .thenAnswer(inv -> {
                    String like = inv.getArgument(3);
                    if (like.contains(OrgLevelEnum.ROOT_COMPANY.getFullName())) {
                        return Collections.singletonList(hqTemplate);
                    }
                    if (like.contains(OrgLevelEnum.BRANCH_COMPANY.getFullName())) {
                        return Collections.singletonList(branchTemplate);
                    }
                    return Collections.emptyList();
                });

        BpmOrgInfo branch1 = org("B001", "北京分公司");
        BpmOrgInfo branch2 = org("B002", "上海分公司");
        when(bpmOrgInfoMapper.selectBranchList()).thenReturn(Arrays.asList(branch1, branch2));
        when(bpmNodeTypeMapper.selectIdByCategoryAndName(anyString(), anyString())).thenReturn(55L);

        service.generate(T_DAY, T_DAY_BASE, VERSION, SCENE_ID, PREFIX);

        ArgumentCaptor<List<BpmNodeInstance>> captor = ArgumentCaptor.forClass(List.class);
        verify(bpmNodeInstanceMapper, times(2)).batchInsert(captor.capture());

        List<List<BpmNodeInstance>> batches = captor.getAllValues();
        // 第一批：总公司 1 条
        List<BpmNodeInstance> hqBatch = batches.get(0);
        assertEquals(1, hqBatch.size());
        BpmNodeInstance hqInstance = hqBatch.get(0);
        assertEquals(SCENE_ID, hqInstance.getSceneId());
        assertEquals("HQ_CODE", hqInstance.getNodeCode());
        assertEquals("高", hqInstance.getPriority());
        assertEquals("PAA", hqInstance.getChannel());
        assertEquals(Long.valueOf(1L), hqInstance.getCustomFieldSchemeId());
        assertEquals(BatchConstants.ORG_ALL, hqInstance.getOrgBranch());
        assertEquals(BatchConstants.ORG_ALL, hqInstance.getOrgCenBranch());
        assertEquals(BatchConstants.ORG_ALL, hqInstance.getOrgBusiBranch());
        assertEquals("总", hqInstance.getDataOrgLevel());

        // 第二批：分公司 1 模板 × 2 分公司 = 2 条
        List<BpmNodeInstance> branchBatch = batches.get(1);
        assertEquals(2, branchBatch.size());
        BpmNodeInstance b1 = branchBatch.get(0);
        assertEquals("B001", b1.getOrgBranch());
        assertEquals("北京分公司", b1.getOrgBranchName());
        assertEquals(BatchConstants.ORG_ALL, b1.getOrgCenBranch());
        assertEquals("分", b1.getDataOrgLevel());

        // 中支/支公司无模板，relations 即便被预取也是空，不应产生额外批次
        verify(bpmNodeInstanceMapper, times(2)).batchInsert(any());
    }

    @Test
    void generate_skipsBatchInsertWhenNoTemplate() {
        when(bpmNodeInstanceFormatMapper.selectTemplates(
                any(), any(), any(), anyString(), any())).thenReturn(Collections.emptyList());

        service.generate(T_DAY, T_DAY_BASE, VERSION, SCENE_ID, PREFIX);

        verify(bpmNodeInstanceMapper, never()).batchInsert(any());
    }

    private BpmNodeInstanceFormat template(String nodeCode, String dataOrgLevel) {
        BpmNodeInstanceFormat t = new BpmNodeInstanceFormat();
        t.setNodeCode(nodeCode);
        t.setTitle("title-" + nodeCode);
        t.setStatus("OPEN");
        t.setNodeTypeCategory("cat");
        t.setNodeTypeName("name");
        t.setDataOrgLevel(dataOrgLevel);
        t.setDescription("desc");
        return t;
    }

    private BpmOrgInfo org(String code, String name) {
        BpmOrgInfo o = new BpmOrgInfo();
        o.setOrgCode(code);
        o.setOrgName(name);
        return o;
    }
}
