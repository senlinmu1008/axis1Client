/**
 * Copyright (C), 2015-2019
 */

import com.alibaba.fastjson.JSON;
import net.zhaoxiaobin.domain.CommonDTO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.junit.Test;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author zhaoxb
 * @create 2019-10-30 00:15
 */
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