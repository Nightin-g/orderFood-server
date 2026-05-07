package com.example.orderfood.demos.web.Controller;

import com.example.orderfood.demos.web.model.Dish;
import com.example.orderfood.demos.web.service.DishService;
import com.example.orderfood.demos.web.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

/**
 * 菜品控制器
 * 处理用户浏览菜品和店铺管理菜品的HTTP请求
 */
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * 查询所有上架的菜品（用户公开访问）
     */
    @GetMapping("/onShelf")
    public R getOnShelfDishes() {
        List<Dish> dishes = dishService.getOnShelfDishes();
        return R.success("查询上架菜品成功", dishes);
    }

    /**
     * 根据店铺查询菜品（用户公开访问）
     */
    @GetMapping("/shop/{shopId}")
    public R getDishesByShop(@PathVariable("shopId") BigInteger shopId) {
        List<Dish> dishes = dishService.getDishesByShopId(shopId);
        return R.success("查询店铺菜品成功", dishes);
    }

    /**
     * 根据分类查询菜品（用户公开访问）
     */
    @GetMapping("/category/{categoryId}")
    public R getDishesByCategory(@PathVariable("categoryId") BigInteger categoryId) {
        List<Dish> dishes = dishService.getDishesByCategoryId(categoryId);
        return R.success("查询分类菜品成功", dishes);
    }

    /**
     * 查询菜品详情（用户公开访问）
     */
    @GetMapping("/{dishId}")
    public R getDishById(@PathVariable("dishId") BigInteger dishId) {
        Dish dish = dishService.getDishById(dishId);
        if (dish == null) {
            return R.error(404, "菜品不存在");
        }
        return R.success("查询菜品详情成功", dish);
    }

    /**
     * 更新菜品信息（店铺权限）
     */
    @PutMapping
    public R updateDish(@RequestBody Dish dish) {
        return dishService.updateDish(dish);
    }

    /**
     * 更新菜品状态（店铺权限）
     * 用于上架、下架、售罄等状态操作
     */
    @PutMapping("/{dishId}/status")
    public R updateDishStatus(@PathVariable("dishId") BigInteger dishId, @RequestParam("status") Integer status) {
        return dishService.updateDishStatus(dishId, status);
    }

    /**
     * 删除菜品（店铺权限）
     * 逻辑删除，将菜品状态设为已删除
     */
    @DeleteMapping("/{dishId}")
    public R deleteDish(@PathVariable("dishId") BigInteger dishId) {
        return dishService.deleteDish(dishId);
    }

    /**
     * 查询所有菜品（管理员权限）
     */
    @GetMapping("/all")
    public R getAllDishes() {
        List<Dish> dishes = dishService.getAllDishes();
        return R.success("查询所有菜品成功", dishes);
    }
}
