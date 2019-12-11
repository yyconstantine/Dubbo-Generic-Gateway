package me.sxl.gateway.service;

import me.sxl.gateway.model.DubboReferenceKey;
import me.sxl.gateway.model.DubboReferenceModel;

import java.util.List;
import java.util.Optional;

public interface ReferenceService {

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
    Optional<DubboReferenceModel> getByKey(DubboReferenceKey key);

}
