package com.cpic.barsms.bpm.domain.service;

import java.util.Date;

public interface OfficeMappingService {

    int generate(Date tDay, Date tDayBase, Long sceneId, String nodeCodePrefix, String versionName);

    void markLogicDelete(Date tDay, Long sceneId, String nodeCodePrefix);
}
