#### Dubbo-Generic-Gateway
---
🎉‍Description:
- 使用dubbo泛化调用达到网关路由转发+负载均衡的功能
- 使用des-rsa进行出入参的非对称加密
---
🎩Use:
- 按照```package me.sxl.gateway.model.DubboReferenceModel```配置数据库表
- 启动项目,并确保需要泛化调用的服务已启动
- POST请求```/api/route/{yourRequestMethod}/{yourRequestUri}```
---
🚩PS:
- 请求路径内的method和uri是为了一定程度上(弱)保证restful风格。。
- 生成rsa公钥私钥对的方法在```me.sxl.common.utils.RSAUtil#init()```