package com.cpic.barsms.bpm.domain.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cpic.barsms.bpm.common.constants.BatchConstants;
import com.cpic.barsms.bpm.common.enums.OrgLevelEnum;
import com.cpic.barsms.bpm.common.utils.StringSplitter;
import com.cpic.barsms.bpm.domain.service.OfficeMappingService;
import com.cpic.barsms.bpm.infra.mapper.BpmNodeDepartmentMapper;
import com.cpic.barsms.bpm.infra.mapper.BpmNodeInstanceFormatMapper;
import com.cpic.barsms.bpm.infra.mapper.BpmNodeInstanceMapper;
import com.cpic.barsms.bpm.infra.mapper.BpmNodeInstanceOfficeMapper;
import com.cpic.barsms.bpm.infra.model.entity.BpmNodeInstance;
import com.cpic.barsms.bpm.infra.model.entity.BpmNodeInstanceFormat;
import com.cpic.barsms.bpm.infra.model.entity.BpmNodeInstanceOffice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OfficeMappingServiceImpl extends ServiceImpl<BpmNodeInstanceOfficeMapper, BpmNodeInstanceOffice> implements OfficeMappingService {

    @Autowired
    private BpmNodeInstanceMapper bpmNodeInstanceMapper;
    @Autowired
    private BpmNodeInstanceOfficeMapper bpmNodeInstanceOfficeMapper;
    @Autowired
    private BpmNodeInstanceFormatMapper bpmNodeInstanceFormatMapper;
    @Autowired
    private BpmNodeDepartmentMapper bpmNodeDepartmentMapper;

    @Override
    public int generate(Date tDay, Date tDayBase, Long sceneId, String nodeCodePrefix, String versionName) {
        List<BpmNodeInstance> instances = bpmNodeInstanceMapper.selectList(
                new QueryWrapper<BpmNodeInstance>()
                        .eq("T_DAY", tDay)
                        .eq("SCENE_ID", sceneId)
                        .like(StringUtils.hasText(nodeCodePrefix),
                                "node_code", nodeCodePrefix + "%")
        );

        if (instances.isEmpty()) {
            return 0;
        }

        List<Long> instanceIds = instances.stream()
                .map(BpmNodeInstance::getId).collect(Collectors.toList());
        bpmNodeInstanceOfficeMapper.deleteByInstanceIds(instanceIds);

        List<BpmNodeInstanceFormat> templates = queryAllTemplates(tDayBase, versionName, nodeCodePrefix);
        Map<String, BpmNodeInstanceFormat> templateMap = templates.stream()
                .collect(Collectors.toMap(
                        BpmNodeInstanceFormat::getNodeCode, t -> t, (a, b) -> a));

        List<BpmNodeInstanceOffice> officeList = new ArrayList<>();
        for (BpmNodeInstance instance : instances) {
            BpmNodeInstanceFormat template = templateMap.get(instance.getNodeCode());
            if (template == null) continue;

            int levelIndex = getLevelIndex(instance.getDataOrgLevel());

            String officeNameStr = StringSplitter.splitByPipe(template.getOfficeName(), levelIndex);
            String deptNameStr = StringSplitter.splitByPipe(template.getDeptName(), levelIndex);

            List<String> officeNames = StringSplitter.splitByComma(officeNameStr);
            List<String> deptNames = StringSplitter.splitByComma(deptNameStr);

            int maxSize = Math.max(officeNames.size(), deptNames.size());
            for (int i = 0; i < maxSize; i++) {
                String officeName = i < officeNames.size() ? officeNames.get(i) : null;
                String deptName = i < deptNames.size() ? deptNames.get(i) : null;

                if (!StringUtils.hasText(officeName) && !StringUtils.hasText(deptName)) {
                    continue;
                }

                String officeCode = matchDeptCode(officeName, instance);
                String deptCode = matchDeptCode(deptName, instance);

                BpmNodeInstanceOffice office = new BpmNodeInstanceOffice();
                office.setNodeInstanceId(instance.getId());
                office.setOfficeCode(officeCode);
                office.setOfficeName(officeName);
                office.setDeptCode(deptCode);
                office.setDeptName(deptName);
                office.setCreateBy(BatchConstants.CREATE_BY_ADM);
                office.setCreateTime(new Date());
                office.setUpdateTime(new Date());
                office.setDeleteFlag(BatchConstants.DELETE_FLAG_NORMAL);
                officeList.add(office);
            }
        }

        if (!officeList.isEmpty()) {
            bpmNodeInstanceOfficeMapper.batchInsert(officeList);
            log.info("岗位/部门映射生成 {} 条", officeList.size());
        }

        return officeList.size();
    }

    @Override
    public void markLogicDelete(Date tDay, Long sceneId, String nodeCodePrefix) {
        List<Long> instanceIds = bpmNodeInstanceMapper.selectList(
                new QueryWrapper<BpmNodeInstance>()
                        .eq("T_DAY", tDay)
                        .eq("SCENE_ID", sceneId)
                        .like(StringUtils.hasText(nodeCodePrefix),
                                "node_code", nodeCodePrefix + "%")
        ).stream().map(BpmNodeInstance::getId).collect(Collectors.toList());

        if (instanceIds.isEmpty()) return;

        int updated = bpmNodeInstanceOfficeMapper.markUnmatchedDeleted(instanceIds);
        log.info("未匹配岗位标记删除 {} 条", updated);
    }

    private String matchDeptCode(String deptName, BpmNodeInstance instance) {
        if (!StringUtils.hasText(deptName)) {
            return null;
        }
        return bpmNodeDepartmentMapper.selectDeptCode(
                deptName,
                resolveOrgLevel(instance),
                instance.getOrgBranch(),
                instance.getOrgCenBranch(),
                instance.getOrgBusiBranch()
        );
    }

    private String resolveOrgLevel(BpmNodeInstance instance) {
        if (BatchConstants.ORG_ALL.equals(instance.getOrgBranch())) return OrgLevelEnum.ROOT_COMPANY.getFullName();
        if (BatchConstants.ORG_ALL.equals(instance.getOrgCenBranch())) return OrgLevelEnum.BRANCH_COMPANY.getFullName();
        if (BatchConstants.ORG_ALL.equals(instance.getOrgBusiBranch())) return OrgLevelEnum.CENTER_BRANCH_COMPANY.getFullName();
        return OrgLevelEnum.SUB_BRANCH_COMPANY.getFullName();
    }

    private int getLevelIndex(String shortName) {
        if (OrgLevelEnum.ROOT_COMPANY.getShortName().equals(shortName)) return 0;
        if (OrgLevelEnum.BRANCH_COMPANY.getShortName().equals(shortName)) return 1;
        if (OrgLevelEnum.CENTER_BRANCH_COMPANY.getShortName().equals(shortName)) return 2;
        return 3;
    }

    private List<BpmNodeInstanceFormat> queryAllTemplates(Date tDayBase, String versionName, String nodeCodePrefix) {
        List<BpmNodeInstanceFormat> result = new ArrayList<>();
        result.addAll(bpmNodeInstanceFormatMapper.selectTemplates(tDayBase, versionName, null, "%" + OrgLevelEnum.ROOT_COMPANY.getFullName() + "%", nodeCodePrefix));
        result.addAll(bpmNodeInstanceFormatMapper.selectTemplates(tDayBase, versionName, null, "%" + OrgLevelEnum.BRANCH_COMPANY.getFullName() + "%", nodeCodePrefix));
        result.addAll(bpmNodeInstanceFormatMapper.selectTemplates(tDayBase, versionName, null, "%" + OrgLevelEnum.CENTER_BRANCH_COMPANY.getFullName() + "%", nodeCodePrefix));
        result.addAll(bpmNodeInstanceFormatMapper.selectTemplates(tDayBase, versionName, null, "%" + OrgLevelEnum.SUB_BRANCH_COMPANY.getFullName() + "%", nodeCodePrefix));
        return result;
    }
}
