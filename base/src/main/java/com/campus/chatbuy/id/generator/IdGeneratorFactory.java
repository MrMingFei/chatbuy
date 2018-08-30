package com.campus.chatbuy.id.generator;

/**
 * Created by jinku on 2017/8/30.
 */
public class IdGeneratorFactory {

    private final static IdGenerator idGenerator = new HostNameIdGenerator();

    public static long generateId() {
        return idGenerator.generateId().longValue();
    }
}
