package me.sxl.gateway.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.sxl.gateway.model.ResponseEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yyconstantine
 * @date 2019/11/21 13:23
 */
@Slf4j
@SuppressWarnings("unchecked")
public class ResponseUtils {

    /**
     * 定义需要去除的信息
     */
    private static final String NEED_REMOVE_FIELD = "class";

    /**
     * 去除dubbo泛化调用返回的result最外层的null值及所有的class信息
     * @param result $invoke result
     * @return json格式的字符串
     * @throws IOException jackson序列化异常
     */
    public static String writeObjectValue2JsonString(Object result) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = (Map<String, Object>) removeClassName(result);
        Map<String, Object> resultMap = new HashMap<>(map);
        for (String key : map.keySet()) {
            if (map.get(key) == null) {
                resultMap.remove(key);
            }
        }
        return objectMapper.writeValueAsString(resultMap);
    }

    /**
     * 递归删除class信息
     * @param result 传入的$invoke result
     * @return 删除class信息后的json格式出参,未处理null值
     */
    private static Object removeClassName(Object result) {
        if (result instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) result;
            List<String> keyList = new ArrayList();
            for (String key : map.keySet()) {
                if (key.equals(NEED_REMOVE_FIELD)) {
                    keyList.add(key);
                }
                Object value = map.get(key);
                if (value instanceof Map) {
                    map.put(key, removeClassName(value));
                }
                if (value instanceof List) {
                    map.put(key, removeClassName(value));
                }
            }
            for (String key : keyList) {
                map.remove(key);
            }
            return map;
        }

        if (result instanceof List) {
            List<Object> list = (List<Object>) result;
            for (int i = 0; i < list.size(); i++) {
                list.set(i, removeClassName(list.get(i)));
            }
            return list;
        }

        return null;
    }

    /**
     * 封装jackson的序列化方法,将Result去null返回
     * @param result 响应vo
     * @return 去null的vo
     * @throws JsonProcessingException
     */
    public static String writeResultValue2JsonString(ResponseEntity result) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper.writeValueAsString(result);
    }

}