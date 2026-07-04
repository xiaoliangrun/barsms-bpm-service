package com.cpic.barsms.bpm.controller;

import com.cpic.barsms.bpm.common.base.ApiResult;
import com.cpic.barsms.bpm.domain.dto.BatchGenerateRequest;
import com.cpic.barsms.bpm.domain.dto.BatchGenerateResultDTO;
import com.cpic.barsms.bpm.domain.service.BatchGenerateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/batch")
public class BatchGenerateController {

    @Autowired
    private BatchGenerateService batchGenerateService;

    @PostMapping("/generate")
    public ApiResult<BatchGenerateResultDTO> generate(
            @Validated @RequestBody BatchGenerateRequest batchGenerateRequest) {
        log.info("接收批量生成请求, versionName={}, nodeCode={}, tDay={}",
                batchGenerateRequest.getVersionName(), batchGenerateRequest.getNodeCode(),
                batchGenerateRequest.getTDay());
        BatchGenerateResultDTO batchGenerateResultDTO = batchGenerateService.generate(batchGenerateRequest);
        return ApiResult.ok(batchGenerateResultDTO);
    }
}
