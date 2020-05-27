package me.sxl.gateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import me.sxl.gateway.model.ResponseEntity;
import me.sxl.gateway.model.constant.ErrorEnum;
import me.sxl.gateway.util.DESUtils;
import me.sxl.gateway.util.ResponseUtils;
import org.apache.dubbo.rpc.service.GenericException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yyconstantine
 * @date 2019/11/21 18:00
 */
@RestControllerAdvice
@Slf4j
public class GatewayExceptionHandler {

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public String handleHttpRequestMethodNotSupportedException(HttpServletRequest request, HttpRequestMethodNotSupportedException e) throws JsonProcessingException {
        log.error("请求方法{}不支持", e.getMethod());
        return DESUtils.encrypt(ResponseUtils.writeResultValue2JsonString(ResponseEntity.error(ErrorEnum.METHOD_NOT_SUPPORT)), (String) request.getAttribute("DES_KEY"));
    }

    @ExceptionHandler(GenericException.class)
    public String handleGenericException(HttpServletRequest request, GenericException e) throws JsonProcessingException {
        log.error("服务调用异常: ", e);
        return DESUtils.encrypt(ResponseUtils.writeResultValue2JsonString(ResponseEntity.error(ErrorEnum.UNKNOWN)), (String) request.getAttribute("DES_KEY"));
    }

    @ExceptionHandler(Exception.class)
    public String handleException(HttpServletRequest request, Exception e) throws JsonProcessingException {
        log.error(e.getMessage(), e);
        return DESUtils.encrypt(ResponseUtils.writeResultValue2JsonString(ResponseEntity.error(ErrorEnum.UNKNOWN)), (String) request.getAttribute("DES_KEY"));
    }

}
