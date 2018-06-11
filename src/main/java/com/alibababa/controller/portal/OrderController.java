package com.alibababa.controller.portal;


import com.alibababa.common.Const;
import com.alibababa.common.ResponseCode;
import com.alibababa.common.ServerResponse;
import com.alibababa.pojo.User;
import com.alibababa.service.IOrderService;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

@Controller
@RequestMapping(value="/order/")
public class OrderController {

    @Autowired
    IOrderService iOrderService;

    private static final Logger logger= LoggerFactory.getLogger(OrderController.class);
    //订单支付
    @RequestMapping("pay.do")
    @ResponseBody                                 //字符串自动转换为整形，省区可Long.parseLong()
    public ServerResponse pay(HttpSession session, Long orderNo, HttpServletRequest request){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
//        String path = request.getSession().getServletContext().getRealPath("upload");
        String path = "C:\\ideaWorkSpace\\dianshangpingtai\\out\\artifacts\\loveSky_war_exploded\\upload";
        return iOrderService.pay(orderNo,user.getId(),path);
    }

    //支付宝回调处理接口,是支付宝调用商城的接口
    @RequestMapping("alipay_callback.do")
    @ResponseBody                                             //为什么返回Object,返回给谁，object有可能是ServerResponse或是字符串？？
    public Object alipayCallback(HttpServletRequest request){ //返回的参数只有字符串success、failed是给支付宝看的
        Map<String,String> params = Maps.newHashMap();

        Map requestParams = request.getParameterMap();//可看到这个map的value是一个数组,要进行处理
        for(Iterator iter = requestParams.keySet().iterator();iter.hasNext();){
            String key = (String)iter.next();
            String[] valueArr = (String[])requestParams.get(key);
            String valStr = "";
            for(int i = 0; i < valueArr.length; i++){
                 valStr = (i == valueArr.length-1) ? valStr+valueArr[i]:valStr+valueArr[i]+",";
            }
            params.put(key,valStr);
        }
        System.out.println("callback1");
        logger.info("支付宝回调,sign:{},trade_status:{},参数:{}",params.get("sign"),params.get("trade_status"),params.toString());
        //验证回调的正确性，是不是支付宝发的，且要避免重复通知
        params.remove("sign_type");
        System.out.println("callback2");
        //执行到这布就不执行了
        try {
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getPublicKey(),"utf-8",Configs.getSignType());
            if(!alipayRSACheckedV2){
                 return ServerResponse.createByErrorMessage("非法请求，验证不通过，再恶意请求我就报警找网警了");  //是给坏人看的返回serverResponse
            }                                                                                               //返回支付宝的是success or failed
        } catch (AlipayApiException e) {
           logger.error("支付宝验证回掉异常",e);
        }

        System.out.println("callbackservice before");
        ServerResponse serverResponse = iOrderService.aliCallback(params);
        System.out.println("callbackservice after");
        if(serverResponse.isSuccess()){
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }

    //接口：前台轮询查询订单的支付状态，是前台调用的
    //就是查询订单表
    @RequestMapping("query_order_pay_status.do")
    @ResponseBody
    public ServerResponse queryOrderStatus(HttpSession session,String orderNo){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.queryOrderStatus(orderNo);
    }

}
