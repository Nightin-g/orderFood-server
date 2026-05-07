package com.example.orderfood.demos.web.Controller;

import com.example.orderfood.demos.web.DTO.AdminLoginDTO;
import com.example.orderfood.demos.web.model.Dish;
import com.example.orderfood.demos.web.model.Shop;
import com.example.orderfood.demos.web.service.AdminService;
import com.example.orderfood.demos.web.service.DishService;
import com.example.orderfood.demos.web.service.ShopService;
import com.example.orderfood.demos.web.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

/**
 * 管理员控制器
 * 提供管理员登录、审核相关的接口
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private DishService dishService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private AdminService adminService;

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public R adminLogin(@RequestBody AdminLoginDTO adminLoginDTO) {
        return adminService.adminLogin(adminLoginDTO);
    }

    /**
     * 查询未审核的菜品
     */
    @GetMapping("/dishes/pending")
    public R getPendingReviewDishes() {
        List<Dish> dishes = dishService.getPendingReviewDishes();
        return R.success("查询未审核菜品成功", dishes);
    }

    /**
     * 查询未审核的店铺
     */
    @GetMapping("/shops/pending")
    public R getPendingReviewShops() {
        List<Shop> shops = shopService.getPendingReviewShops();
        return R.success("查询未审核店铺成功", shops);
    }

    /**
     * 审核菜品通过
     */
    @PutMapping("/dishes/{dishId}/approve")
    public R approveDish(@PathVariable("dishId") BigInteger dishId) {
        return dishService.approveDish(dishId);
    }

    /**
     * 审核菜品不通过
     */
    @PutMapping("/dishes/{dishId}/reject")
    public R rejectDish(@PathVariable("dishId") BigInteger dishId) {
        return dishService.rejectDish(dishId);
    }

    /**
     * 审核店铺通过
     */
    @PutMapping("/shops/{shopId}/approve")
    public R approveShop(@PathVariable("shopId") BigInteger shopId) {
        return shopService.approveShop(shopId);
    }

    /**
     * 审核店铺不通过
     */
    @PutMapping("/shops/{shopId}/reject")
    public R rejectShop(@PathVariable("shopId") BigInteger shopId) {
        return shopService.rejectShop(shopId);
    }
}