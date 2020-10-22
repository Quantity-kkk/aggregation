package top.kyqzwj.test.http.ioblock.client;

import com.jfinal.kit.Kv;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Description:
 * Copyright: © 2019 CSNT. All rights reserved.
 * Company:CSNT
 *
 * @author kyq
 * @version 1.0
 * @Date 2020/10/19 17:39
 */
public class TestDealLoop {
    public static void main(String[] arg) throws InterruptedException {
        LinkedBlockingQueue<Kv> originQueue = new LinkedBlockingQueue<>();

        UploadRunner uploadRunner = new UploadRunner(originQueue, 1);

        uploadRunner.start();
        Thread.sleep(10);
        System.out.println(System.currentTimeMillis());
        uploadRunner.stop();
        System.out.println(System.currentTimeMillis());
    }
}
