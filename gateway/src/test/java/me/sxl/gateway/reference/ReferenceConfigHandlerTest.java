package me.sxl.gateway.reference;

import org.apache.dubbo.common.extension.ExtensionLoader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ReferenceConfigHandlerTest {

    //    @Autowired
    private ApiReferenceStrategy referenceStrategy;

//    @Autowired
//    private NacosApiReferenceStrategy nacosApiReferenceStrategy;

    @Test
    public void testGetDubboSPI() {
        // 先获取SPI类加载器
        ExtensionLoader<ApiReferenceStrategy> extensionLoader = ExtensionLoader.getExtensionLoader(ApiReferenceStrategy.class);
//         再通过key-value方式获取
//         不通过adaptive方式获取的原因是要配置URL
        referenceStrategy = extensionLoader.getExtension("nacos");
        Assert.assertNotNull(referenceStrategy);

        // 获取到具体的实现类后获取nacos的配置信息
        // curl -X POST "http://127.0.0.1:8848/nacos/v1/cs/configs?dataId=testData&group=testGroup&content=helloWorld"
        String content = this.referenceStrategy.reference();
        Assert.assertEquals(content, "helloWorld");
        System.out.println(content);
    }


}