package top.kyqzwj.test.jmx;

/**
 * Description:
 * Copyright: © 2019 CSNT. All rights reserved.
 * Company:CSNT
 *
 * @author kyq
 * @version 1.0
 * @Date 2020/10/21 17:36
 */
public interface HelloMBean {
    String getName();
    void setName(String name);
    String hello();
    String hello(String caller);
}
