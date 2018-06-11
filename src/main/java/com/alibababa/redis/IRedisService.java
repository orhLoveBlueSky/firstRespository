package com.alibababa.redis;

import com.alibababa.common.ServerResponse;
import com.alibababa.pojo.User;

public interface IRedisService {
    public <T> boolean set(String prefix, String token, T data);

    public User getUser(String tokenValue);


}