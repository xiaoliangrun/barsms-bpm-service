package com.cpic.barsms.bpm.domain.service;

import com.cpic.barsms.bpm.common.exception.BizBatchException;

public interface BpmSceneService {

    Long selectIdByName(String sceneName);

    Long resolveSceneId(String versionName);
}
