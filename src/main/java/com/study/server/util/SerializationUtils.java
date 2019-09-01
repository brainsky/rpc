package com.study.server.util;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import lombok.extern.slf4j.Slf4j;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SerializationUtils {


    private static Map<Class<?>, Schema<?>> cacheMap = new ConcurrentHashMap<>();

    private static Objenesis objenesis = new ObjenesisStd(true);


    private SerializationUtils(){}

    @SuppressWarnings("unchecked")
    private static <T> Schema<T> getSchema(Class<T> cls){

        Schema<T> schema = (Schema<T>) cacheMap.get(cls);

        if(schema == null){
            schema = RuntimeSchema.createFrom(cls);
            if(schema != null){
                cacheMap.put(cls, schema);
            }
        }
        return schema;
    }

    /**
     * 序列化
     * @param object
     * @param <T>
     * @return
     */
    public static <T> byte[] serialize(T object){
        Class<T> cls = (Class<T>) object.getClass();

        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

        try {
            Schema<T> schema = getSchema(cls);

            return ProtobufIOUtil.toByteArray(object, schema, buffer);
        } catch (Exception e) {
          log.error("serialize exception",e);
          throw  new IllegalStateException(e.getMessage(), e);
        } finally {
           buffer.clear();
        }

    }

    /**
     * 反序列化
     * @param data
     * @param genericClass
     * @return
     */
    public static <T> T deserialize(byte[] data, Class<T> genericClass){
        try {
            T instance = (T)genericClass.newInstance();

            Schema<T>  schema = getSchema(genericClass);

            ProtobufIOUtil.mergeFrom(data, instance, schema);

            return instance;

        } catch (InstantiationException | IllegalAccessException e) {
            log.error("deserialize exception",e);
            throw  new IllegalStateException(e.getMessage(),e);
        }
    }
}
