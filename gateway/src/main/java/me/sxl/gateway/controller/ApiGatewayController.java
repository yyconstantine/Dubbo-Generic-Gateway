package me.sxl.gateway.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.sxl.gateway.config.DubboReferenceConfig;
import me.sxl.gateway.model.ApiGatewayDTO;
import me.sxl.gateway.model.DubboReferenceKey;
import me.sxl.gateway.model.DubboReferenceValue;
import me.sxl.gateway.model.ResponseEntity;
import me.sxl.gateway.model.constant.ErrorEnum;
import me.sxl.gateway.service.ReferenceService;
import me.sxl.gateway.util.DESUtils;
import me.sxl.gateway.util.RequestUtils;
import me.sxl.gateway.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/api")
public class ApiGatewayController {

    @Value("${rsa.key.private}")
    private String privateKey;

    private DubboReferenceConfig dubboReferenceConfig;

    private ReferenceService referenceService;

    @Autowired
    public void setDubboReferenceConfig(DubboReferenceConfig dubboReferenceConfig) {
        this.dubboReferenceConfig = dubboReferenceConfig;
    }

    @Autowired
    public void setReferenceService(ReferenceService referenceService) {
        this.referenceService = referenceService;
    }

    @RequestMapping("/api/{version}/{method}/{uri}")
    @SuppressWarnings("unchecked")
    public String route(@PathVariable String method,
                        @PathVariable String uri,
                        @PathVariable String version,
                        @RequestBody ApiGatewayDTO gatewayDTO,
                        HttpServletRequest request) throws IOException {
        log.info("上传参数: method >> {}, uri >> {}, DTO >> {}", method, uri, gatewayDTO);

        // 这里做解密过程
        String[] paramsAndDesKey = RequestUtils.decode2ParamsAndDesKey(gatewayDTO, privateKey);
        String reqParams = paramsAndDesKey[0];
        String desKey = paramsAndDesKey[1];

        if (StringUtils.isEmpty(reqParams) || StringUtils.isEmpty(desKey)) {
            log.error("参数解析错误");
            return ResponseEntity.error(ErrorEnum.REQ_DECODE_ERROR).toString();
        }
        log.info("参数解析结果为: {}", reqParams);

        // 将des-key传入请求上下文,用于ExceptionHandler拦截异常时进行加密,不太优雅的实现
        request.setAttribute("DES_KEY", desKey);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = new HashMap<>();
        try {
            map = mapper.readValue(reqParams, Map.class);
        } catch (JsonProcessingException e) {
            log.error("参数序列化解析出错: ", e);
        }

        return this.generic(uri, method, version, map, desKey);
    }

    private String generic(String reqUri, String reqMethod, String version, Map<String, Object> map, String desKey) throws IOException {
        // 根据uri和method唯一确定一个dubbo泛化接口
        Optional<DubboReferenceValue> referenceOpt = this.referenceService.findDubboReferenceBy(DubboReferenceKey
                .builder()
                .reqUri(reqUri)
                .reqMethod(reqMethod)
                .version(version)
                .build());

        if (!referenceOpt.isPresent()) {
            log.warn("上送路径不存在: {}", reqMethod + "/" + reqUri);
            return DESUtils.encrypt(ResponseUtils.writeResultValue2JsonString(ResponseEntity.error(ErrorEnum.PATH_NOT_FOUND)),
                    desKey);
        }

        String rtValue = this.referenceService.invoke(map, referenceOpt.get());
        log.info("$invoke(return): {}", rtValue);

        // 将返回参数进行包装(des加密)
        return DESUtils.encrypt(rtValue, desKey);
    }

}
