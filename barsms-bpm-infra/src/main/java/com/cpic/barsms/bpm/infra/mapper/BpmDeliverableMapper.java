package com.cpic.barsms.bpm.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cpic.barsms.bpm.infra.model.entity.BpmDeliverable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BpmDeliverableMapper extends BaseMapper<BpmDeliverable> {

    int batchInsert(@Param("list") List<BpmDeliverable> list);
}
