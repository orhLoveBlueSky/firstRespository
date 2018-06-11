package com.alibababa.service;

import com.alibababa.common.ServerResponse;
import com.alibababa.pojo.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface IUserService {
     public ServerResponse login(HttpServletResponse response, String username, String password);

     public ServerResponse checkValid(String value, String type);
     public ServerResponse regist(User user);
     public ServerResponse getQuestionByUsername(String username);
     public ServerResponse checkAnswer(String username, String question, String answer);
     public ServerResponse resetPassword(String passwordNew, String username, String forget_token);
     public ServerResponse checkAdminRole(User user);
     public ServerResponse getUserInfoFromRedis(String tokenValue);
     public ServerResponse createValidateCode(HttpServletRequest request, HttpServletResponse response,  String phone);
     public ServerResponse checkPhonebumber(String number);
     public ServerResponse loginByValidateCode(String phone, String validateCode);
     }

