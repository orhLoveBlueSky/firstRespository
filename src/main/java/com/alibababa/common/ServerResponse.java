package com.alibababa.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

//泛型构造的服务器端的响应对象
//这为什么要实现序列化接口，又不用持久化？
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> implements Serializable{
    private int status;
    private String msg;
    private T data;
    private ServerResponse(int status){
        this.status = status;
    }
    private ServerResponse(int status, String msg){
        this.msg = msg;
        this.status = status;
    }
    private ServerResponse(int status, T data){
        this.status=status;
        this.data = data;
    }
    private ServerResponse(int status, String msg,T data){
        this.status=status;
        this.msg = msg;
        this.data = data;
    }
    public void setData(T data){
        this.data = data;
    }
    //使之不再jason序列化的结果中，那要它来干什么
    @JsonIgnore
    public boolean isSuccess(){
        return this.status == ResponseCode.SUCCESS.getCode();
    }
    public int getStatus(){
        return status;
    }
    public T getData(){
        return data;
    }
    public String getMsg(){
        return msg;
    }

    public static <T> ServerResponse createBySuccess(){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }

    public static <T> ServerResponse<T> createBySuccessMessage(String msg){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
    }

    public static <T> ServerResponse<T> createBySuccessData(T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }

    public static <T> ServerResponse<T> createBySuccess(String msg,T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
    }


    public static <T> ServerResponse<T> createByError(){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
    }


    public static <T> ServerResponse createByErrorMessage(String errorMessage){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),errorMessage);
    }

    public static <T> ServerResponse createByErrorCodeMessage(int status,String msg){
        return new ServerResponse<T>(status,msg);
    }

    public static<T> ServerResponse<T> createByErrorData(T data) {
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),data);
    }


}
