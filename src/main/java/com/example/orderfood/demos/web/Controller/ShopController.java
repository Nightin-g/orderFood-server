package com.example.orderfood.demos.web.Controller;

import com.example.orderfood.demos.web.DTO.DishInsertDTO;
import com.example.orderfood.demos.web.DTO.ShopLoginDTO;
import com.example.orderfood.demos.web.DTO.ShopRegisterDTO;
import com.example.orderfood.demos.web.DTO.ShopUpdateDTO;
import com.example.orderfood.demos.web.model.Shop;
import com.example.orderfood.demos.web.service.DishService;
import com.example.orderfood.demos.web.service.ShopService;
import com.example.orderfood.demos.web.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

/**
 * 店铺控制器
 * 处理店铺注册、登录和信息修改等HTTP请求
 */
@RestController
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    private ShopService shopService;
    
    @Autowired
    private DishService dishService;

    /**
     * 获取当前登录店铺信息
     */
    @GetMapping("/me")
    public R getCurrentShop() {
        return shopService.getCurrentShop();
    }

    /**
     * 店铺注册
     */
    @PostMapping("/register")
    public R shopRegister(@RequestBody ShopRegisterDTO shopRegisterDTO) {
        return shopService.shopRegister(shopRegisterDTO);
    }

    /**
     * 店铺登录
     */
    @PostMapping("/login")
    public R shopLogin(@RequestBody ShopLoginDTO shopLoginDTO) {
        return shopService.shopLogin(shopLoginDTO);
    }

    /**
     * 修改店铺信息
     */
    @PutMapping("/update")
    public R updateShopInfo(@RequestBody ShopUpdateDTO shopUpdateDTO) {
        return shopService.updateShopInfo(shopUpdateDTO);
    }
    
    /**
     * 添加菜品
     */
    @PostMapping("/dishes")
    public R addDish(@RequestBody DishInsertDTO dishInsertDTO) {
        return dishService.addDish(dishInsertDTO);
    }
    
    /**
     * 店铺开始营业
     */
    @PutMapping("/status/open")
    public R openShop() {
        return shopService.openShop();
    }
    
    /**
     * 店铺进入休息
     */
    @PutMapping("/status/rest")
    public R restShop() {
        return shopService.restShop();
    }
    
    /**
     * 店铺暂时歇业
     */
    @PutMapping("/status/temporary-close")
    public R temporarilyCloseShop() {
        return shopService.temporarilyCloseShop();
    }
    
    /**
     * 店铺恢复营业
     */
    @PutMapping("/status/resume")
    public R resumeShop() {
        return shopService.resumeShop();
    }
    
    /**
     * 店铺永久停业
     */
    @PutMapping("/status/permanent-close")
    public R permanentlyCloseShop() {
        return shopService.permanentlyCloseShop();
    }

    /**
     * 获取所有已审核通过的店铺列表（公开访问）
     */
    @GetMapping("/list")
    public R getShopList() {
        List<Shop> shops = shopService.getApprovedShops();
        return R.success("查询店铺列表成功", shops);
    }

    /**
     * 获取单个店铺详情（公开访问）
     */
    @GetMapping("/detail/{shopId}")
    public R getShopDetail(@PathVariable("shopId") BigInteger shopId) {
        return shopService.getShopDetail(shopId);
    }
}