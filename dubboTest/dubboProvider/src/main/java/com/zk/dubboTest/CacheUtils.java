package com.zk.dubboTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Component
public class CacheUtils {

    @Autowired
    private JedisPool jedisPool;

    public void put(String key,Object obj){
        if (obj==null){
            return;
        }
        if (obj instanceof String){
            jedisPool.getResource().set(key,(String) obj);
        }else if (obj instanceof Serializable){
            jedisPool.getResource().set(key.getBytes(),serialize(obj));
        }
    }

    public <T>T get(String key){
            T tmp = (T)new Object();
            if (tmp  instanceof String){
                return (T)jedisPool.getResource().get(key);
            }else {
                byte[] data = jedisPool.getResource().get(key.getBytes());
                return (T)unserizlize(data);
            }
    }

    private Type getResultType(Method declaredMethod){
        //获取返回值的类型，此处不是数组，请注意智商，返回值只能是一个
        Type genericReturnType = declaredMethod.getGenericReturnType();
        System.out.println(genericReturnType);
        //获取返回值的泛型参数
        if(genericReturnType instanceof ParameterizedType){
            Type[] actualTypeArguments = ((ParameterizedType)genericReturnType).getActualTypeArguments();
            if (actualTypeArguments!=null){
                return actualTypeArguments[0];
            }
        }
        return null;
    }

    public void del(String key){
        jedisPool.getResource().del(key.getBytes());
    }

    private  byte[] serialize(Object object) {
        ObjectOutputStream objectOutputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            byte[] getByte = byteArrayOutputStream.toByteArray();
            return getByte;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private  Object unserizlize(byte[] binaryByte) {
        if (binaryByte==null){
            return null;
        }
        ObjectInputStream objectInputStream = null;
        ByteArrayInputStream byteArrayInputStream = null;
        byteArrayInputStream = new ByteArrayInputStream(binaryByte);
        try {
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Object obj = objectInputStream.readObject();
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
