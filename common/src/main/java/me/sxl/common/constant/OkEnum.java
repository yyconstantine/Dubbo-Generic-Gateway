package me.sxl.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OkEnum {

    GLOBAL_SEARCH_OK(200, "查询成功"),
    GLOBAL_INSERT_OK(204, "增加成功"),
    GLOBAL_UPDATE_OK(204, "修改成功"),
    GLOBAL_DELETE_OK(204, "删除成功");

    private int code;

    private String msg;

}
