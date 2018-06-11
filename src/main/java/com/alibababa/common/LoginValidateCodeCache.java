package com.alibababa.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by ouronghui
 * 缓存登陆验证码
 */
public class LoginValidateCodeCache {

    private static Logger logger = LoggerFactory.getLogger(LoginValidateCodeCache.class);

    public static final String PREFIX = "LOGIN_VALIDATECODE";

    //LRU算法
    private static LoadingCache<String,String> localCache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(1).expireAfterAccess(90, TimeUnit.SECONDS)
            .build(new CacheLoader<String, String>() {
                //默认的数据加载实现,当调用get取值的时候,如果key没有对应的值,就调用这个方法进行加载.
                @Override
                public String load(String s) throws Exception {
                    return "null";
                }
            });

    public static void setValue(String value){
//        logger.info("{}","缓存登陆验证码");
        System.out.println("LoginValidateCodeCache: 缓存登陆验证码");
        localCache.put(PREFIX,value);
    }

    public static String getKey(String key){
        String value = null;
        try {
            value = localCache.get(key);
            if("null".equals(value)){
                return null;
            }
            return value;
        }catch (Exception e){
            logger.error("LoginValidateCodeCache get error",e);
        }
        return null;
    }
}
