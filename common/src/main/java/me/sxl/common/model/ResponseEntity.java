package me.sxl.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import me.sxl.common.constant.ErrorEnum;
import me.sxl.common.constant.OkEnum;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    public static <T> ResponseEntity<T> error(int code, String msg, T data) {
        return new ResponseEntity<>(code, msg, data);
    }

    public static <T> ResponseEntity<T> error(ErrorEnum errorEnum) {
        return new ResponseEntity<>(errorEnum.getCode(), errorEnum.getMsg(), null);
    }

}