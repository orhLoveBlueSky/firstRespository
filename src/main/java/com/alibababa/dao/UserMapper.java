package com.alibababa.dao;

import com.alibababa.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
//    int deleteByPrimaryKey(Integer id);
//
//    int insert(User record);
//
//    int insertSelective(User record);
//
//    User selectByPrimaryKey(Integer id);
//
//    int updateByPrimaryKeySelective(User record);
//
//    int updateByPrimaryKey(User record);
    int checkUsername(String username);
    int checkEmail(String email);
    User checkUsernameByPassword(@Param("username") String username, @Param("password") String password);
      //增删改查的返回值都是int??
     int insert(User user);
    String selectQuestionByUsername(String username);
    int checkAnswer(@Param("username") String username,@Param("question") String question,@Param("answer") String answer);
    int updatePassword(@Param("username")String username, @Param("password")String password);
    List<User> selctUserByQuestion(String question);
    int checkPhone(@Param("phone") String phone);
    User selectUserByPhone(@Param("phone")String phone);
}