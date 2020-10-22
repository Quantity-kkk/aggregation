package top.kyqzwj.test.schedule;

import com.jfinal.log.Log;
import it.sauronsoftware.cron4j.Scheduler;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Copyright: © 2019 CSNT. All rights reserved.
 * Company:CSNT
 *
 * @author kyq
 * @version 1.0
 * @Date 2020/10/20 13:47
 */
public class ScheduledThread{
    private static Log LOG = Log.getLog("ScheduledThread");

    /**
     * 间隔时间任务调度器
     * */
    private ScheduledThreadPoolExecutor fixedScheduler;
    /**
     * cron调度器
     */
    private Scheduler cronScheduler = null;
    private Thread processThread = null;
    /**
     * 调度线程池
     */
    private int scheduledThreadPoolSize = 2;

    public ScheduledThread(int scheduledThreadPoolSize){
        this.scheduledThreadPoolSize = scheduledThreadPoolSize;
    }

    /**
     * @param job                 定期执行的任务
     * @param initialDelaySeconds 启动延迟时间
     * @param periodSeconds       每次执行任务的间隔时间(单位秒)
     * @return
     * @Title: scheduleAtFixedRate
     * @Description: 延迟指定秒后启动，并以固定的频率来运行任务。后续任务的启动时间不受前次任务延时影响（并行）。
     * @since V1.0.0
     */
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable job, int initialDelaySeconds, int periodSeconds) {
        return fixedScheduler.scheduleAtFixedRate(job, initialDelaySeconds, periodSeconds, TimeUnit.SECONDS);
    }

    /**
     * @param job                 定期执行的任务
     * @param initialDelaySeconds 启动延迟时间
     * @param periodSeconds       每次执行任务的间隔时间(单位秒)
     * @return
     * @Title: scheduleWithFixedDelay
     * @Description: 延迟指定秒后启动，两次任务间保持固定的时间间隔(任务串行执行，前一个结束之后间隔固定时间后一个才会启动)
     * @since V1.0.0
     */
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable job, int initialDelaySeconds, int periodSeconds) {
        return fixedScheduler.scheduleWithFixedDelay(job, initialDelaySeconds, periodSeconds, TimeUnit.SECONDS);
    }

    public boolean start(){
        //1.加载任务
        initFixedScheduler();

        return true;
    }

    public boolean stop(){
        this.fixedScheduler.shutdown();
        LOG.info("ScheduledThreadPoolExecutor开始停止");
        try {
            while (!fixedScheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                LOG.info("SchedulerPlugin正在停止");
            }
        } catch (Throwable t) {
            LOG.error("SchedulerPlugin停止失败");
            return false;
        }

        LOG.info("SchedulerPlugin已停止");
        return true;
    }

    private void initFixedScheduler() {
        if (this.fixedScheduler == null) {
            synchronized (this) {
                if (this.fixedScheduler == null) {
                    this.fixedScheduler = new ScheduledThreadPoolExecutor(scheduledThreadPoolSize);
                }
            }
        }
    }
}
