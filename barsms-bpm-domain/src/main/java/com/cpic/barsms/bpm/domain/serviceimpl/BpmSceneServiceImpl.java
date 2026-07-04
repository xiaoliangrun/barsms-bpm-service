package com.cpic.barsms.bpm.domain.serviceimpl;

import com.cpic.barsms.bpm.common.exception.BizBatchException;
import com.cpic.barsms.bpm.domain.service.BpmSceneService;
import com.cpic.barsms.bpm.infra.mapper.BpmSceneMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BpmSceneServiceImpl implements BpmSceneService {

    @Autowired
    private BpmSceneMapper bpmSceneMapper;

    @Override
    public Long selectIdByName(String sceneName) {
        return bpmSceneMapper.selectIdByName(sceneName);
    }

    @Override
    public Long resolveSceneId(String versionName) {
        String sceneName = versionName.replace("模版", "");
        Long sceneId = bpmSceneMapper.selectIdByName(sceneName);
        if (sceneId == null) {
            throw new BizBatchException("场景不存在: " + sceneName);
        }
        return sceneId;
    }
}
