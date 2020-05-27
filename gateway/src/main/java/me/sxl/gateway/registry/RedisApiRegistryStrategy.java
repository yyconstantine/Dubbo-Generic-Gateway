package me.sxl.gateway.registry;

import me.sxl.gateway.model.constant.Constants;
import me.sxl.gateway.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author yyconstantine
 * @date 2020/5/27 上午 9:31
 */
@Service
public class RedisApiRegistryStrategy extends ApiResourceLoader implements ApiRegistryStrategy {

    private RedisUtils redisUtils;

    @Autowired
    public void setRedisUtils(RedisUtils redisUtils) {
        this.redisUtils = redisUtils;
    }

    @Override
    public void registry(Object bean, String beanName) {

        Map<Object, Object> apis = this.getApis();

        Map<Object, Object> originApis = this.redisUtils.hmget(Constants.DUBBO_APIS_IDX_PREFIX);
        if (originApis != null && originApis.size() > 0) {
            apis.putAll(originApis);
        }
        this.redisUtils.hmset(Constants.DUBBO_APIS_IDX_PREFIX, apis);

        // pipeline操作,减少client和server多次通讯的开销(pipeline是基于client的缓存)
            /*this.redisUtil.executePipelined((RedisCallback<Map<String, DubboMethodModel>>) connection -> {
                connection.openPipeline();
                for (Map.Entry<String, DubboMethodModel> entry : apis.entrySet()) {
                    // 以hashcode值作为key进行key-value存储
                    connection.set((Constants.DUBBO_APIS_IDX_PREFIX + entry.getKey()).getBytes(), JSON.toJSONString
                    (entry.getValue()).getBytes());
                }
                return null;
            });*/
    }
}
