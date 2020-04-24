package me.sxl.gateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import me.sxl.common.constant.ErrorEnum;
import me.sxl.common.model.ResponseEntity;
import me.sxl.common.utils.DESUtils;
import me.sxl.gateway.util.ResponseUtil;
import org.apache.dubbo.rpc.service.GenericException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

/**
 * @author songxianglong
 * @date 2019/11/21 18:00
 */
@RestControllerAdvice
@Slf4j
public class GatewayExceptionHandler {

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public String handleHttpRequestMethodNotSupportedException(HttpServletRequest request, HttpRequestMethodNotSupportedException e) throws JsonProcessingException {
        log.error("请求方法{}不支持", e.getMethod());
        return DESUtils.encrypt(ResponseUtil.writeResultValue2JsonString(ResponseEntity.error(ErrorEnum.METHOD_NOT_SUPPORT)), (String) request.getAttribute("DES_KEY"));
    }

    @ExceptionHandler(GenericException.class)
    public String handleGenericException(HttpServletRequest request, GenericException e) throws JsonProcessingException {
        log.error("泛化调用异常: ", e);
        return DESUtils.encrypt(ResponseUtil.writeResultValue2JsonString(ResponseEntity.error(ErrorEnum.UNKNOWN)), (String) request.getAttribute("DES_KEY"));
    }

    @ExceptionHandler(Exception.class)
    public String handleException(HttpServletRequest request, Exception e) throws JsonProcessingException {
        log.error(e.getMessage(), e);
        return DESUtils.encrypt(ResponseUtil.writeResultValue2JsonString(ResponseEntity.error(ErrorEnum.UNKNOWN)), (String) request.getAttribute("DES_KEY"));
    }

}
