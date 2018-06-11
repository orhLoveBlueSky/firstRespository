package com.alibababa.redis;

import com.alibababa.common.ServerResponse;

public interface IRedisRedPacketService {

    public void saveUserRedPacketToDB(Long redPacketId, Double unitAmount);

        public ServerResponse grapRedPacketByRedis(Long redPacketId, Long userId);
}
