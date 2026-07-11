package com.cpic.barsms.bpm.domain.service;

import java.util.Date;

public interface MonthlyTaskGeneratorService {

    void generate(Date tDay, Date tDayBase, String versionName, Long sceneId, String nodeCodePrefix);
}
