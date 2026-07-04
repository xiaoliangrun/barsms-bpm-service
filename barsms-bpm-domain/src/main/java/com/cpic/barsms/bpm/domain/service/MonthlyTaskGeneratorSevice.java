package com.cpic.barsms.bpm.domain.service;

import com.cpic.barsms.bpm.common.enums.ProcDateTypeEnum;

import java.util.Date;

public interface MonthlyTaskGeneratorSevice {

    void generate(Date tDay, Date tDayBase, String versionName, Long sceneId, String nodeCodePrefix);
}
