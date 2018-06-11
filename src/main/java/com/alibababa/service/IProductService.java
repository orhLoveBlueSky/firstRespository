package com.alibababa.service;

import com.alibababa.common.ServerResponse;
import com.alibababa.pojo.Product;
import com.alibababa.vo.ProductDetailVo;

/**
 * Created by Administrator on 2018/3/26.
 */
public interface IProductService {
    ServerResponse saveOrUpdate(Product product);
    ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);
    ServerResponse<String> setSaleStatus(Integer productId,Integer status);
}
