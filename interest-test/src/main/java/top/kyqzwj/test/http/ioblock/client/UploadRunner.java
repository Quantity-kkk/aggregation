package top.kyqzwj.test.http.ioblock.client;

import com.jfinal.kit.Kv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Copyright: © 2019 CSNT. All rights reserved.
 * Company:CSNT
 *
 * @author kyq
 * @version 1.0
 * @Date 2020/10/19 15:06
 */
public class UploadRunner implements Runnable {
    protected static Logger logger = LoggerFactory.getLogger(UploadRunner.class);
    private final LinkedBlockingQueue<Kv> originQueue;
    private volatile boolean running = false;
    private Thread processThread = null;
    private final int token;

    public UploadRunner(LinkedBlockingQueue<Kv> originQueue, int token) {
        this.originQueue = originQueue;
        this.token = token;
    }

    public synchronized void start() {
        if (!running) {
            running = true;

            processThread = new Thread(this, "SplitWork" + token);
//            processThread.setDaemon(true);
            processThread.start();
        }
    }

    public synchronized void stop() {
        if (running) {
            running = false;

            try {
                processThread.join();
            } catch (Exception e) {
                e.printStackTrace();
            }

            while (originQueue.size() > 0) {
                upload();
            }
        }
    }


    public LinkedBlockingQueue<Kv> getOriginQueue() {
        return originQueue;
    }

    @Override
    public void run() {
        while (running) {
            upload();
        }
    }

    private void upload() {
        Kv originData = null;
        try {
            System.out.println(originQueue.size());
            if ((originData = originQueue.poll(60000, TimeUnit.MILLISECONDS)) != null) {
                logger.info("{}开始处理, 剩余{}条", token, originQueue.size());
                HttpHelper.uploadMinistry("http://localhost:8080","test",Thread.currentThread()+"");
                logger.info("{}处理完成, 剩余{}条", token, originQueue.size());
            }
        } catch (Throwable e) {
            logger.error("{}上传异常:{}", token, e.toString(), e);
        }
    }
}