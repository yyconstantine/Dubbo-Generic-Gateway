package me.sxl.gateway.mapper;

import me.sxl.gateway.model.DubboReferenceKey;
import me.sxl.gateway.model.DubboReferenceModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReferenceMapper {

    /**
     * 查询所有dubbo接口信息
     * @return dubbo接口信息list
     */
    List<DubboReferenceModel> listReference();

    /**
     * 根据指定key查询dubbo接口信息
     * @param key key
     * @return dubbo接口信息
     */
    DubboReferenceModel getByKey(DubboReferenceKey key);

}
