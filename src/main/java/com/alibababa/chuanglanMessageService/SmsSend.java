package com.alibababa.chuanglanMessageService;

import com.alibaba.fastjson.JSON;

import java.io.UnsupportedEncodingException;

/**
 *
 * @author tianyh
 * @Description:普通短信发送
 */
public class SmsSend {

    public static final String charset = "utf-8";
    // 请登录zz.253.com 获取创蓝API账号(非登录账号,示例:N1234567)
    public static String account = "CN1713650";
    // 请登录zz.253.com 获取创蓝API密码(非登录密码)
    public static String password = "jiangushi888_";

    public static void send(String phone, String validateCode) throws UnsupportedEncodingException {
		
        //短信发送的URL 请登录zz.253.com 获取完整的URL接口信息
        String smsSingleRequestServerUrl = "http://smssh1.253.com/msg/send/json";
        // 设置您要发送的内容：其中“【】”中括号为运营商签名符号，多签名内容前置添加提交
        String msg1 = "【253云通讯】你好,你的验证码是: " + validateCode;

//        //验证码随机6位数
//        int random = (int)((Math.random()*9+1)*100000);   //数字是 1 - 9
//        //短信内容2
//        String msg2 = "" + random;
        //短信内容拼接
//        String msg = msg1 + msg2;

        //手机号码
//        String phone = "18845118773";  //15251212317
        //状态报告
        String report= "true";

        SmsSendRequest smsSingleRequest = new SmsSendRequest(account, password, msg1, phone,report);

        String requestJson = JSON.toJSONString(smsSingleRequest);

        System.out.println("before request string is: " + requestJson);

        String response = ChuangLanSmsUtil.sendSmsByPost(smsSingleRequestServerUrl, requestJson);

        System.out.println("response after request result is :" + response);

        SmsSendResponse smsSingleResponse = JSON.parseObject(response, SmsSendResponse.class);

        System.out.println("response  toString is :" + smsSingleResponse);

    }
}
