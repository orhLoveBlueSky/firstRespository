package com.alibababa.controller.portal;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibababa.common.ServerResponse;
import com.alibababa.dao.UserRedPacketMapper;
import com.alibababa.pojo.UserRedPacket;
import com.alibababa.redis.IRedisRedPacketService;
import com.alibababa.service.IUserRedPacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/userRedPacket")
public class UserRedPacketController {

    @Autowired
    private IUserRedPacketService iUserRedPacketService;
    @Autowired
    private IRedisRedPacketService iRedisRedPacketService;


////    url: "/Dian/userRedPacket/grapRedPacket.do?redPacketId=1&userId=" + i,
//    @RequestMapping(value = "/grapRedPacket")
//    @ResponseBody
//    public Map<String, Object> grapRedPacket(Long redPacketId, Long userId) {
//        // 抢红包
//        int result = iUserRedPacketService.grapRedPacket(redPacketId, userId);
//        Map<String, Object> retMap = new HashMap<String, Object>();
//        boolean flag = result > 0;
//        retMap.put("success", flag);
//        retMap.put("message", flag ? "抢红包成功" : "抢红包失败");
//        return retMap;
//    }
    //    url: "/Dian/userRedPacket/grapRedPacket.do?redPacketId=1&userId=" + i,
    @RequestMapping(value = "/grapRedPacket")
    @ResponseBody
    public Map<String, Object> grapRedPacket(Long redPacketId, Long userId) {

            iUserRedPacketService.grapRedPacketForVersion(redPacketId, userId);

        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("message","抢红包失败");
        return retMap;
    }

    @RequestMapping(value = "/grapRedPacketByRedis")
    @ResponseBody
    public ServerResponse grapRedPacketByRedis(Long redPacketId, Long userId){
        ServerResponse res = iRedisRedPacketService.grapRedPacketByRedis(redPacketId,userId);

        return res;
    }

//     @Autowired
//    UserRedPacketMapper userRedPacketMapper;
//    @RequestMapping(value = "/test")
//    @ResponseBody
//    public ServerResponse test() {
//        UserRedPacket userRedPacket=new UserRedPacket();
//        Long l=Long.parseLong("8888");
//        Long time = System.currentTimeMillis();
//        userRedPacket.setRedPacketId(l);
//        userRedPacket.setNote("ouronghui");
//        userRedPacket.setUserId(l);
//        userRedPacket.setAmount(Double.parseDouble("1"));
//        userRedPacket.setGrabTime(new Timestamp(time));
//        System.out.println("userRedPacket");
//        System.out.println(userRedPacket);
//
//        UserRedPacket userRedPacket2=new UserRedPacket();
//        Long l1=Long.parseLong("7777");
//        userRedPacket2.setRedPacketId(l1);
//        userRedPacket2.setNote("ouronghui");
//        userRedPacket2.setUserId(l1);
//        userRedPacket2.setAmount(Double.parseDouble("1"));
//        userRedPacket2.setGrabTime(new Timestamp(time));
//        System.out.println("userRedPacket2");
//        System.out.println(userRedPacket2);
//
//        List<UserRedPacket> list = new ArrayList<>();
//        list.add(userRedPacket);
//        list.add(userRedPacket2);
//        int res = userRedPacketMapper.saveBatch(list);
//
//        return ServerResponse.createBySuccessMessage("res: " + res);
//    }

}
