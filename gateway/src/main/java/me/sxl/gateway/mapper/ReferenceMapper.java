package me.sxl.gateway.mapper;

import me.sxl.gateway.model.DubboReferenceModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReferenceMapper {

    List<DubboReferenceModel> dubboReferenceList();

}
