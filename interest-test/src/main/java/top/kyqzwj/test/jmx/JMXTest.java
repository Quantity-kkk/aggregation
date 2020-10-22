package top.kyqzwj.test.jmx;

import javax.management.*;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.rmi.registry.LocateRegistry;

/**
 * Description: jmx的 rmi 连接器
 *
 * JMX主要是 Java定义的管理扩展接口规范，通过这一接口规范，可以用来管理和监测Java程序，例如常见的jvm内存信息，cpu使用率等，都是基于JMX实现。
 * tomcat应用监控也基于JMX实现，druid也实现了jmx相关的一套监测接口。
 * Copyright: © 2019 CSNT. All rights reserved.
 * Company:CSNT
 *
 * @author kyq
 * @version 1.0
 * @Date 2020/10/21 17:10
 */
public class JMXTest {

    public static void main(String[] args){
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();

        //MBean register
        try {
            System.out.println("Bean register JMXBean...");
            ObjectName helloName = new ObjectName("KyqDemo:type=helloDemo,name=helloTestJMXBean");
            server.registerMBean(new Hello(), helloName);
            System.out.println("JMXBean registered...");
        } catch (InstanceAlreadyExistsException e) {
            e.printStackTrace();
        } catch (MBeanRegistrationException e) {
            e.printStackTrace();
        } catch (NotCompliantMBeanException e) {
            e.printStackTrace();
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        }

        //MBean connector register
        try {
            System.out.println("Bean register rmi JMXBean connector ...");

            LocateRegistry.createRegistry(8999);
            JMXServiceURL url =  new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:8999/jmxrmi");
            JMXConnectorServer jmxConnectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, null, server);
            jmxConnectorServer.start();

            System.out.println("Rmi JMXBean connector registered...");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
