package top.kyqzwj.test.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import org.junit.Test;
import java.nio.file.*;

/**
 * Description:
 * Copyright: © 2019 CSNT. All rights reserved.
 * Company:CSNT
 *
 * @author kyq
 * @version 1.0
 * @Date 2020/10/14 14:26
 */
public class WatchServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(WatchServiceTest.class);


    /**
     * 测试WatchService的监听在take调用获取watchkey后，在watchkey重置前，如果新增了文件是否还能监听到。
     *
     * 步骤：take到事件后，程序会睡眠几秒，在这期间手动将准备好的文件放置到文件夹中，观测是否能监听到。
     * */
    @Test
    public void testWatchBlock(){
        try {
            final Path path = FileSystems.getDefault().getPath("E:\\testpackage\\watchdir");
            System.out.println(path);

            final WatchService watchService = FileSystems.getDefault().newWatchService();
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

            while (true) {
                final WatchKey wk = watchService.take();
                for (WatchEvent<?> event : wk.pollEvents()) {
                    Path eventPath = (Path) event.context();
                    System.out.println("有新增文件："+eventPath.toString());
                    Thread.sleep(3000L);
                    // reset the key

                    boolean valid = wk.reset();
                    if (!valid) {
                        logger.error("watch key invalid!");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("",e);
        }

    }
}
