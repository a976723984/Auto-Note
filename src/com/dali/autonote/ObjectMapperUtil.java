package com.dali.autonote;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializer;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;

public class ObjectMapperUtil {
    private static ObjectMapper mapper;

    public static ObjectMapper getInstance() {
        if (mapper == null) {
            synchronized (ObjectMapperUtil.class) {
                if (mapper == null) {
                    mapper = new ObjectMapper();
                    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                    mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
                    mapper.setSerializerFactory(BeanSerializerFactory.instance);
                    mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
                }
            }
        }
        return mapper;
    }
}
