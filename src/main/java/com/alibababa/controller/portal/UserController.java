package com.alibababa.controller.portal;

import com.alibababa.common.Const;
import com.alibababa.common.ResponseCode;
import com.alibababa.common.ServerResponse;

import com.alibababa.pojo.User;
import com.alibababa.service.IUserService;
import com.alibababa.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;



@Controller
@RequestMapping(value="/user/")
public class UserController {

    @Autowired
    IUserService iUserService;




    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse login(String username, String password, HttpSession session, HttpServletResponse httpServletRespon) {

        ServerResponse response = iUserService.login(httpServletRespon, username, password);
        //登陆成功往创建session，并保存用户的信息
        if (response.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        User userData = (User) response.getData();
        UserVo userVo = new UserVo(userData.getUsername(), userData.getEmail(), userData.getPhone());
        response.setData(userVo);
        return response;
    }

    //此action怎么预防在浏览器的url输入呢
    @RequestMapping(value = "logout.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse logout(HttpSession session) {
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    //检查用户名或邮箱等的有效性，单个检查，这里只检查存在性，缺了数据格式的检查
    @RequestMapping(value = "checkValid.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse checkValid(String value, String type) {  //传null怎么办？？
        return iUserService.checkValid(value, type);

    }

    @RequestMapping(value = "regist.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> regist(User user) {
        return iUserService.regist(user);
    }

    @RequestMapping(value = "forget_get_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getQuestion(String username) {

        return iUserService.getQuestionByUsername(username);
    }

    @RequestMapping(value = "forget_check_answer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse checkAnswer(String username, String question, String answer) {
        return iUserService.checkAnswer(username, question, answer);
    }

    @RequestMapping(value = "forget_reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse resetPassword(String username, String passwordNew, String forget_token) {
        return iUserService.resetPassword(username, passwordNew, forget_token);
    }

    @RequestMapping(value = "getUserInfo.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse testCookieValue(HttpSession session, @CookieValue("JSESSIONID") String sessionId, @CookieValue("token") String tokenValue) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请先登录");
        }
        System.out.println("getUserInfo_Controller");
        System.out.println("testCookieValue,sessionId=" + sessionId);
        System.out.println("testCookieValue,token=" + tokenValue);

        return iUserService.getUserInfoFromRedis(tokenValue);
    }


    //通过手机号，和手机短信验证码登录,先验证手机号码是否存在，存在则生成验证码，否则发送错误信息
    @RequestMapping(value = "checkPhone.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse checkPhone(String phone) {
        return iUserService.checkPhonebumber(phone);
    }

    //手机存在才能获取验证码图片
    @RequestMapping(value = "getValidateCode.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getValidateCode(HttpServletRequest request, HttpServletResponse response,String phone) {

        return iUserService.createValidateCode(request, response, phone);
    }

    //通过验证码图片登陆
    @RequestMapping(value = "loginByValidatecode.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse loginByValidatecode(String phone, String validateCode,HttpSession session) {
        ServerResponse serverResponse = iUserService.loginByValidateCode(phone,validateCode);
        if(serverResponse.isSuccess()){
            System.out.println("登陆成功！");

                session.setAttribute(Const.CURRENT_USER, serverResponse.getData());



        }
        return serverResponse;
    }



}
