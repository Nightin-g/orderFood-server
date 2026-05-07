# 大学食堂订餐系统API文档

## 1. 用户接口

| 接口路径 | 方法 | 功能描述 | 请求参数 | 成功响应 |
|---------|------|---------|---------|---------|
| `/user/login` | POST | 用户登录 | `{"userAccount": "用户账号", "password": "密码"}` | `{"code": 200, "msg": "登录成功", "extra": {"token": "JWT令牌", "role": "user"}}` |
| `/user/register` | POST | 用户注册 | `{"userAccount": "用户账号", "password": "密码", "confirmPassword": "确认密码", "phoneNum": "手机号", "userName": "用户名(可选)"}` | `{"code": 200, "msg": "注册成功", "data": null}` |
| `/user/update` | PUT | 修改用户信息 | `{"userName": "用户名", "sex": 0, "address": "地址", "userPhoto": "头像URL", "phone": "手机号"}` | `{"code": 200, "msg": "更新成功", "data": null}` |
| `/user/updatePwd` | POST | 修改密码 | `{"userId": "用户ID", "password": "旧密码", "newPassword": "新密码", "captcha": "验证码", "captchaKey": "验证码密钥"}` | `{"code": 200, "msg": "密码更新成功", "data": null}` |

### 1.1 用户对象说明 (User)

```json
{
  "userId": "用户ID (BigInteger)",
  "userAccount": "用户账号",
  "password": "密码(加密)",
  "userName": "用户名",
  "phoneNum": "手机号",
  "address": "地址",
  "sex": "性别 (Integer, 0-未知 1-男 2-女)",
  "userPhoto": "用户头像URL",
  "createTime": "创建时间",
  "userStatus": "用户状态: 0-正常 1-受限 2-注销"
}
```

## 2. 店铺接口

| 接口路径 | 方法 | 功能描述 | 请求参数 | 成功响应 |
|---------|------|---------|---------|---------|
| `/shop/register` | POST | 店铺注册 | `{"shopName": "店铺名称", "shopAccount": "店铺账号", "password": "密码", "confirmPassword": "确认密码", "shopType": 0, "shopPhone": "店铺电话", "position": 0, "captcha": "验证码", "captchaKey": "验证码密钥"}` | `{"code": 200, "msg": "注册成功，等待审核", "data": null}` |
| `/shop/login` | POST | 店铺登录 | `{"shopAccount": "店铺账号", "password": "密码", "captcha": "验证码", "captchaKey": "验证码密钥"}` | `{"code": 200, "msg": "登录成功", "extra": {"token": "JWT令牌", "role": "shop"}}` |
| `/shop/update` | PUT | 修改店铺信息 | `{"shopName": "店铺名称", "shopType": 0, "shopPhone": "电话", "deliveryFee": 0.00, "shopPhoto": "图片URL", "operating": 0, "position": 0}` | `{"code": 200, "msg": "更新成功", "data": null}` |
| `/shop/dishes` | POST | 添加菜品 | `{"dishName": "菜品名称", "categoryId": "分类ID", "price": 0.00, "ingredients": "食材成分", "dishPhoto": "菜品图片URL", "dishPhotoId": "图片ID"}` | `{"code": 200, "msg": "添加菜品成功，等待审核", "data": null}` |
| `/shop/status/open` | PUT | 店铺开始营业 | 无 | `{"code": 200, "msg": "店铺已开始营业", "data": null}` |
| `/shop/status/rest` | PUT | 店铺进入休息 | 无 | `{"code": 200, "msg": "店铺已进入休息", "data": null}` |
| `/shop/status/temporary-close` | PUT | 店铺暂时歇业 | 无 | `{"code": 200, "msg": "店铺已暂时歇业", "data": null}` |
| `/shop/status/resume` | PUT | 店铺恢复营业 | 无 | `{"code": 200, "msg": "店铺已恢复营业", "data": null}` |
| `/shop/status/permanent-close` | PUT | 店铺永久停业 | 无 | `{"code": 200, "msg": "店铺已永久停业", "data": null}` |

### 2.1 店铺对象说明 (Shop)

```json
{
  "shopId": "店铺ID (BigInteger)",
  "shopName": "店铺名称",
  "shopAccount": "店铺账号",
  "password": "密码(加密)",
  "shopStatus": "店铺状态(见状态码表)",
  "shopType": "店铺类型 (Integer)",
  "operating": "是否营业中 (0-否, 1-是)",
  "shopSales": "店铺销量 (BigInteger)",
  "deliveryFee": "配送费 (BigDecimal)",
  "shopPhone": "店铺电话",
  "position": "位置编码 (Integer, 十位表示食堂，个位表示楼层)",
  "shopScore": "店铺评分 (BigDecimal)",
  "shopPhoto": "店铺图片URL",
  "shopPhotoId": "店铺图片ID",
  "createTime": "创建时间",
  "shopScorePerson": "评分人数 (Integer)"
}
```

### 2.2 店铺状态码说明

| 状态码 | 状态名称 | 描述 |
|-------|---------|------|
| 0 | PENDING_REVIEW | 待审核 |
| 1 | OPEN | 正在营业 |
| 2 | RESTING | 休息中 |
| 3 | TEMPORARILY_CLOSED | 暂时歇业 |
| 4 | PERMANENTLY_CLOSED | 永久停业 |
| 5 | REVIEW_FAILED | 审核未通过 |

## 3. 订单接口

| 接口路径 | 方法 | 功能描述 | 请求参数 | 成功响应 |
|---------|------|---------|---------|---------|
| `/order/create` | POST | 创建订单 | `{"items": [{"dishId": "菜品ID", "dishName": "菜品名称", "quantity": 1, "price": 0.00}], "totalPrice": 0.00, "address": "配送地址", "phone": "联系电话", "remark": "备注"}` | `{"code": 200, "msg": "订单创建成功", "data": {"orderId": "订单ID", "orderNum": "订单号", "orderStatus": 0, "orderPrice": 0.00}}` |
| `/order/user` | GET | 查询用户订单 | 无 | `{"code": 200, "msg": "查询用户订单成功", "data": [{"orderId": "订单ID", "orderNum": "订单号", "orderStatus": 0, "orderPrice": 0.00, "createTime": "创建时间", "finishTime": "完成时间"}]}` |
| `/order/detail/{orderId}` | GET | 查询订单详情 | `orderId`（路径参数） | `{"code": 200, "msg": "查询订单详情成功", "data": {"orderId": "订单ID", "orderNum": "订单号", "orderStatus": 0, "orderPrice": 0.00, "createTime": "创建时间", "finishTime": "完成时间", "userId": "用户ID"}}` |
| `/order/cancel/{orderId}` | PUT | 取消订单 | `orderId`（路径参数） | `{"code": 200, "msg": "订单已取消", "data": null}` |
| `/order/pay/{orderId}` | PUT | 支付订单 | `orderId`（路径参数） | `{"code": 200, "msg": "订单支付成功", "data": null}` |
| `/order/pickup/{orderId}` | PUT | 确认取餐 | `orderId`（路径参数） | `{"code": 200, "msg": "已确认取餐", "data": null}` |
| `/order/evaluate/{orderId}` | PUT | 评价订单 | `orderId`（路径参数），`score`（查询参数, Integer），`comment`（查询参数） | `{"code": 200, "msg": "评价成功", "data": null}` |
| `/order/refund/{orderId}` | PUT | 申请退款 | `orderId`（路径参数），`reason`（查询参数） | `{"code": 200, "msg": "退款申请已提交", "data": null}` |
| `/order/shop` | GET | 查询店铺订单 | 无 | `{"code": 200, "msg": "查询店铺订单成功", "data": [{"orderId": "订单ID", "orderNum": "订单号", "orderStatus": 0, "orderPrice": 0.00}]}` |
| `/order/shop/accept/{orderId}` | PUT | 店铺接单 | `orderId`（路径参数） | `{"code": 200, "msg": "订单已接单", "data": null}` |
| `/order/shop/reject/{orderId}` | PUT | 店铺拒绝订单 | `orderId`（路径参数） | `{"code": 200, "msg": "订单已拒绝", "data": null}` |
| `/order/shop/preparing/{orderId}` | PUT | 店铺标记为出餐 | `orderId`（路径参数） | `{"code": 200, "msg": "订单已标记为出餐", "data": null}` |
| `/order/shop/delivering/{orderId}` | PUT | 店铺标记为配送中 | `orderId`（路径参数） | `{"code": 200, "msg": "订单已标记为配送中", "data": null}` |
| `/order/shop/ready/{orderId}` | PUT | 店铺标记为待取餐 | `orderId`（路径参数） | `{"code": 200, "msg": "订单已标记为待取餐", "data": null}` |

### 3.1 订单对象说明 (Order)

```json
{
  "orderId": "订单ID (BigInteger)",
  "orderNum": "订单号",
  "orderStatus": "订单状态 (BigInteger, 见状态码表)",
  "orderPrice": "订单价格 (BigDecimal)",
  "createTime": "创建时间 (LocalDateTime)",
  "finishTime": "完成时间 (LocalDateTime)",
  "userId": "用户ID (BigInteger)"
}
```

### 3.2 订单状态码说明

| 状态码 | 状态名称 | 描述 |
|-------|---------|------|
| 0 | PENDING_PAYMENT | 待付款 |
| 1 | PAID | 已付款 |
| 2 | PREPARING | 待出餐 |
| 3 | DELIVERING | 配送中 |
| 4 | READY_FOR_PICKUP | 待取餐 |
| 5 | PICKED_UP_UNEVALUATED | 已取餐，未评价 |
| 6 | EVALUATED | 已评价 |
| 7 | CANCELLED | 已取消 |
| 8 | REFUNDING | 退款中 |
| 9 | REFUNDED | 已退款 |

### 3.3 订单商品项说明 (OrderItemDTO)

```json
{
  "dishId": "菜品ID (BigInteger)",
  "dishName": "菜品名称",
  "quantity": "数量 (Integer)",
  "price": "单价 (BigDecimal)"
}
```

## 4. 评论接口

| 接口路径 | 方法 | 功能描述 | 请求参数 | 成功响应 |
|---------|------|---------|---------|---------|
| `/comment/create` | POST | 创建评论 | `{"context": "评论内容", "score": 5.0, "shopId": "店铺ID(可选)", "dishId": "菜品ID(可选)"}` | `{"code": 200, "msg": "评论创建成功", "data": {"commentId": "评论ID", "context": "评论内容", "score": 5.0, "shopId": "店铺ID", "dishId": "菜品ID", "userId": "用户ID", "createTime": "创建时间"}}` |
| `/comment/shop/{shopId}` | GET | 查询店铺评论 | `shopId`（路径参数） | `{"code": 200, "msg": "查询店铺评论成功", "data": [{"commentId": "评论ID", "context": "评论内容", "score": 5.0, "shopId": "店铺ID", "userId": "用户ID", "dishId": "菜品ID", "createTime": "创建时间"}]}` |
| `/comment/dish/{dishId}` | GET | 查询菜品评论 | `dishId`（路径参数） | `{"code": 200, "msg": "查询菜品评论成功", "data": [{"commentId": "评论ID", "context": "评论内容", "score": 5.0, "shopId": "店铺ID", "userId": "用户ID", "dishId": "菜品ID", "createTime": "创建时间"}]}` |
| `/comment/user` | GET | 查询用户评论 | 无 | `{"code": 200, "msg": "查询用户评论成功", "data": [{"commentId": "评论ID", "context": "评论内容", "score": 5.0, "shopId": "店铺ID", "userId": "用户ID", "dishId": "菜品ID", "createTime": "创建时间"}]}` |
| `/comment/shop/{shopId}/score` | GET | 获取店铺平均评分 | `shopId`（路径参数） | `{"code": 200, "msg": "获取店铺平均评分成功", "data": 4.5}` |
| `/comment/dish/{dishId}/score` | GET | 获取菜品平均评分 | `dishId`（路径参数） | `{"code": 200, "msg": "获取菜品平均评分成功", "data": 4.5}` |
| `/comment/delete/{commentId}` | DELETE | 删除评论 | `commentId`（路径参数） | `{"code": 200, "msg": "评论删除成功", "data": null}` |

### 4.1 评论对象说明 (Comment)

```json
{
  "commentId": "评论ID (BigInteger)",
  "context": "评论内容",
  "score": "评分 (BigDecimal)",
  "shopId": "店铺ID (BigInteger, 可选)",
  "userId": "用户ID (BigInteger)",
  "dishId": "菜品ID (BigInteger, 可选)",
  "createTime": "创建时间 (LocalDateTime)"
}
```

## 5. 验证码接口

| 接口路径 | 方法 | 功能描述 | 请求参数 | 成功响应 |
|---------|------|---------|---------|---------|
| `/captcha/generate` | GET | 生成验证码 | 无 | 返回PNG图片（响应头: `Captcha-Key`, `Content-Type: image/png`） |
| `/captcha/validate` | GET | 验证验证码 | `captchaKey`（查询参数），`captcha`（查询参数） | `{"code": 200, "msg": "验证码验证成功", "data": null}` |

## 6. 菜品接口

| 接口路径 | 方法 | 功能描述 | 请求参数 | 成功响应 |
|---------|------|---------|---------|---------|
| `/dish/onShelf` | GET | 查询所有上架的菜品 | 无 | `{"code": 200, "msg": "查询上架菜品成功", "data": [{dishId, dishName, price, ...}]}` |
| `/dish/shop/{shopId}` | GET | 根据店铺查询菜品 | `shopId`（路径参数） | `{"code": 200, "msg": "查询店铺菜品成功", "data": [{dishId, dishName, price, ...}]}` |
| `/dish/category/{categoryId}` | GET | 根据分类查询菜品 | `categoryId`（路径参数） | `{"code": 200, "msg": "查询分类菜品成功", "data": [{dishId, dishName, price, ...}]}` |
| `/dish/{dishId}` | GET | 查询菜品详情 | `dishId`（路径参数） | `{"code": 200, "msg": "查询菜品详情成功", "data": {dishId, dishName, price, ...}}` |
| `/dish` | PUT | 更新菜品信息 | `Dish`对象（请求体，只需传需要更新的字段） | `{"code": 200, "msg": "更新菜品成功", "data": null}` |
| `/dish/{dishId}/status` | PUT | 更新菜品状态 | `dishId`（路径参数），`status`（查询参数, Integer） | `{"code": 200, "msg": "更新菜品状态成功", "data": null}` |
| `/dish/{dishId}` | DELETE | 删除菜品（逻辑删除） | `dishId`（路径参数） | `{"code": 200, "msg": "删除菜品成功", "data": null}` |
| `/dish/all` | GET | 查询所有菜品（管理员） | 无 | `{"code": 200, "msg": "查询所有菜品成功", "data": [{dishId, dishName, ...}]}` |

### 6.1 菜品对象说明 (Dish)

```json
{
  "dishId": "菜品ID (BigInteger)",
  "dishName": "菜品名称",
  "dishStatus": "菜品状态 (Integer, 见状态码表)",
  "forSale": "是否在售 (Integer, 0-否, 1-是)",
  "categoryId": "分类ID (BigInteger)",
  "price": "价格 (BigDecimal)",
  "dishSales": "销量 (BigInteger)",
  "dishScore": "评分 (BigDecimal)",
  "dishScorePerson": "评分人数 (Integer)",
  "dishPhotoId": "菜品图片ID (BigInteger)",
  "dishPhoto": "菜品图片URL",
  "ingredients": "食材成分",
  "createTime": "创建时间 (LocalDateTime)",
  "shopId": "店铺ID (BigInteger)"
}
```

### 6.2 菜品状态码说明

| 状态码 | 状态名称 | 描述 |
|-------|---------|------|
| 0 | PENDING_REVIEW | 待审核 |
| 1 | ON_SHELF | 已上架 |
| 2 | SOLD_OUT | 已售罄 |
| 3 | TEMPORARILY_OFF_SHELF | 暂时下架 |
| 4 | DELETED | 已删除 |
| 5 | REVIEW_FAILED | 审核未通过 |

## 7. 管理员接口

| 接口路径 | 方法 | 功能描述 | 请求参数 | 成功响应 |
|---------|------|---------|---------|---------|
| `/admin/login` | POST | 管理员登录 | `{"adminAccount": "管理员账号", "password": "密码"}` | `{"code": 200, "msg": "登录成功", "extra": {"token": "JWT令牌", "role": "admin"}}` |
| `/admin/dishes/pending` | GET | 查询未审核的菜品 | 无 | `{"code": 200, "msg": "查询未审核菜品成功", "data": [Dish对象列表]}` |
| `/admin/shops/pending` | GET | 查询未审核的店铺 | 无 | `{"code": 200, "msg": "查询未审核店铺成功", "data": [Shop对象列表]}` |
| `/admin/dishes/{dishId}/approve` | PUT | 审核菜品通过 | `dishId`（路径参数） | `{"code": 200, "msg": "审核通过成功", "data": null}` |
| `/admin/dishes/{dishId}/reject` | PUT | 审核菜品不通过 | `dishId`（路径参数） | `{"code": 200, "msg": "审核不通过成功", "data": null}` |
| `/admin/shops/{shopId}/approve` | PUT | 审核店铺通过 | `shopId`（路径参数） | `{"code": 200, "msg": "审核通过成功", "data": null}` |
| `/admin/shops/{shopId}/reject` | PUT | 审核店铺不通过 | `shopId`（路径参数） | `{"code": 200, "msg": "审核不通过成功", "data": null}` |

**注意：管理员账号不开放注册，由系统初始化（默认账号: admin / 123456）。**

## 8. 图片接口

| 接口路径 | 方法 | 功能描述 | 请求参数 | 成功响应 |
|---------|------|---------|---------|---------|
| `/photo/upload` | POST | 上传图片 | `file`（MultipartFile，表单文件），`type`（图片类型: activities/avatars/comments/dishes/shops），`commentId`（可选），`shopId`（可选），`dishId`（可选），`userId`（可选），`activityId`（可选） | `{"code": 200, "msg": "图片上传成功", "extra": {"photoId": "图片ID", "url": "图片访问路径"}}` |
| `/photo/{photoId}` | GET | 根据ID获取图片信息 | `photoId`（路径参数） | `{"code": 200, "msg": "获取图片成功", "data": {"photoId": "图片ID", "url": "图片路径", "commentId": "评论ID", "shopId": "店铺ID", "dishId": "菜品ID", "userId": "用户ID", "activityId": "活动ID"}}` |
| `/photo/list` | GET | 根据关联ID获取图片列表 | `relationId`（查询参数），`type`（查询参数: activities/avatars/comments/dishes/shops） | `{"code": 200, "msg": "获取图片列表成功", "data": [Photo对象列表]}` |
| `/photo/{photoId}` | DELETE | 删除图片 | `photoId`（路径参数） | `{"code": 200, "msg": "图片删除成功", "data": null}` |

### 8.1 图片对象说明 (Photo)

```json
{
  "photoId": "图片ID (BigInteger)",
  "commentId": "关联评论ID (BigInteger, 可选)",
  "shopId": "关联店铺ID (BigInteger, 可选)",
  "dishId": "关联菜品ID (BigInteger, 可选)",
  "userId": "关联用户ID (BigInteger, 可选)",
  "activityId": "关联活动ID (BigInteger, 可选)",
  "url": "图片访问路径"
}
```

### 8.2 图片类型说明

| 类型值 | 说明 |
|-------|------|
| `activities` | 活动图片 |
| `avatars` | 头像图片 |
| `comments` | 评论图片 |
| `dishes` | 菜品图片 |
| `shops` | 店铺图片 |

## 9. 响应格式说明

所有API接口的响应格式统一为：

```json
{
  "code": 200,
  "msg": "消息",
  "data": {},
  "extra": {}
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | Integer | 状态码 |
| `msg` | String | 返回消息 |
| `data` | Object | 返回数据，可为null |
| `extra` | Map<String, Object> | 额外参数，如上传接口返回的photoId、url等 |

### 状态码说明

| 状态码 | 说明 |
|-------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权（账号不存在或密码错误） |
| 403 | 权限不足或账号受限 |
| 404 | 资源不存在 |
| 500 | 系统内部错误 |

## 10. 角色体系

系统分为三种角色，分别对应不同的数据库表：

| 角色 | 表名 | 描述 |
|------|------|------|
| `user` | user | 普通用户 - 可注册、登录、订餐、查看菜品和评论 |
| `shop` | shop | 店铺用户 - 可注册、登录（需管理员审核后才能启用），管理店铺和菜品 |
| `admin` | administrator | 管理员 - 不开放注册，由系统初始化，负责审核店铺和菜品 |

### 10.1 公开接口

以下接口不需要认证即可访问：

- `/user/login`
- `/user/register`
- `/shop/login`
- `/shop/register`
- `/admin/login`
- `/captcha/**`

### 10.2 认证方式

除公开接口外，其他所有接口都需要在请求头中携带JWT令牌：

```
Authorization: Bearer {token}
```

### 10.3 登录流程

1. 根据角色选择对应的登录接口（`/user/login`、`/shop/login`、`/admin/login`）
2. 店铺登录需要先获取验证码：调用 `/captcha/generate`，从响应头获取 `Captcha-Key`
3. 调用登录接口，登录成功后响应 `extra` 中包含 `token` 和 `role`
4. token存入Redis（key格式: `user:token:{id}`、`shop:token:{id}`、`admin:token:{id}`），过期时间1小时
5. 后续请求在 `Authorization` 请求头中携带token

### 10.4 登录响应格式

```json
{
  "code": 200,
  "msg": "登录成功",
  "data": null,
  "extra": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "role": "user"
  }
}
```

### 10.5 用户状态枚举 (UserStatusEnum)

| 状态码 | 状态名称 | 描述 |
|-------|---------|------|
| 0 | NORMAL | 正常 |
| 1 | RESTRICTED | 受限 |
| 2 | CANCELLED | 注销 |

### 10.6 分类状态枚举 (CategoryStatusEnum)

| 状态码 | 状态名称 | 描述 |
|-------|---------|------|
| 0 | NOT_USED | 未被使用 |
| 1 | IN_USE | 正在使用 |
| 2 | TEMPORARILY_DISABLED | 暂时停用 |

### 10.7 认证失败响应

```json
{
  "code": 403,
  "msg": "未授权，请先登录",
  "data": null
}
```
