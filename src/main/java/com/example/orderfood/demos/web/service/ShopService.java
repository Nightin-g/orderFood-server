package com.example.orderfood.demos.web.service;

import com.example.orderfood.demos.web.DTO.ShopLoginDTO;
import com.example.orderfood.demos.web.DTO.ShopRegisterDTO;
import com.example.orderfood.demos.web.DTO.ShopUpdateDTO;
import com.example.orderfood.demos.web.model.Shop;
import com.example.orderfood.demos.web.util.R;

import java.math.BigInteger;
import java.util.List;

public interface ShopService {
    /**
     * 查询未审核的店铺
     */
    List<Shop> getPendingReviewShops();
    
    /**
     * 审核店铺通过
     */
    R approveShop(BigInteger shopId);
    
    /**
     * 审核店铺不通过
     */
    R rejectShop(BigInteger shopId);
    
    /**
     * 店铺注册
     */
    R shopRegister(ShopRegisterDTO shopRegisterDTO);
    
    /**
     * 店铺登录
     */
    R shopLogin(ShopLoginDTO shopLoginDTO);
    
    /**
     * 修改店铺信息
     */
    R updateShopInfo(ShopUpdateDTO shopUpdateDTO);
    
    /**
     * 店铺开始营业
     */
    R openShop();
    
    /**
     * 店铺进入休息
     */
    R restShop();
    
    /**
     * 店铺暂时歇业
     */
    R temporarilyCloseShop();
    
    /**
     * 店铺恢复营业（从暂时歇业到休息中）
     */
    R resumeShop();
    
    /**
     * 店铺永久停业
     */
    R permanentlyCloseShop();

    R getCurrentShop();

    /**
     * 获取所有已审核通过的店铺列表（公开访问）
     */
    List<Shop> getApprovedShops();

    /**
     * 获取单个店铺详情（公开访问）
     */
    R getShopDetail(BigInteger shopId);
}