package top.kyqzwj.test.thread;

import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.util.concurrent.CountDownLatch;

/**
 * Description: 测试的是多线程断点调试，如果调试设置里面Suspend设置为ALL，是没有办法接收到所有线程的断点信号的。
 * Copyright: © 2019 CSNT. All rights reserved.
 * Company:CSNT
 *
 * @author kyq
 * @version 1.0
 * @Date 2020/10/20 17:45
 */
public class ThreadOne extends Thread{

    private CountDownLatch countDownLatch;

    public ThreadOne(CountDownLatch latch){
        this.countDownLatch = latch;
        this.setDaemon(true);
    }
    @Override
    public void run() {
        for(int i = 0; i < 9; i++){
            System.out.println("sub" + i);
            countDownLatch.countDown();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(10);
        Thread thread = new ThreadOne(latch);
        thread.start();

        new Thread(){
            @Override
            public void run() {
                latch.countDown();
            }
        }.start();

        latch.await();
        for(int i = 0; i < 10; i++){
            System.out.println("main" + i);
        }
    }
}
