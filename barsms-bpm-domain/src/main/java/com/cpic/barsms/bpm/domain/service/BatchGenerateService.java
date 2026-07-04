package com.cpic.barsms.bpm.domain.service;

import com.cpic.barsms.bpm.domain.dto.BatchGenerateRequest;
import com.cpic.barsms.bpm.domain.dto.BatchGenerateResultDTO;

public interface BatchGenerateService {

    BatchGenerateResultDTO generate(BatchGenerateRequest batchGenerateRequest);
}
