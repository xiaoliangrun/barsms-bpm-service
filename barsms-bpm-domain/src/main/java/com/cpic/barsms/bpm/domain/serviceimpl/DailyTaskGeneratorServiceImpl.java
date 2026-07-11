package com.cpic.barsms.bpm.domain.serviceimpl;

import com.cpic.barsms.bpm.common.constants.BatchConstants;
import com.cpic.barsms.bpm.common.constants.BatchProperties;
import com.cpic.barsms.bpm.common.enums.OrgLevelEnum;
import com.cpic.barsms.bpm.common.enums.ProcDateTypeEnum;
import com.cpic.barsms.bpm.domain.service.DailyTaskGeneratorService;
import com.cpic.barsms.bpm.infra.dto.OrgRelationDTO;
import com.cpic.barsms.bpm.infra.mapper.BpmDimCalendarMapper;
import com.cpic.barsms.bpm.infra.mapper.BpmNodeInstanceFormatMapper;
import com.cpic.barsms.bpm.infra.mapper.BpmNodeInstanceMapper;
import com.cpic.barsms.bpm.infra.mapper.BpmNodeTypeMapper;
import com.cpic.barsms.bpm.infra.mapper.BpmOrgInfoMapper;
import com.cpic.barsms.bpm.infra.model.entity.BpmDimCalendar;
import com.cpic.barsms.bpm.infra.model.entity.BpmNodeInstance;
import com.cpic.barsms.bpm.infra.model.entity.BpmNodeInstanceFormat;
import com.cpic.barsms.bpm.infra.model.entity.BpmOrgInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class DailyTaskGeneratorServiceImpl implements DailyTaskGeneratorService {

    @Autowired
    private BpmNodeInstanceMapper bpmNodeInstanceMapper;
    @Autowired
    private BpmNodeInstanceFormatMapper bpmNodeInstanceFormatMapper;
    @Autowired
    private BpmOrgInfoMapper bpmOrgInfoMapper;
    @Autowired
    private BpmDimCalendarMapper bpmDimCalendarMapper;
    @Autowired
    private BpmNodeTypeMapper bpmNodeTypeMapper;
    @Autowired
    private BatchProperties batchProperties;

    @Override
    public void generate(Date tDay, Date tDayBase, String versionName, Long sceneId, String nodeCodePrefix) {
        Date lastDay = getLastDayOfMonth(tDay);
        List<BpmDimCalendar> calendars = bpmDimCalendarMapper.selectByDateRange(tDay, lastDay);

        generateHeadquartersDaily(tDay, tDayBase, versionName, sceneId, nodeCodePrefix, calendars);
        generateBranchDaily(tDay, tDayBase, versionName, sceneId, nodeCodePrefix, calendars);
        generateCenterBranchDaily(tDay, tDayBase, versionName, sceneId, nodeCodePrefix, calendars);
        generateSubBranchDaily(tDay, tDayBase, versionName, sceneId, nodeCodePrefix, calendars);
    }

    /**
     * 生成总公司每日任务实例
     * @param tDay 任务基准日期
     * @param tDayBase 模板查询基准日期
     * @param versionName 版本名称
     * @param sceneId 场景ID
     * @param nodeCodePrefix 节点编码前缀
     * @param calendars 日历维度列表
     */
    private void generateHeadquartersDaily(Date tDay, Date tDayBase, String versionName,
                                            Long sceneId, String nodeCodePrefix,
                                            List<BpmDimCalendar> calendars) {
        List<BpmNodeInstanceFormat> templates = bpmNodeInstanceFormatMapper.selectTemplates(
                tDayBase, versionName, ProcDateTypeEnum.DAILY, "%" + OrgLevelEnum.ROOT_COMPANY.getFullName() + "%", nodeCodePrefix);

        List<BpmNodeInstance> nodeInstanceList = new ArrayList<>();
        for (BpmNodeInstanceFormat t : templates) {
            for (BpmDimCalendar cal : calendars) {
                Date plannedDate = addDays(tDay, cal.getDay() - 1);
                nodeInstanceList.add(buildDailyInstance(t, tDay, sceneId, plannedDate,
                        null, null, null, null, null, null));
            }
        }

        if (!nodeInstanceList.isEmpty()) {
            bpmNodeInstanceMapper.batchInsert(nodeInstanceList);
            log.info("总公司每日任务生成 {} 条", nodeInstanceList.size());
        }
    }

    /**
     * 生成分公司每日任务实例
     * @param tDay 任务基准日期
     * @param tDayBase 模板查询基准日期
     * @param versionName 版本名称
     * @param sceneId 场景ID
     * @param nodeCodePrefix 节点编码前缀
     * @param calendars 日历维度列表
     */
    private void generateBranchDaily(Date tDay, Date tDayBase, String versionName,
                                      Long sceneId, String nodeCodePrefix,
                                      List<BpmDimCalendar> calendars) {
        List<BpmNodeInstanceFormat> templates = bpmNodeInstanceFormatMapper.selectTemplates(
                tDayBase, versionName, ProcDateTypeEnum.DAILY, "%" + OrgLevelEnum.BRANCH_COMPANY.getFullName() + "%", nodeCodePrefix);
        List<BpmOrgInfo> branches = bpmOrgInfoMapper.selectBranchList();

        List<BpmNodeInstance> instances = new ArrayList<>();
        for (BpmNodeInstanceFormat t : templates) {
            for (BpmOrgInfo branch : branches) {
                for (BpmDimCalendar cal : calendars) {
                    Date plannedDate = addDays(tDay, cal.getDay() - 1);
                    instances.add(buildDailyInstance(t, tDay, sceneId, plannedDate,
                            branch.getOrgCode(), branch.getOrgName(),
                            null, null, null, null));
                }
            }
        }

        if (!instances.isEmpty()) {
            bpmNodeInstanceMapper.batchInsert(instances);
            log.info("分公司每日任务生成 {} 条", instances.size());
        }
    }

    /**
     * 生成中支每日任务实例
     * @param tDay 任务基准日期
     * @param tDayBase 模板查询基准日期
     * @param versionName 版本名称
     * @param sceneId 场景ID
     * @param nodeCodePrefix 节点编码前缀
     * @param calendars 日历维度列表
     */
    private void generateCenterBranchDaily(Date tDay, Date tDayBase, String versionName,
                                            Long sceneId, String nodeCodePrefix,
                                            List<BpmDimCalendar> calendars) {
        List<BpmNodeInstanceFormat> templates = bpmNodeInstanceFormatMapper.selectTemplates(
                tDayBase, versionName, ProcDateTypeEnum.DAILY, "%" + OrgLevelEnum.CENTER_BRANCH_COMPANY.getFullName() + "%", nodeCodePrefix);
        List<OrgRelationDTO> relations = bpmOrgInfoMapper.selectCenterWithBranch(
                batchProperties.getExcludedOrgCodes());

        List<BpmNodeInstance> instances = new ArrayList<>();
        for (BpmNodeInstanceFormat t : templates) {
            for (OrgRelationDTO rel : relations) {
                for (BpmDimCalendar cal : calendars) {
                    Date plannedDate = addDays(tDay, cal.getDay() - 1);
                    instances.add(buildDailyInstance(t, tDay, sceneId, plannedDate,
                            rel.getBranchCode(), rel.getBranchName(),
                            rel.getCenterCode(), rel.getCenterName(),
                            null, null));
                }
            }
        }

        if (!instances.isEmpty()) {
            bpmNodeInstanceMapper.batchInsert(instances);
            log.info("中支每日任务生成 {} 条", instances.size());
        }
    }

    /**
     * 生成支公司每日任务实例
     * @param tDay 任务基准日期
     * @param tDayBase 模板查询基准日期
     * @param versionName 版本名称
     * @param sceneId 场景ID
     * @param nodeCodePrefix 节点编码前缀
     * @param calendars 日历维度列表
     */
    private void generateSubBranchDaily(Date tDay, Date tDayBase, String versionName,
                                         Long sceneId, String nodeCodePrefix,
                                         List<BpmDimCalendar> calendars) {
        List<BpmNodeInstanceFormat> templates = bpmNodeInstanceFormatMapper.selectTemplates(
                tDayBase, versionName, ProcDateTypeEnum.DAILY, "%" + OrgLevelEnum.SUB_BRANCH_COMPANY.getFullName() + "%", nodeCodePrefix);
        List<OrgRelationDTO> relations = bpmOrgInfoMapper.selectSubWithCenterAndBranch();

        List<BpmNodeInstance> instances = new ArrayList<>();
        for (BpmNodeInstanceFormat t : templates) {
            for (OrgRelationDTO rel : relations) {
                for (BpmDimCalendar cal : calendars) {
                    Date plannedDate = addDays(tDay, cal.getDay() - 1);
                    instances.add(buildDailyInstance(t, tDay, sceneId, plannedDate,
                            rel.getBranchCode(), rel.getBranchName(),
                            rel.getCenterCode(), rel.getCenterName(),
                            rel.getSubCode(), rel.getSubName()));
                }
            }
        }

        if (!instances.isEmpty()) {
            bpmNodeInstanceMapper.batchInsert(instances);
            log.info("支公司每日任务生成 {} 条", instances.size());
        }
    }

    private BpmNodeInstance buildDailyInstance(BpmNodeInstanceFormat template, Date tDay,
                                               Long sceneId, Date plannedDate,
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
        instance.setPlannedStart(plannedDate);
        instance.setPlannedEnd(plannedDate);
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

    /**
     * 日期加减计算
     *
     * @param date 基准日期
     * @param days 要增加的天数，负数表示往前推
     * @return 计算后的日期
     */
    private Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }

    /**
     * 获取当月最后一天的下一天（即下月第一天零点）
     * @param date 输入日期
     * @return 下月第一天零点日期
     */
    private Date getLastDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }
}
