package me.sxl.gateway.registry;

import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import me.sxl.gateway.annotation.DubboApi;
import me.sxl.gateway.config.PropertyConfig;
import me.sxl.gateway.model.DubboReferenceModel;
import me.sxl.gateway.model.constant.Constants;
import me.sxl.gateway.util.ClassNameUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author yyconstantine
 * @date 2020/5/27 上午 11:41
 */
@Service
public class ApiResourceLoader implements BeanPostProcessor {

    private Map<Object, Object> apis = Maps.newConcurrentMap();

    protected PropertyConfig propertyConfig;

    @Autowired
    public void setPropertyConfig(PropertyConfig propertyConfig) {
        this.propertyConfig = propertyConfig;
    }

    @SneakyThrows
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (StringUtils.isEmpty(this.propertyConfig.getScanPackages()) || "null".equals(this.propertyConfig.getScanPackages())) {
            return bean;
        }
        if (bean.getClass().getName().contains(Constants.GATEWAY_KEY)) {
            return bean;
        }
        if (bean.getClass().getName().startsWith(this.propertyConfig.getScanPackages())) {
            Method[] methods = ReflectionUtils.getAllDeclaredMethods(bean.getClass());
            if (methods != null) {
                if (bean.getClass().getName().contains(Constants.TEST_KEY)) {
                    return bean;
                }
                String interfaceName = Class.forName(ClassNameUtils.simplifyClassName(bean.getClass().getName())).getInterfaces()[0].getName();
                for (Method method : methods) {
                    DubboApi dubboApi = AnnotationUtils.findAnnotation(method, DubboApi.class);
                    if (dubboApi != null) {
                        Integer id = (dubboApi.method() + dubboApi.api()).hashCode();
                        String requestUri = "".equals(dubboApi.api()) ? method.getName() : dubboApi.api();
                        apis.put(dubboApi.method() + "-" + requestUri, DubboReferenceModel.builder()
                                .id(id)
                                .protocol(Constants.DUBBO_PROTOCOL_VALUE)
                                .timeout(dubboApi.timeout())
                                .dubboInterface(interfaceName)
                                .dubboMethod(method.getName())
                                .methodSign(method.getParameterTypes().length > 0 ?
                                        method.getParameterTypes()[0].getName() : "")
                                .requestUri(requestUri)
                                .requestMethod(dubboApi.method())
                                .version(dubboApi.version())
                                .build());
                    }
                }
            }
        }
        return bean;
    }

    protected Map<Object, Object> getApis() {
        return apis;
    }

}
