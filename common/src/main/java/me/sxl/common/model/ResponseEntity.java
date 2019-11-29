package me.sxl.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.sxl.common.constant.ErrorEnum;
import me.sxl.common.constant.OkEnum;

@Data
@AllArgsConstructor
public class ResponseEntity<T> {

    private int code;

    private String msg;

    private T data;

    public static <T> ResponseEntity<T> ok(int code, String msg, T data) {
        return new ResponseEntity<>(code, msg, data);
    }

    public static <T> ResponseEntity<T> ok(OkEnum okEnum) {
        return new ResponseEntity<>(okEnum.getCode(), okEnum.getMsg(), null);
    }

    public static <T> ResponseEntity<T> ok(OkEnum okEnum, T data) {
        return new ResponseEntity<>(okEnum.getCode(), okEnum.getMsg(), data);
    }

    public static <T> ResponseEntity<T> error(int code, String msg) {
        return new ResponseEntity<>(code, msg, null);
    }

    public static <T> ResponseEntity<T> error(int code, String msg, T data) {
        return new ResponseEntity<>(code, msg, data);
    }

    public static <T> ResponseEntity<T> error(ErrorEnum errorEnum) {
        return new ResponseEntity<>(errorEnum.getCode(), errorEnum.getMsg(), null);
    }

}
