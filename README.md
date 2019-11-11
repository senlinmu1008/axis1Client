# axis1Client
基于Axis1搭建的客户端

## 导包

```xml
<dependency>
    <groupId>org.apache.axis</groupId>
    <artifactId>axis</artifactId>
    <version>1.4</version>
</dependency>
<dependency>
    <groupId>axis</groupId>
    <artifactId>axis-jaxrpc</artifactId>
    <version>1.4</version>
</dependency>
<dependency>
    <groupId>axis</groupId>
    <artifactId>axis-wsdl4j</artifactId>
    <version>1.5.1</version>
</dependency>
<dependency>
    <groupId>commons-discovery</groupId>
    <artifactId>commons-discovery</artifactId>
    <version>0.2</version>
</dependency>
<dependency>
    <groupId>javax.mail</groupId>
    <artifactId>mail</artifactId>
    <version>1.4</version>
</dependency>
```

## 简单类型调用

```Java
@Slf4j
public class SimpleTestCase {
    @Test
    @SneakyThrows
    public void testAxis1Simple() {
        // http://ip:port/contextPath/url-pattern/serviceName?wsdl
        String webServiceUrl = "http://127.0.0.1:8080/axisServer/v1/call?wsdl";
        String sum = callSimpleType(webServiceUrl, "sum", new Object[]{"123", "456"});

        log.info("求和结果:[{}]", sum);
    }

    private String callSimpleType(String webServiceUrl, String methodName, Object[] objects) throws ServiceException, RemoteException {
        Service service = new Service();
        Call call = (Call) service.createCall();

        call.setTimeout(30000); // 设置超时
        call.setOperationName(new QName(methodName)); // 设置调用方法名
        call.setTargetEndpointAddress(webServiceUrl); // 设置调用的url

        // 执行调用,数组元素与被调用方法的参数列表一一对应，参数可以少传（需要为引用类型按null处理）但不能多传
        return call.invoke(objects).toString();
    }

}
```

## 复杂类型调用

```Java
@Slf4j
public class ComplexTestCase {
    @Test
    @SneakyThrows
    public void testAxis1Complex() {
        // http://ip:port/contextPath/url-pattern/serviceName?wsdl
        String webServiceUrl = "http://127.0.0.1:8080/axisServer/v1/call?wsdl";

        CommonDTO requestDTO = new CommonDTO();
        requestDTO.setCompany("ABC");
        requestDTO.setType(123);
        requestDTO.setServerFlag(false);
        List<String> managerList = new ArrayList<>();
        managerList.add("张三");
        managerList.add("李四");
        managerList.add("王五");
        requestDTO.setManagerList(managerList);

        CommonDTO responseDTO = callComplexType(webServiceUrl, "acceptInfo", requestDTO);

        log.info(JSON.toJSONString(responseDTO, true));
    }

    private CommonDTO callComplexType(String webServiceUrl, String methodName, CommonDTO commonDTO) throws ServiceException, RemoteException, MalformedURLException {
        Service service = new Service();
        Call call = (Call) service.createCall();

        call.setTimeout(30000); // 设置超时
        call.setOperationName(new QName(methodName)); // 设置调用方法名
        call.setTargetEndpointAddress(webServiceUrl); // 设置调用的url

        //注册实体对象,与server-config.wsdd中的beanMapping配置一致
        QName qName = new QName("urn:commonDTO", "common");
        call.registerTypeMapping(CommonDTO.class, qName,
                new BeanSerializerFactory(CommonDTO.class, qName),
                new BeanDeserializerFactory(CommonDTO.class, qName));
        //设置被调用方法的形参
        call.addParameter("arg1", qName, ParameterMode.IN);
        //设置返回值类型
        call.setReturnClass(CommonDTO.class);

        // 执行调用
        return (CommonDTO) call.invoke(new Object[]{commonDTO});
    }
}
```

## 说明

1. 返回对象在反序列化时设置属性值是通过拼接set方法来实现，要求set方法无返回值，不要使用lombok的@Accessors注解