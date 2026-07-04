package com.cpic.barsms.bpm.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cpic.barsms.bpm.infra.model.entity.BpmNodeInstanceOffice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BpmNodeInstanceOfficeMapper extends BaseMapper<BpmNodeInstanceOffice> {

    int deleteByInstanceIds(@Param("ids") List<Long> ids);

    int batchInsert(@Param("list") List<BpmNodeInstanceOffice> list);

    int markUnmatchedDeleted(@Param("instanceIds") List<Long> instanceIds);
}
