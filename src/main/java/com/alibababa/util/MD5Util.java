package com.alibababa.util;


import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {

          //用前要先分清是表单传来的密码还是普通的输入
         public static String md5(String src){
             return DigestUtils.md5Hex(src);
         }
        //此盐值与前端加密用的一样
        private static final String salt = PropertiesUtil.getProperty("passwordOfFrontBack.salt");
        //非表单输入进行一次md5，模拟前端在表单密码加密
        public static String inputPassToFormPass(String inputPass){
            String str = ""+salt.charAt(0)+salt.charAt(2) + inputPass +salt.charAt(5) + salt.charAt(4);
            return md5(str);
        }
    //前端传来的经加密的密码，再一次md5存进数据库
    public static String formPassToDBPass(String formPass, String saltDB) {
        String str = ""+salt.charAt(0)+salt.charAt(2) + formPass +salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }
    //直接传进服务器，没经加密，一二步的和
    public static String inputPassToDbPass(String inputPass, String saltDB) {
        String formPass = inputPassToFormPass(inputPass);
        String dbPass = formPassToDBPass(formPass, saltDB);
        return dbPass;
    }
}
