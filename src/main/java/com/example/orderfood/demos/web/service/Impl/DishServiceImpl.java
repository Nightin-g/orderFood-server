package com.example.orderfood.demos.web.service.Impl;

import com.example.orderfood.demos.web.DTO.DishInsertDTO;
import com.example.orderfood.demos.web.enums.DishStatusEnum;
import com.example.orderfood.demos.web.mapper.DishMapper;
import com.example.orderfood.demos.web.mapper.ShopMapper;
import com.example.orderfood.demos.web.model.Dish;
import com.example.orderfood.demos.web.model.Shop;
import com.example.orderfood.demos.web.service.DishService;
import com.example.orderfood.demos.web.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private ShopMapper shopMapper;

    @Override
    public List<Dish> getPendingReviewDishes() {
        return dishMapper.selectPendingReview();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R approveDish(BigInteger dishId) {
        Dish dish = dishMapper.selectById(dishId);
        if (dish == null) {
            return R.error(404, "菜品不存在");
        }

        if (dish.getDishStatus() != DishStatusEnum.PENDING_REVIEW.getCode()) {
            return R.error(400, "菜品状态不是待审核");
        }

        dish.setDishStatus(DishStatusEnum.ON_SHELF.getCode());
        int result = dishMapper.updateById(dish);
        if (result <= 0) {
            return R.error(500, "审核通过失败");
        }

        return R.success("审核通过成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R rejectDish(BigInteger dishId) {
        Dish dish = dishMapper.selectById(dishId);
        if (dish == null) {
            return R.error(404, "菜品不存在");
        }

        if (dish.getDishStatus() != DishStatusEnum.PENDING_REVIEW.getCode()) {
            return R.error(400, "菜品状态不是待审核");
        }

        dish.setDishStatus(DishStatusEnum.REVIEW_FAILED.getCode());
        int result = dishMapper.updateById(dish);
        if (result <= 0) {
            return R.error(500, "审核不通过失败");
        }

        return R.success("审核不通过成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R addDish(DishInsertDTO dishInsertDTO) {
        Long shopIdLong = (Long) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BigInteger shopId = BigInteger.valueOf(shopIdLong);

        Dish dish = new Dish();
        dish.setDishName(dishInsertDTO.getDishName());
        dish.setCategoryId(dishInsertDTO.getCategoryId());
        dish.setPrice(dishInsertDTO.getPrice());
        dish.setIngredients(dishInsertDTO.getIngredients());
        dish.setDishPhoto(dishInsertDTO.getDishPhoto());
        dish.setDishPhotoId(dishInsertDTO.getDishPhotoId());
        dish.setDishStatus(DishStatusEnum.PENDING_REVIEW.getCode());
        dish.setForSale(0);
        dish.setDishSales(BigInteger.ZERO);
        dish.setDishScore(new java.math.BigDecimal(0));
        dish.setShopId(shopId);
        dish.setDishScorePerson(0);
        dish.setCreateTime(java.time.LocalDateTime.now());

        int result = dishMapper.insert(dish);
        if (result <= 0) {
            return R.error(500, "添加菜品失败，请稍后重试");
        }

        return R.success("添加菜品成功，等待审核");
    }

    @Override
    public List<Dish> getDishesByShopId(BigInteger shopId) {
        Dish dish = new Dish();
        dish.setShopId(shopId);
        return dishMapper.selectByExample(dish);
    }

    @Override
    public List<Dish> getDishesByCategoryId(BigInteger categoryId) {
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        return dishMapper.selectByExample(dish);
    }

    @Override
    public List<Dish> getOnShelfDishes() {
        Dish dish = new Dish();
        dish.setDishStatus(DishStatusEnum.ON_SHELF.getCode());
        return dishMapper.selectByExample(dish);
    }

    @Override
    public Dish getDishById(BigInteger dishId) {
        return dishMapper.selectById(dishId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R updateDish(Dish dish) {
        Long shopIdLong = (Long) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BigInteger shopId = BigInteger.valueOf(shopIdLong);

        Dish existingDish = dishMapper.selectById(dish.getDishId());
        if (existingDish == null) {
            return R.error(404, "菜品不存在");
        }

        if (!existingDish.getShopId().equals(shopId)) {
            return R.error(403, "无权操作该菜品");
        }

        int result = dishMapper.updateSelective(dish);
        if (result <= 0) {
            return R.error(500, "更新菜品失败");
        }

        return R.success("更新菜品成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R updateDishStatus(BigInteger dishId, Integer status) {
        Long shopIdLong = (Long) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BigInteger shopId = BigInteger.valueOf(shopIdLong);

        Dish dish = dishMapper.selectById(dishId);
        if (dish == null) {
            return R.error(404, "菜品不存在");
        }

        if (!dish.getShopId().equals(shopId)) {
            return R.error(403, "无权操作该菜品");
        }

        DishStatusEnum statusEnum = DishStatusEnum.fromCode(status);
        if (statusEnum == null) {
            return R.error(400, "无效的菜品状态");
        }

        dish.setDishStatus(status);
        int result = dishMapper.updateById(dish);
        if (result <= 0) {
            return R.error(500, "更新菜品状态失败");
        }

        return R.success("更新菜品状态成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R deleteDish(BigInteger dishId) {
        Long shopIdLong = (Long) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BigInteger shopId = BigInteger.valueOf(shopIdLong);

        Dish dish = dishMapper.selectById(dishId);
        if (dish == null) {
            return R.error(404, "菜品不存在");
        }

        if (!dish.getShopId().equals(shopId)) {
            return R.error(403, "无权操作该菜品");
        }

        dish.setDishStatus(DishStatusEnum.DELETED.getCode());
        int result = dishMapper.updateById(dish);
        if (result <= 0) {
            return R.error(500, "删除菜品失败");
        }

        return R.success("删除菜品成功");
    }

    @Override
    public List<Dish> getAllDishes() {
        return dishMapper.selectAll();
    }
}