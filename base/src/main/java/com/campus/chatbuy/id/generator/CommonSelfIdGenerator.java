package com.campus.chatbuy.id.generator;

import com.campus.chatbuy.util.StringUtil;
import org.apache.log4j.Logger;
import org.guzz.util.Assert;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * http://www.jianshu.com/p/80e68ae9e3a4
 *
 * 自生成Id生成器.
 * 
 * <p>
 * 长度为64bit,从高位到低位依次为
 * </p>
 * 
 * <pre>
 * 1bit   符号位 
 * 41bits 时间偏移量从2016年11月1日零点到现在的毫秒数
 * 10bits 工作进程Id
 * 12bits 同一个毫秒内的自增量
 * </pre>
 * 
 * <p>
 * 工作进程Id获取优先级: 系统变量{@code sjdbc.self.id.generator.worker.id} 大于 环境变量{@code SJDBC_SELF_ID_GENERATOR_WORKER_ID}
 * ,另外可以调用@{@code CommonSelfIdGenerator.setWorkerId}进行设置
 * </p>
 * 
 * @author gaohongtao
 */
public class CommonSelfIdGenerator implements IdGenerator {

    private static final Logger log = Logger.getLogger(CommonSelfIdGenerator.class);
    
    public static final long SJDBC_EPOCH;
    
    private static final long SEQUENCE_BITS = 12L;
    
    private static final long WORKER_ID_BITS = 10L;
    
    private static final long SEQUENCE_MASK = (1 << SEQUENCE_BITS) - 1;
    
    private static final long WORKER_ID_LEFT_SHIFT_BITS = SEQUENCE_BITS;
    
    private static final long TIMESTAMP_LEFT_SHIFT_BITS = WORKER_ID_LEFT_SHIFT_BITS + WORKER_ID_BITS;
    
    private static final long WORKER_ID_MAX_VALUE = 1L << WORKER_ID_BITS;
    
    private static AbstractClock clock = AbstractClock.systemClock();
    
    private static long workerId;
    
    static {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, Calendar.NOVEMBER, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        SJDBC_EPOCH = calendar.getTimeInMillis();
        initWorkerId();
    }
    
    private long sequence;
    
    private long lastTime;
    
    static  void initWorkerId() {
        String workerId = System.getProperty("sjdbc.self.id.generator.worker.id");
        if (StringUtil.isNotEmpty(workerId)) {
            setWorkerId(Long.valueOf(workerId));
            return;
        }

        workerId = System.getenv("SJDBC_SELF_ID_GENERATOR_WORKER_ID");
        if (StringUtil.isEmpty(workerId)) {
            return;
        }
        setWorkerId(Long.valueOf(workerId));
    }
    
    /**
     * 设置工作进程Id.
     * 
     * @param workerId 工作进程Id
     */
    public static  void setWorkerId(final Long workerId) {
        Assert.assertTrue(workerId >= 0L && workerId < WORKER_ID_MAX_VALUE, "workerId is illegal");
        CommonSelfIdGenerator.workerId = workerId;
    }
    
    /**
     * 生成Id.
     * 
     * @return 返回@{@link Long}类型的Id
     */
    @Override
    public synchronized  Number generateId() {
        long time = clock.millis();
        Assert.assertTrue(lastTime <= time, String.format("Clock is moving backwards, last time is %d milliseconds, " +
                "current time is %d milliseconds", lastTime, time ));

        if (lastTime == time) {
            if (0L == (sequence = ++sequence & SEQUENCE_MASK)) {
                time = waitUntilNextTime(time);
            }
        } else {
            sequence = 0;
        }
        lastTime = time;
        if (log.isDebugEnabled()) {
            log.debug(String.format("{}-{}-{}", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").
                    format(new Date(lastTime)), workerId, sequence));
        }
        return ((time - SJDBC_EPOCH) << TIMESTAMP_LEFT_SHIFT_BITS) | (workerId << WORKER_ID_LEFT_SHIFT_BITS) | sequence;
    }
    
    private  long waitUntilNextTime(final long lastTime) {
        long time = clock.millis();
        while (time <= lastTime) {
            time = clock.millis();
        }
        return time;
    }
}