package com.cpic.barsms.bpm.domain.service;

import java.util.Date;

public interface ReferenceNodeService {

    void updateReadOnlyStatus(Date tDay, Long sceneId, String nodeCodePrefix);

    void updateReferenceNodes(Date tDay, Long sceneId, String nodeCodePrefix);
}
