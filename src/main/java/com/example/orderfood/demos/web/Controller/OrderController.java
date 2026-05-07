package com.example.orderfood.demos.web.Controller;

import com.example.orderfood.demos.web.DTO.OrderCreateDTO;
import com.example.orderfood.demos.web.service.OrderService;
import com.example.orderfood.demos.web.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

/**
 * 订单控制器
 * 处理订单相关的HTTP请求
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping("/create")
    public R createOrder(@RequestBody OrderCreateDTO orderCreateDTO) {
        return orderService.createOrder(orderCreateDTO);
    }

    /**
     * 查询用户的所有订单
     */
    @GetMapping("/user")
    public R getUserOrders() {
        List<?> orders = orderService.getUserOrders();
        return R.success("查询用户订单成功", orders);
    }

    /**
     * 查询订单详情
     */
    @GetMapping("/detail/{orderId}")
    public R getOrderDetail(@PathVariable("orderId") BigInteger orderId) {
        return orderService.getOrderDetail(orderId);
    }

    /**
     * 取消订单
     */
    @PutMapping("/cancel/{orderId}")
    public R cancelOrder(@PathVariable("orderId") BigInteger orderId) {
        return orderService.cancelOrder(orderId);
    }

    /**
     * 支付订单
     */
    @PutMapping("/pay/{orderId}")
    public R payOrder(@PathVariable("orderId") BigInteger orderId) {
        return orderService.payOrder(orderId);
    }

    /**
     * 确认取餐
     */
    @PutMapping("/pickup/{orderId}")
    public R confirmPickup(@PathVariable("orderId") BigInteger orderId) {
        return orderService.confirmPickup(orderId);
    }

    /**
     * 评价订单
     */
    @PutMapping("/evaluate/{orderId}")
    public R evaluateOrder(@PathVariable("orderId") BigInteger orderId, 
                          @RequestParam("score") Integer score, 
                          @RequestParam("comment") String comment) {
        return orderService.evaluateOrder(orderId, score, comment);
    }

    /**
     * 申请退款
     */
    @PutMapping("/refund/{orderId}")
    public R applyRefund(@PathVariable("orderId") BigInteger orderId, 
                        @RequestParam("reason") String reason) {
        return orderService.applyRefund(orderId, reason);
    }

    /**
     * 查询店铺的所有订单
     */
    @GetMapping("/shop")
    public R getShopOrders() {
        List<?> orders = orderService.getShopOrders();
        return R.success("查询店铺订单成功", orders);
    }

    /**
     * 店铺接单
     */
    @PutMapping("/shop/accept/{orderId}")
    public R acceptOrder(@PathVariable("orderId") BigInteger orderId) {
        return orderService.acceptOrder(orderId);
    }

    /**
     * 店铺拒绝订单
     */
    @PutMapping("/shop/reject/{orderId}")
    public R rejectOrder(@PathVariable("orderId") BigInteger orderId) {
        return orderService.rejectOrder(orderId);
    }

    /**
     * 店铺标记为出餐
     */
    @PutMapping("/shop/preparing/{orderId}")
    public R markAsPreparing(@PathVariable("orderId") BigInteger orderId) {
        return orderService.markAsPreparing(orderId);
    }

    /**
     * 店铺标记为配送中
     */
    @PutMapping("/shop/delivering/{orderId}")
    public R markAsDelivering(@PathVariable("orderId") BigInteger orderId) {
        return orderService.markAsDelivering(orderId);
    }

    /**
     * 店铺标记为待取餐
     */
    @PutMapping("/shop/ready/{orderId}")
    public R markAsReadyForPickup(@PathVariable("orderId") BigInteger orderId) {
        return orderService.markAsReadyForPickup(orderId);
    }
}
