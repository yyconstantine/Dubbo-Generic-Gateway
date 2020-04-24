package me.sxl.order.api.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.sxl.common.constant.ErrorEnum;
import me.sxl.common.constant.OkEnum;
import me.sxl.common.model.ResponseEntity;

/**
 * @author songxianglong
 * @date 2020/4/24 下午 4:55
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderServiceRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -9219931258109272254L;

    private Integer code;

    private String msg;

    public OrderServiceRuntimeException(OkEnum successEnum) {
        super(successEnum.getMsg());
        this.code = successEnum.getCode();
        this.msg = successEnum.getMsg();
    }

    public OrderServiceRuntimeException(ErrorEnum errorEnum) {
        super(errorEnum.getMsg());
        this.code = errorEnum.getCode();
        this.msg = errorEnum.getMsg();
    }

    public OrderServiceRuntimeException(ResponseEntity result) {
        super(result.getMsg());
        this.code = result.getCode();
        this.msg = result.getMsg();
    }

    /**
     * 不打印异常堆栈信息
     * @return
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }

    @Override
    public String toString() {
        return "订单服务异常: {" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
