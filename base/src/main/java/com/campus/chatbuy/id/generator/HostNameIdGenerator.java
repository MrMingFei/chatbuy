package com.campus.chatbuy.id.generator;

import com.campus.chatbuy.util.ConfigUtil;
import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 根据机器名最后的数字编号获取工作进程Id.如果线上机器命名有统一规范,建议使用此种方式.
 * 列如机器的HostName为:dangdang-db-sharding-dev-01(公司名-部门名-服务名-环境名-编号)
 * ,会截取HostName最后的编号01作为workerId.
 *
 * @author DonneyYoung
 **/
public class HostNameIdGenerator implements IdGenerator {

    private static final Logger log = Logger.getLogger(HostNameIdGenerator.class);

    private final CommonSelfIdGenerator commonSelfIdGenerator = new CommonSelfIdGenerator();

    static {
        initWorkerId();
    }

    static void initWorkerId() {
        InetAddress address;
        Long workerId;
        try {
            address = InetAddress.getLocalHost();
        } catch (final UnknownHostException e) {
            throw new IllegalStateException("Cannot get LocalHost InetAddress, please check your network!");
        }
        String hostName = address.getHostName();
        log.info("initWorkerId hostName [" + hostName + "]");
        try {
            workerId = Long.valueOf(hostName.replace(hostName.replaceAll("\\d+$", ""), ""));
        } catch (final NumberFormatException e) {
            if (ConfigUtil.isProd()) {
                throw new IllegalArgumentException(String.format("Wrong hostname:%s, hostname must be end with number!", hostName));
            } else {
                workerId = new Long(123);
            }
        }
        CommonSelfIdGenerator.setWorkerId(workerId);
    }

    @Override
    public  Number generateId() {
        return commonSelfIdGenerator.generateId();
    }
}