package com.cpic.barsms.bpm.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cpic.barsms.bpm.infra.model.entity.BpmNodeType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BpmNodeTypeMapper extends BaseMapper<BpmNodeType> {

    Long selectIdByCategoryAndName(@Param("category") String category,
                                   @Param("typeName") String typeName);

    List<Long> selectIdsByTypeKey(@Param("typeKey") String typeKey);
}
