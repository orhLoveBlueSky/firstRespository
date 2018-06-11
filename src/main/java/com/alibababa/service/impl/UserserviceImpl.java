package com.alibababa.service.impl;

import com.alibababa.activemq.queue.PhoneValidateCodeMessageProducer;
import com.alibababa.common.*;
import com.alibababa.dao.UserMapper;
import com.alibababa.imagecodevalidate.RandomValidateCode;
import com.alibababa.pojo.User;
import com.alibababa.redis.IRedisService;
import com.alibababa.service.IUserService;
import com.alibababa.util.MD5Util;
import com.alibababa.util.MD5Util1111;
import com.alibababa.util.PropertiesUtil;
import com.google.common.base.Joiner;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

import java.util.UUID;


@Service("iUserService")
public class UserserviceImpl implements IUserService {

   @Autowired
    private UserMapper userMapper;
    @Autowired
    private IRedisService iRedisService;
    @Override
    public ServerResponse login(HttpServletResponse httpServletResponse,String username, String password){

        String decUsername = null;
        try {
            decUsername = new String(username.getBytes("ISO8859-1"), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int resultCount = userMapper.checkUsername(decUsername);

        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        //要转化为md5才能与数据库的值对应上
        String md5Password = MD5Util.inputPassToDbPass(password, PropertiesUtil.getProperty("passwordDB.salt"));

        User user=userMapper.checkUsernameByPassword(decUsername,md5Password);
        if(user == null){
            return ServerResponse.createBySuccessMessage("密码有误");
        }
        System.out.println("密码正确");
        String token = UUID.randomUUID().toString().replace("-","");
        System.out.println(token);
        iRedisService.set(Const.COOKI_NAME_TOKEN,token,user);
        addCookie(httpServletResponse,token);
        return ServerResponse.createBySuccess("登陆成功",user);

    }

    private void addCookie(HttpServletResponse response, String token){
        Cookie cookie = new Cookie(Const.COOKI_NAME_TOKEN,token);
        System.out.println("设置cookie");
        cookie.setMaxAge(12*60*60);
        cookie.setPath("/");

        response.addCookie(cookie);
    }
    //方法不做转中文的操作，在调用者做转码
    @Override
    public ServerResponse checkValid(String value, String type) {
        //验证传下来的是否为null??
        if(org.apache.commons.lang3.StringUtils.isNotBlank(type)){
            if(Const.USERNAME.equals(type)){
                System.out.println("checkValid");
                System.out.println(value);

                System.out.println(userMapper);
                int resultCount = userMapper.checkUsername(value);

                System.out.println(resultCount);
                if(resultCount > 0){
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if(Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(value);
                if(resultCount > 0){
                    return ServerResponse.createByErrorMessage("邮箱已存在");
                }
            }
            return ServerResponse.createBySuccessMessage("校验成功(用户名或邮箱不存在)");
        }
       return ServerResponse.createByErrorMessage("校验失败：传入的参数为null或空字符或空格");
    }

    @Override
    public ServerResponse regist(User user){

        if(StringUtils.isBlank(user.getUsername())|| StringUtils.isBlank(user.getQuestion())||StringUtils.isBlank(user.getAnswer())){
                return ServerResponse.createByErrorMessage("用户名或问题或答案不能为空");
        }
        //验证用户名是否重复
         ServerResponse response = this.checkValid(user.getUsername(),Const.USERNAME);
        if(!response.isSuccess()){
            return response;
        }
        //验证邮箱的有效性
        response = this.checkValid(user.getEmail(),Const.EMAIL);
        if(!response.isSuccess()){
              return response;
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);
        String md5Password = MD5Util.inputPassToDbPass(user.getPassword(),PropertiesUtil.getProperty("passwordDB.salt"));
        user.setPassword(md5Password);

        String decUsername = null;
        String decQuestion = null;
        String decAnswer = null;
        try {
            decUsername = new String(user.getUsername().getBytes("ISO8859-1"), "utf-8");
            decQuestion = new String(user.getQuestion().getBytes("ISO8859-1"), "utf-8");
            decAnswer = new String(user.getAnswer().getBytes("ISO8859-1"), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        user.setUsername(decUsername);
        user.setAnswer(decAnswer);
        user.setQuestion(decQuestion);

        int resultCount = userMapper.insert(user);

        if(resultCount == 0){
             return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    @Override
    public ServerResponse getQuestionByUsername(String username) {
        if(org.apache.commons.lang3.StringUtils.isNotBlank(username)){
            String decUsername = null;
            try {
                decUsername = new String(username.getBytes("ISO8859-1"), "utf-8");
                System.out.println("getQuestionByUsername");
                System.out.println(decUsername);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            ServerResponse response = this.checkValid(decUsername,Const.USERNAME);
            if(response.isSuccess()){
                return ServerResponse.createByErrorMessage("用户名不存在");
            }
            String question = userMapper.selectQuestionByUsername(decUsername);
            return ServerResponse.createBySuccessData(question);
        }
        return ServerResponse.createByErrorMessage("用户名为null或空格或空字符穿");
    }

    @Override
   public ServerResponse checkAnswer(String username, String question, String answer){
        String decQuestion = null;
        String decAnswer = null;
        String decUsername = null;
        try {
            decUsername = new String(username.getBytes("ISO8859-1"), "utf-8");
            decQuestion = new String(question.getBytes("ISO8859-1"), "utf-8");
            decAnswer = new String(answer.getBytes("ISO8859-1"), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int resultCount = userMapper.checkAnswer(decUsername,decQuestion,decAnswer);
        System.out.println("check answer");
        System.out.println(resultCount);
           if(resultCount > 0){
               String forgetToken = UUID.randomUUID().toString();
               System.out.println("checkAnswer token");
               System.out.println(forgetToken);
               TokenCache.setKey(TokenCache.TOKEN_PREFIX+decUsername,forgetToken);
               return ServerResponse.createBySuccess("查询成功",forgetToken);
           }


       return ServerResponse.createByErrorMessage("校验失败，用户名有误");

   }
    //更新密码需要验证传下来的forget_token
    public ServerResponse resetPassword(String username, String passwordNew, String forget_token){
        if(org.apache.commons.lang3.StringUtils.isBlank(username)){
            return ServerResponse.createByErrorMessage("用户名有误");
        }
        String decUsername = null;
        try {
            decUsername = new String(username.getBytes("ISO8859-1"), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+decUsername);
        if(org.apache.commons.lang3.StringUtils.isBlank(forget_token)){
            return ServerResponse.createByErrorMessage("token无效或过期");
        }

        if(org.apache.commons.lang3.StringUtils.equals(token,forget_token)){
            String md5Password  = MD5Util1111.MD5EncodeUtf8(passwordNew);
               int resultCount = userMapper.updatePassword(decUsername,md5Password);
               if(resultCount > 0){
                   return ServerResponse.createBySuccessMessage("更新密码成功");
               }
        }else{
            return ServerResponse.createByErrorMessage("token不相等");
        }
        return ServerResponse.createByErrorMessage("重置密码失败");

    }

    public ServerResponse checkAdminRole(User user){
       if(user != null && user.getRole() == Const.Role.ROLE_ADMIN){
           return ServerResponse.createBySuccessMessage("是管理员");
       }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),"不是管理员");
    }

    public ServerResponse getUserInfoFromRedis(String tokenValue){

        if(tokenValue == null){
            return ServerResponse.createByErrorMessage("token为空");
        }
        User user = iRedisService.getUser(tokenValue);
        if(user == null){
            return ServerResponse.createByErrorMessage("token为空");
        }
        return ServerResponse.createBySuccessData(user);
    }

    public ServerResponse createValidateCode(HttpServletRequest request, HttpServletResponse response, String phone){
        response.setContentType("image/jpeg");// 设置相应类型,告诉浏览器输出的内容为图片
        response.setHeader("Pragma", "No-cache");// 设置响应头信息，告诉浏览器不要缓存此内容
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Set-Cookie", "name=value; HttpOnly");//设置HttpOnly属性,防止Xss攻击
        response.setDateHeader("Expire", 0);
        int random = (int)((Math.random()*9+1)*100000);
        String randomCode = random+"";
        //将验证码缓存到GUAVA
        LoginValidateCodeCache.setValue(randomCode);

        //将信息（包含手机号码，和短信内容：验证码）发送到消息中间件
        Joiner joiner = Joiner.on("");
        String msg = joiner.join(phone , "," ,randomCode);
        PhoneValidateCodeMessageProducer phoneValidateCodeMessageProducer = new PhoneValidateCodeMessageProducer();
        phoneValidateCodeMessageProducer.sendQueueMessage(msg);


        return ServerResponse.createBySuccess();
    }

    public ServerResponse checkPhonebumber(String number){
        int result = userMapper.checkPhone(number);
        if(result > 0){
            return ServerResponse.createBySuccessMessage("手机号码存在");
        }
        return ServerResponse.createByErrorMessage("手机号码不存在");
    }

    public ServerResponse loginByValidateCode(String phone, String validateCode){
         String cacheCode = LoginValidateCodeCache.getKey(LoginValidateCodeCache.PREFIX);
         if(validateCode.equals(cacheCode)){
             User user = userMapper.selectUserByPhone(phone);
             if ( user == null ) {
                 return ServerResponse.createByErrorMessage("登陆失败");
             }
             return ServerResponse.createBySuccess("登陆成功",user);
         }
         return ServerResponse.createByErrorMessage("登陆失败");
    }
}
