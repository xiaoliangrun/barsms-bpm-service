package com.cpic.barsms.bpm.domain.serviceimpl;

import com.cpic.barsms.bpm.common.constants.BatchConstants;
import com.cpic.barsms.bpm.common.constants.BatchProperties;
import com.cpic.barsms.bpm.common.enums.OrgLevelEnum;
import com.cpic.barsms.bpm.common.enums.ProcDateTypeEnum;
import com.cpic.barsms.bpm.domain.service.MonthlyTaskGeneratorService;
import com.cpic.barsms.bpm.infra.dto.OrgRelationDTO;
import com.cpic.barsms.bpm.infra.mapper.BpmNodeInstanceFormatMapper;
import com.cpic.barsms.bpm.infra.mapper.BpmNodeInstanceMapper;
import com.cpic.barsms.bpm.infra.mapper.BpmNodeTypeMapper;
import com.cpic.barsms.bpm.infra.mapper.BpmOrgInfoMapper;
import com.cpic.barsms.bpm.infra.model.entity.BpmNodeInstance;
import com.cpic.barsms.bpm.infra.model.entity.BpmNodeInstanceFormat;
import com.cpic.barsms.bpm.infra.model.entity.BpmOrgInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class MonthlyTaskGeneratorServiceImpl implements MonthlyTaskGeneratorService {

    @Autowired
    private BpmNodeInstanceMapper bpmNodeInstanceMapper;
    @Autowired
    private BpmNodeInstanceFormatMapper bpmNodeInstanceFormatMapper;
    @Autowired
    private BpmOrgInfoMapper bpmOrgInfoMapper;
    @Autowired
    private BpmNodeTypeMapper bpmNodeTypeMapper;
    @Autowired
    private BatchProperties batchProperties;

    @Override
    public void generate(Date tDay, Date tDayBase, String versionName, Long sceneId, String nodeCodePrefix) {
        generateHeadquarters(tDay, tDayBase, versionName, sceneId, nodeCodePrefix);
        generateBranch(tDay, tDayBase, versionName, sceneId, nodeCodePrefix);
        generateCenterBranch(tDay, tDayBase, versionName, sceneId, nodeCodePrefix);
        generateSubBranch(tDay, tDayBase, versionName, sceneId, nodeCodePrefix);
    }

    private void generateHeadquarters(Date tDay, Date tDayBase, String versionName,
                                     Long sceneId, String nodeCodePrefix) {
        List<BpmNodeInstanceFormat> templates = bpmNodeInstanceFormatMapper.selectTemplates(
                tDayBase, versionName, ProcDateTypeEnum.MONTHLY, "%" + OrgLevelEnum.ROOT_COMPANY.getFullName() + "%", nodeCodePrefix);

        List<BpmNodeInstance> instances = new ArrayList<>();
        for (BpmNodeInstanceFormat t : templates) {
            instances.add(buildInstance(t, tDay, sceneId,
                    null, null, null, null, null, null));
        }

        if (!instances.isEmpty()) {
            bpmNodeInstanceMapper.batchInsert(instances);
            log.info("总公司月度任务生成 {} 条", instances.size());
        }
    }

    private void generateBranch(Date tDay, Date tDayBase, String versionName,
                                 Long sceneId, String nodeCodePrefix) {
        List<BpmNodeInstanceFormat> templates = bpmNodeInstanceFormatMapper.selectTemplates(
                tDayBase, versionName, ProcDateTypeEnum.MONTHLY, "%" + OrgLevelEnum.BRANCH_COMPANY.getFullName() + "%", nodeCodePrefix);
        List<BpmOrgInfo> branches = bpmOrgInfoMapper.selectBranchList();

        List<BpmNodeInstance> instances = new ArrayList<>();
        for (BpmNodeInstanceFormat t : templates) {
            for (BpmOrgInfo branch : branches) {
                instances.add(buildInstance(t, tDay, sceneId,
                        branch.getOrgCode(), branch.getOrgName(),
                        null, null, null, null));
            }
        }

        if (!instances.isEmpty()) {
            bpmNodeInstanceMapper.batchInsert(instances);
            log.info("分公司月度任务生成 {} 条", instances.size());
        }
    }

    private void generateCenterBranch(Date tDay, Date tDayBase, String versionName,
                                       Long sceneId, String nodeCodePrefix) {
        List<BpmNodeInstanceFormat> templates = bpmNodeInstanceFormatMapper.selectTemplates(
                tDayBase, versionName, ProcDateTypeEnum.MONTHLY, "%" + OrgLevelEnum.CENTER_BRANCH_COMPANY.getFullName() + "%", nodeCodePrefix);
        List<OrgRelationDTO> relations = bpmOrgInfoMapper.selectCenterWithBranch(
                batchProperties.getExcludedOrgCodes());

        List<BpmNodeInstance> instances = new ArrayList<>();
        for (BpmNodeInstanceFormat t : templates) {
            for (OrgRelationDTO rel : relations) {
                instances.add(buildInstance(t, tDay, sceneId,
                        rel.getBranchCode(), rel.getBranchName(),
                        rel.getCenterCode(), rel.getCenterName(),
                        null, null));
            }
        }

        if (!instances.isEmpty()) {
            bpmNodeInstanceMapper.batchInsert(instances);
            log.info("中支月度任务生成 {} 条", instances.size());
        }
    }

    private void generateSubBranch(Date tDay, Date tDayBase, String versionName,
                                    Long sceneId, String nodeCodePrefix) {
        List<BpmNodeInstanceFormat> templates = bpmNodeInstanceFormatMapper.selectTemplates(
                tDayBase, versionName, ProcDateTypeEnum.MONTHLY, "%" + OrgLevelEnum.SUB_BRANCH_COMPANY.getFullName() + "%", nodeCodePrefix);
        List<OrgRelationDTO> relations = bpmOrgInfoMapper.selectSubWithCenterAndBranch();

        List<BpmNodeInstance> instances = new ArrayList<>();
        for (BpmNodeInstanceFormat t : templates) {
            for (OrgRelationDTO rel : relations) {
                instances.add(buildInstance(t, tDay, sceneId,
                        rel.getBranchCode(), rel.getBranchName(),
                        rel.getCenterCode(), rel.getCenterName(),
                        rel.getSubCode(), rel.getSubName()));
            }
        }

        if (!instances.isEmpty()) {
            bpmNodeInstanceMapper.batchInsert(instances);
            log.info("支公司月度任务生成 {} 条", instances.size());
        }
    }

    private BpmNodeInstance buildInstance(BpmNodeInstanceFormat template, Date tDay,
                                          Long sceneId,
                                          String branchCode, String branchName,
                                          String centerCode, String centerName,
                                          String subCode, String subName) {
        BpmNodeInstance instance = new BpmNodeInstance();
        instance.setTDay(tDay);
        instance.setSceneId(sceneId);
        instance.setNodeTypeId(bpmNodeTypeMapper.selectIdByCategoryAndName(
                template.getNodeTypeCategory(), template.getNodeTypeName()));
        instance.setTitle(template.getTitle());
        instance.setStatus(template.getStatus());
        instance.setTimePoint(null);
        instance.setPriority(batchProperties.getDefaultPriority());
        instance.setProgress(BatchConstants.DEFAULT_PROGRESS);
        instance.setDueDate(template.getDueDate());
        instance.setPlannedStart(template.getPlannedStart());
        instance.setPlannedEnd(template.getPlannedEnd());
        instance.setCustomFieldSchemeId(Long.parseLong(batchProperties.getDefaultCustomFieldSchemeId()));
        instance.setFieldSchemeData(null);
        instance.setDescription(template.getDescription());
        instance.setOrgCode(batchProperties.getHeadOfficeCode());
        instance.setOrgName(batchProperties.getHeadOfficeName());
        instance.setOrgBranch(branchCode != null ? branchCode : BatchConstants.ORG_ALL);
        instance.setOrgBranchName(branchName);
        instance.setOrgCenBranch(centerCode != null ? centerCode : BatchConstants.ORG_ALL);
        instance.setOrgCenBranchName(centerName);
        instance.setOrgBusiBranch(subCode != null ? subCode : BatchConstants.ORG_ALL);
        instance.setOrgBusiBranchName(subName);
        instance.setCreateBy(template.getCreateBy());
        instance.setCreateTime(new Date());
        instance.setUpdateBy(template.getUpdateBy());
        instance.setUpdateTime(new Date());
        instance.setChannel(batchProperties.getDefaultChannel());
        instance.setNodeCode(template.getNodeCode());
        instance.setDataOrgLevel(OrgLevelEnum.toShortName(template.getDataOrgLevel()));
        return instance;
    }
}
