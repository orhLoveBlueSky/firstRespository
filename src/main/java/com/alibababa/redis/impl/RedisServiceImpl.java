package com.alibababa.redis.impl;


import com.alibaba.fastjson.JSON;
import com.alibababa.common.Const;
import com.alibababa.pojo.User;
import com.alibababa.redis.IRedisService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;



@Service("iRedisService")
public class RedisServiceImpl implements IRedisService {


    @Autowired
    JedisPool jedisPool;
    public <T> boolean set(String prefix,String token,T data){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String str = beanToString(data);
            if(str == null || str.length() <= 0){
                return false;
            }
            int seconds = 24*60*60;
             String realKey = prefix + ":" + token;
            System.out.println("redisSet");
            System.out.println(realKey);
            jedis.setex(realKey, seconds, str);
            return true;
        }finally {
            if(jedis != null){
               jedis.close();
            }
        }

    }

    private <T> String beanToString(T data) {
        if(data == null){

            return null;
        }
        Class<?> clazz = data.getClass();
        if(clazz==int.class || clazz==Integer.class){
            return ""+data;
        }else if(clazz == String.class){
               return (String)data;
        }else if(clazz == long.class || clazz == Long.class){
              return "" + data;
        }else {
            return JSON.toJSONString(data);
        }

    }
    //根据cookie的token从redis中取用户信息，redis字符串转bean
    public User getUser(String tokenValue){
        Jedis jedis = null;
        if(tokenValue == null){
            return null;
        }
        try{
            jedis = jedisPool.getResource();
            String realKey = Const.COOKI_NAME_TOKEN + ":" + tokenValue;
            System.out.println("redisGet");
            System.out.println(realKey);

            String redisStr = jedis.get(realKey);
            //将字符串转换为对应的bean！！！
            System.out.println("getUser");
            System.out.println(redisStr);
            User user= (User)stringToBean(redisStr, User.class);
            System.out.println(user);
            return user;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    private Object stringToBean(String jsonStr,Class<?> clazz){
        Object bean = JSON.parseObject(jsonStr, clazz);
        return bean;
    }
}
