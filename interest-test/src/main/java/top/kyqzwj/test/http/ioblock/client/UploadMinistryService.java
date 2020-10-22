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
 * @Date 2020/10/19 15:17
 */
public class UploadMinistryService {

    private final LinkedBlockingQueue<Kv> originQueue = new LinkedBlockingQueue<>();

    private UploadRunner[] uploadRunners;
    private static final int UPLOAD_THREAD_NUM = 1;
    public  void uploadData(){
        //初始化50个数据放进去处理，查看线程阻塞的情况下CPU的占用
        for(int i = 0; i < 50; i++){
            Kv kv = new Kv();
            kv.put("id", i);
            originQueue.add(kv);
        }

        uploadRunners = new UploadRunner[UPLOAD_THREAD_NUM];

        for (int i = 0; i < UPLOAD_THREAD_NUM; i++) {
            uploadRunners[i] = new UploadRunner(originQueue, i);
        }

        //线程依次启动
        for (int i = 0; i < UPLOAD_THREAD_NUM; i++) {
            uploadRunners[i].start();
        }

        //线程依次等待执行完成
        for (int i = 0; i < UPLOAD_THREAD_NUM; i++) {
            uploadRunners[i].stop();
        }
    }

    public static void main(String[] args){
        UploadMinistryService uploadMinistryService = new UploadMinistryService();

        uploadMinistryService.uploadData();
    }
}
