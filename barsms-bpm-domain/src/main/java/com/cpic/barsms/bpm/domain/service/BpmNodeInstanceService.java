package com.cpic.barsms.bpm.domain.service;

import java.util.Date;

public interface BpmNodeInstanceService {

    int deleteByTargetMonth(Date tDay, Long sceneId, String nodeCodePrefix);

    int countByTargetMonth(Date tDay, Long sceneId, String nodeCodePrefix);
}
