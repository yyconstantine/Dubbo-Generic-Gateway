package me.sxl.order.handler;

import lombok.extern.slf4j.Slf4j;
import me.sxl.gateway.model.ResponseEntity;
import me.sxl.gateway.model.constant.ErrorEnum;
import me.sxl.order.api.exception.OrderServiceRuntimeException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolationException;

/**
 * @author yyconstantine
 * @date 2020/4/24 下午 4:54
 */
@Component
@Aspect
@Slf4j
public class OrderExceptionHandler {

    @Pointcut("execution(public * me.sxl.order.facade..*.*(..))")
    public void exceptionPointCut() {}

    @Around("exceptionPointCut()")
    public Object handleException(ProceedingJoinPoint pjp) throws Throwable {
        ResponseEntity result;
        try {
            result = (ResponseEntity) pjp.proceed();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result = handlePartnerException(pjp, e);
        }
        return result;
    }

    private ResponseEntity handlePartnerException(ProceedingJoinPoint pjp, Exception e) {
        if (e instanceof OrderServiceRuntimeException) {
            // 自定义异常捕获
            OrderServiceRuntimeException orderServiceRuntimeException = (OrderServiceRuntimeException) e;
            return ResponseEntity.error(orderServiceRuntimeException.getCode(), orderServiceRuntimeException.getMessage());
        }
        if (e instanceof ConstraintViolationException) {
            // 参数异常捕获
            ConstraintViolationException constraintViolationException = (ConstraintViolationException) e;
            return ResponseEntity.error(400, constraintViolationException.getConstraintViolations().iterator().next().getMessage());
        }
        log.error("订单服务异常: " + e);
        return ResponseEntity.error(ErrorEnum.UNKNOWN);
    }

}
