package me.sxl.gateway.service.impl;

import me.sxl.gateway.mapper.ReferenceMapper;
import me.sxl.gateway.model.DubboReferenceKey;
import me.sxl.gateway.model.DubboReferenceModel;
import me.sxl.gateway.service.ReferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReferenceServiceImpl implements ReferenceService {

    private ReferenceMapper referenceMapper;

    @Autowired
    public void setReferenceMapper(ReferenceMapper referenceMapper) {
        this.referenceMapper = referenceMapper;
    }

    @Override
    public List<DubboReferenceModel> listReference() {
        return this.referenceMapper.listReference();
    }

    @Override
    public Optional<DubboReferenceModel> getByKey(DubboReferenceKey key) {
        return Optional.ofNullable(this.referenceMapper.getByKey(key));
    }
}
