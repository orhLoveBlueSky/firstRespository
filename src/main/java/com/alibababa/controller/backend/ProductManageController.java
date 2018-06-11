package com.alibababa.controller.backend;

import com.alibababa.common.Const;
import com.alibababa.common.ResponseCode;
import com.alibababa.common.ServerResponse;
import com.alibababa.pojo.Product;
import com.alibababa.pojo.User;
import com.alibababa.service.IFileService;
import com.alibababa.service.IProductService;
import com.alibababa.service.IUserService;
import com.alibababa.util.PropertiesUtil;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;


@Controller
@RequestMapping("/manage/product")
public class  ProductManageController {
       @Autowired
      private IUserService iUserService;
       @Autowired
       private IFileService iFileService;
      @Autowired
      private IProductService iProductService;
    //可以保存商品也可以更新商品
    @RequestMapping(value = "productSave.do",method = RequestMethod.POST)
    @ResponseBody
      public ServerResponse productSave(HttpSession session, Product product) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请先登录");
        }
        ServerResponse res = iUserService.checkAdminRole(user);
        if (!res.isSuccess()) {
            return res;
        }
        return iProductService.saveOrUpdate(product);
    }


    //获取商品的详情，不只在商品表
    @RequestMapping(value = "detail.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getDetail(HttpSession session, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请先登录");
        }
        ServerResponse res = iUserService.checkAdminRole(user);
        if (!res.isSuccess()) {
            return res;
        }
        return iProductService.manageProductDetail(productId);
    }

    //设置商品的状态
    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId,Integer status){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");

        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.setSaleStatus(productId,status);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(HttpSession session, @RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
//        if(user == null){
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");
//        }
//        if(iUserService.checkAdminRole(user).isSuccess()){
            //path是绝对路径，servlet,windows平台和linux平台的绝对路径不一样，这方便，将路径写死好了
            //win10的ftp无权限访问，只上传到tomcat中，不到ftp
//            String path = request.getSession().getServletContext().getRealPath("upload");
              String path = "C:\\ideaWorkSpace\\dianshangpingtai\\out\\artifacts\\loveSky_war_exploded\\upload";
            System.out.println("request.getSession().getServletContext().getRealPath(\"upload\")");
            System.out.println(path);

            String targetFileName = iFileService.upload(file,path);

            System.out.println("targetFileName");
            System.out.println(targetFileName);

            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            System.out.println("url");
            System.out.println(url);

            Map fileMap = Maps.newHashMap();
            fileMap.put("uri",targetFileName);
            fileMap.put("url",url);
            return ServerResponse.createBySuccessData(fileMap);
//        }else{
//            return ServerResponse.createByErrorMessage("无权限操作");
//        }
    }

}
