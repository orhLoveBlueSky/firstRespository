package com.alibababa.service.impl;

import com.alibababa.dao.RedPacketMapper;
import com.alibababa.dao.UserRedPacketMapper;
import com.alibababa.pojo.RedPacket;
import com.alibababa.pojo.UserRedPacket;
import com.alibababa.service.IUserRedPacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;



@Service("iUserRedPacketService")
public class UserRedPacketServiceImpl implements IUserRedPacketService {

    @Autowired
    private UserRedPacketMapper userRedPacketMapper;

    @Autowired
    private RedPacketMapper redPacketMapper;

    // 失败
    private static final int FAILED = 0;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int grapRedPacket(Long redPacketId, Long userId) {
        // 获取红包信息
        // RedPacket redPacket = redPacketDao.getRedPacket(redPacketId);
        // 悲观锁
        RedPacket redPacket = redPacketMapper.getRedPacketForUpdate(redPacketId);
        System.out.println("redPacket.getStock: "+redPacket.getStock());
        System.out.println("userid: "+userId);
        // 当前小红包库存大于0
        if (redPacket.getStock() > 0) {
            redPacketMapper.decreaseRedPacket(redPacketId);
            // 生成抢红包信息
            UserRedPacket userRedPacket = new UserRedPacket();
            userRedPacket.setRedPacketId(redPacketId);
            userRedPacket.setUserId(userId);
            userRedPacket.setAmount(redPacket.getUnitAmount());
            userRedPacket.setNote("抢红包 " + redPacketId);
            // 插入抢红包信息
            int result = userRedPacketMapper.grapRedPacket(userRedPacket);
            return result;
        }
        System.out.println("抢红包失败");
        System.out.println("user_id:" + userId);
        // 失败返回
        return FAILED;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int grapRedPacketForVersion(Long redPacketId, Long userId) {
        // 获取红包信息
        // RedPacket redPacket = redPacketDao.getRedPacket(redPacketId);
        // 悲观锁
        RedPacket redPacket = redPacketMapper.getRedPacket(redPacketId);
        System.out.println("redPacket.getStock: "+redPacket.getStock());
        System.out.println("userid: "+userId);
        // 当前小红包库存大于0
        if (redPacket.getStock() > 0) {
            int update = redPacketMapper.decreaseRedPacketForVersion(redPacketId,redPacket.getVersion());
            // 生成抢红包信息
            if( update == 0 ){
                return FAILED;
            }
            UserRedPacket userRedPacket = new UserRedPacket();
            userRedPacket.setRedPacketId(redPacketId);
            userRedPacket.setUserId(userId);
            userRedPacket.setAmount(redPacket.getUnitAmount());
            userRedPacket.setNote("抢红包 " + redPacketId);
            // 插入抢红包信息
            int result = userRedPacketMapper.grapRedPacket(userRedPacket);
            return result;
        }
        System.out.println("红包没了/(ㄒoㄒ)/~~");
        System.out.println("user_id:" + userId);
        // 失败返回
        return FAILED;
    }




}

