package com.alibababa.redis.impl;

import com.alibababa.common.Const;
import com.alibababa.common.ServerResponse;
import com.alibababa.dao.UserRedPacketMapper;
import com.alibababa.pojo.UserRedPacket;
import com.alibababa.redis.IRedisRedPacketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


@Service("iRedisRedPacketService")
public class RedisRedPacketServiceImpl implements IRedisRedPacketService {
    private static final String Prefix = "red_packet_list_";
    private static final int TIME_SIZE = 1000;
    private Logger logger = LoggerFactory.getLogger(RedisRedPacketServiceImpl.class);
    @Autowired
    JedisPool jedisPool;
    String sha1 = null;
    String script = "local listKey = 'red_packet_list_'..KEYS[1] \n"
           +"local redPacket = 'red_packet_hash_'..KEYS[1] \n"
           +"local stock = tonumber(redis.call('hget',redPacket,'stock')) \n"
           +"if stock <= 0 then return 0 end \n"
            +"stock = stock-1 \n"
           +"redis.call('hset',redPacket,'stock',tostring(stock)) \n"
            +"redis.call('rpush',listKey,ARGV[1]) \n"
            +"if stock == 0 then return 2 end \n"
           +"return 1 \n";
    @Autowired
    UserRedPacketMapper userRedPacketMapper;
    @Async
    public void saveUserRedPacketToDB(Long redPacketId, Double unitAmount){
        logger.info("红包已抢完，将redis的抢红包的用户信息保存到数据库");

        Jedis jedis = null;
        String key = Const.redPacketList + redPacketId;
        List<UserRedPacket> userRedPacketsList = new ArrayList(Const.TIME_SIZE);
        try {
            jedis = jedisPool.getResource();
            Long size = jedis.llen(key);
            logger.info("redPacketListSize: " + size);
            Long times = size % Const.TIME_SIZE == 0 ? size / Const.TIME_SIZE : size / Const.TIME_SIZE + 1;
            List userIdList=null;
            int count = 0;                     //成功更新数据库表的行数
            logger.info("times: " + times);
            for ( int i = 0; i < times; i++ ) {
                if ( i == 0 ) {
                   userIdList = jedis.lrange(key, 0, TIME_SIZE-1);

               }else {
                   userIdList = jedis.lrange(key, i*Const.TIME_SIZE, (i+1)*Const.TIME_SIZE-1);
               }
               for ( int j = 0; j < userIdList.size(); j++ ) {
                   String iterm = userIdList.get(j).toString();
                   String[] arr = iterm.split("-");
                   String userIdStr = arr[0];
                   String timeStr = arr[1];
                   Long userId = Long.parseLong(userIdStr);
                   Long time = Long.parseLong(timeStr);
                   UserRedPacket userRedPacket = new UserRedPacket();
                   userRedPacket.setUserId(userId);
                   userRedPacket.setRedPacketId(redPacketId);
                   userRedPacket.setGrabTime(new Timestamp(time));
                   userRedPacket.setAmount(unitAmount);
                   userRedPacket.setNote("10元红包");
                   userRedPacketsList.add(userRedPacket);
               }
               count += userRedPacketMapper.saveBatch(userRedPacketsList);
               userRedPacketsList.clear();
            }
//            jedis.del(Const.redPacketList + redPacketId);
            Long end = System.currentTimeMillis();
            logger.info("保存数据库完");
            logger.info("共保存了"+count+"条记录");
        }finally {
            if( jedis != null ){
                jedis.close();
            }
        }
    }

    public ServerResponse grapRedPacketByRedis(Long redPacketId, Long userId){
        String args = userId+"-"+System.currentTimeMillis();
        Long result = null;
        Jedis jedis = null;

        try{
            jedis = jedisPool.getResource();
            if(sha1 == null){
                sha1 = jedis.scriptLoad(script);
            }
            System.out.println("line:100");
            Object res = jedis.evalsha(sha1, 1, redPacketId + "", args);

            result = (Long) res;
            System.out.println("res: " + result);
            if(result == 1){
                return ServerResponse.createBySuccessMessage("你抢到红包了");
            }
            if(result == 0){
                return ServerResponse.createByErrorMessage("你没抢到红包");
            }
            String unitAmountStr = jedis.hget(Const.redPacketHash + redPacketId,"unit_amount");
            Double uniAmount = Double.parseDouble(unitAmountStr);
            this.saveUserRedPacketToDB(redPacketId,uniAmount);

            return ServerResponse.createBySuccessMessage("红包已全部抢完");


        }finally {
            if(jedis != null){
                jedis.close();
            }
        }

    }



}
