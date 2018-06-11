package com.alibababa.dao;

import com.alibababa.pojo.UserRedPacket;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface UserRedPacketMapper {

    /**
     * 插入抢红包信息.
     * @param userRedPacket ——抢红包信息
     * @return 影响记录数.
     */
    public int grapRedPacket(UserRedPacket userRedPacket);
    public int insert(UserRedPacket userRedPacket);
    public int saveBatch(List<UserRedPacket> elms);
}
