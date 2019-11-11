/**
 * Copyright (C), 2015-2019
 */

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.junit.Test;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;

/**
 * @author zhaoxb
 * @create 2019-10-28 22:33
 */
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