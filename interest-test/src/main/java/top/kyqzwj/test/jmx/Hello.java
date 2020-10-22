package top.kyqzwj.test.jmx;

/**
 * Description:
 * Copyright: © 2019 CSNT. All rights reserved.
 * Company:CSNT
 *
 * @author kyq
 * @version 1.0
 * @Date 2020/10/21 17:38
 */
public class Hello implements HelloMBean{
    private String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String hello() {
        String msg = "Hello World！";
        System.out.println(msg);
        return msg;
    }

    @Override
    public String hello(String caller) {
        String msg = "Hello World！" + caller;
        System.out.println(msg);
        return msg;
    }
}
