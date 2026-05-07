# 大学食堂订餐系统 API 文档

---

## 目录

- [1. 概述](#1-概述)
- [2. 认证模块](#2-认证模块)
- [3. 普通用户模块](#3-普通用户模块)
- [4. 店铺用户模块](#4-店铺用户模块)
- [5. 管理员模块](#5-管理员模块)
- [6. 图片模块](#6-图片模块)
- [附录A：数据模型](#附录a数据模型)
- [附录B：状态码表](#附录b状态码表)

---

## 1. 概述

### 1.1 角色体系

| 角色 | 标识 | 对应表 | 注册方式 | 权限摘要 |
|------|------|--------|---------|---------|
| 普通用户 | `user` | `user` | 自主注册 | 浏览菜品/店铺、下单、发表评论、管理个人信息 |
| 店铺用户 | `shop` | `shop` | 自主注册，管理员审核后启用 | 管理店铺信息、变更营业状态、管理菜品、处理订单 |
| 管理员 | `admin` | `administrator` | 系统预置，不开放注册 | 审核店铺和菜品 |

默认管理员账号：`admin` / `123456`（应用启动时自动初始化）。

### 1.2 统一响应格式

所有接口返回 JSON，结构如下：

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": null,
  "extra": {}
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | Integer | 业务状态码，200 表示成功 |
| `msg` | String | 提示信息 |
| `data` | Object | 返回的业务数据，无数据时为 `null` |
| `extra` | Map | 附加字段，如登录接口返回 `token`、上传接口返回 `photoId` 等 |

**HTTP 状态码对照：**

| code | 含义 |
|------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未授权（账号不存在或密码错误） |
| 403 | 权限不足 / 账号受限 / 未登录 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 1.3 认证方式

#### 公开接口（无需 Token）

- `/user/login`、`/user/register`
- `/shop/login`、`/shop/register`
- `/admin/login`
- `/captcha/**`

#### 受保护接口

请求头携带 JWT：

```
Authorization: Bearer <token>
```

Token 在登录成功后从 `extra.token` 获取，有效期 **1 小时**，服务端以 `{role}:token:{id}` 格式存入 Redis。

#### 登录响应示例

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

#### 认证失败响应

```json
{ "code": 403, "msg": "未授权，请先登录", "data": null }
```

---

## 2. 认证模块

> 本节接口均为公开接口，无需 Token。

### 2.1 普通用户

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/user/login` | 用户登录 |
| POST | `/user/register` | 用户注册 |

**POST /user/login**

```
Content-Type: application/json

{
  "userAccount": "string (必填, 账号)",
  "password":    "string (必填, 密码)"
}
```

成功响应：`extra` 返回 `token` 与 `role: "user"`。

错误场景：

| 场景 | code | msg |
|------|------|-----|
| 账号不存在 | 401 | 用户不存在 |
| 密码错误 | 401 | 密码错误 |
| 账号被禁用 | 403 | 用户账号已被禁用 |

**POST /user/register**

```
Content-Type: application/json

{
  "userAccount":     "string (必填, 账号, ≥6位)",
  "password":        "string (必填, 密码, ≥6位)",
  "confirmPassword": "string (必填, 确认密码)",
  "phoneNum":        "string (必填, 手机号)",
  "userName":        "string (可选, 昵称, 默认=账号)"
}
```

错误场景：

| 场景 | code | msg |
|------|------|-----|
| 两次密码不一致 | 400 | 两次输入的密码不一致 |
| 账号/密码不足6位 | 400 | 账号/密码长度不能少于6位 |
| 账号已存在 | 400 | 账号已存在 |
| 并发冲突 | 500 | 系统繁忙，请稍后重试 |

### 2.2 店铺用户

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/shop/login` | 店铺登录（需验证码） |
| POST | `/shop/register` | 店铺注册（需验证码） |

**POST /shop/login**

```
Content-Type: application/json

{
  "shopAccount": "string (必填, 店铺账号)",
  "password":    "string (必填, 密码)",
  "captcha":     "string (必填, 验证码)",
  "captchaKey":  "string (必填, 验证码Key, 从 /captcha/generate 响应头获取)"
}
```

成功响应：`extra` 返回 `token` 与 `role: "shop"`。

错误场景：

| 场景 | code | msg |
|------|------|-----|
| 验证码错误/过期 | 400 | 验证码错误或已过期 |
| 账号不存在 | 401 | 店铺不存在 |
| 密码错误 | 401 | 密码错误 |
| 店铺审核中 | 403 | 店铺正在审核中 |
| 审核未通过 | 403 | 店铺审核未通过 |
| 已永久停业 | 403 | 店铺已永久停业 |

**POST /shop/register**

```
Content-Type: application/json

{
  "shopName":        "string (必填, 店铺名称)",
  "shopAccount":     "string (必填, 店铺账号, ≥6位)",
  "password":        "string (必填, 密码, ≥6位)",
  "confirmPassword": "string (必填, 确认密码)",
  "shopType":        "int (必填, 店铺类型)",
  "shopPhone":       "string (必填, 店铺电话)",
  "position":        "int (必填, 位置编码, 十位=食堂编号, 个位=楼层)",
  "captcha":         "string (必填, 验证码)",
  "captchaKey":      "string (必填, 验证码Key)"
}
```

注册后店铺状态为 **待审核**（shopStatus=0），需管理员审核通过后方可登录。

### 2.3 管理员

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/admin/login` | 管理员登录 |

管理员账号由系统预置，**不开放注册**。

**POST /admin/login**

```
Content-Type: application/json

{
  "adminAccount": "string (必填, 管理员账号)",
  "password":     "string (必填, 密码)"
}
```

成功响应：`extra` 返回 `token` 与 `role: "admin"`。

### 2.4 验证码

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/captcha/generate` | 生成验证码图片 |
| GET | `/captcha/validate` | 校验验证码 |

**GET /captcha/generate** — 返回 PNG 图片，响应头含 `Captcha-Key`（后续请求需携带）。

**GET /captcha/validate**

```
Query: ?captchaKey=xxx&captcha=xxx
```

校验不区分大小写，验证成功后 Key 立即失效（一次性）。

---

## 3. 普通用户模块

> **角色：`user`** | 以下接口均需携带普通用户 Token。

### 3.1 个人信息

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/user/me` | 获取当前登录用户信息 |
| PUT | `/user/update` | 修改个人信息 |
| POST | `/user/updatePwd` | 修改密码 |

**GET /user/me** — 从 Token 中提取用户 ID，返回 User 对象（不含密码字段）。

**PUT /user/update** — 从 Token 中提取用户 ID，无需传入。

```
{
  "userName":  "string (可选, 昵称)",
  "sex":       "int (可选, 0=未知 1=男 2=女)",
  "address":   "string (可选, 地址)",
  "userPhoto": "string (可选, 头像URL)",
  "phone":     "string (可选, 手机号)"
}
```

**POST /user/updatePwd** — 需验证码校验。

```
{
  "userId":     "BigInteger (必填, 用户ID)",
  "password":   "string (必填, 旧密码, ≥6位)",
  "newPassword":"string (必填, 新密码, ≥6位)",
  "captcha":    "string (必填, 验证码)",
  "captchaKey": "string (必填, 验证码Key)"
}
```

### 3.2 菜品浏览

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/dish/onShelf` | 查询所有上架菜品 |
| GET | `/dish/shop/{shopId}` | 按店铺查菜品 |
| GET | `/dish/category/{categoryId}` | 按分类查菜品 |
| GET | `/dish/{dishId}` | 查询菜品详情 |

以上接口返回 Dish 对象或列表，数据结构见 [附录A.3](#a3-菜品-dish)。

### 3.3 订单管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/order/create` | 创建订单 |
| GET | `/order/user` | 查询我的订单 |
| GET | `/order/detail/{orderId}` | 查询订单详情 |
| PUT | `/order/pay/{orderId}` | 支付订单 |
| PUT | `/order/cancel/{orderId}` | 取消订单 |
| PUT | `/order/pickup/{orderId}` | 确认取餐 |
| PUT | `/order/evaluate/{orderId}` | 评价订单 |
| PUT | `/order/refund/{orderId}` | 申请退款 |

**POST /order/create**

```
{
  "items": [
    {
      "dishId":   "BigInteger (必填, 菜品ID)",
      "dishName": "string (可选, 菜品名)",
      "quantity": "int (必填, 数量)",
      "price":    "BigDecimal (必填, 单价)"
    }
  ],
  "totalPrice": "BigDecimal (必填, 总价)",
  "address":    "string (必填, 配送地址)",
  "phone":      "string (必填, 联系电话)",
  "remark":     "string (可选, 备注)"
}
```

成功响应 `data` 包含 `orderId`、`orderNum`、`orderStatus`、`orderPrice`。

**PUT /order/evaluate/{orderId}**

```
Query: ?score=5&comment=很好吃
```

**PUT /order/refund/{orderId}**

```
Query: ?reason=送餐太慢
```

**订单状态流转（用户侧）：**

```
创建 → 待付款(0) → 支付 → 已付款(1) → ... → 待取餐(4) → 取餐 → 待评价(5) → 评价 → 已完成(6)
                                                      ↓
                                                 申请退款 → 退款中(8) → 已退款(9)
```

### 3.4 评论管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/comment/create` | 发表评论 |
| GET | `/comment/shop/{shopId}` | 查看店铺评论 |
| GET | `/comment/dish/{dishId}` | 查看菜品评论 |
| GET | `/comment/user` | 查看我的评论 |
| GET | `/comment/shop/{shopId}/score` | 店铺均分 |
| GET | `/comment/dish/{dishId}/score` | 菜品均分 |
| DELETE | `/comment/delete/{commentId}` | 删除评论 |

**POST /comment/create**

```
{
  "context": "string (必填, 评论内容)",
  "score":   "BigDecimal (必填, 评分)",
  "shopId":  "BigInteger (可选, 评价店铺时传)",
  "dishId":  "BigInteger (可选, 评价菜品时传)"
}
```

---

## 4. 店铺用户模块

> **角色：`shop`** | 需店铺 Token，且店铺状态已审核通过。

### 4.1 店铺信息

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/shop/me` | 获取当前登录店铺信息 |
| PUT | `/shop/update` | 修改店铺信息 |

**GET /shop/me** — 从 Token 提取店铺 ID，返回 Shop 对象（不含密码字段）。

**PUT /shop/update** — 从 Token 提取店铺 ID，只更新非空字段。

```
{
  "shopName":    "string (可选)",
  "shopType":    "int (可选)",
  "shopPhone":   "string (可选)",
  "deliveryFee": "BigDecimal (可选, 配送费)",
  "shopPhoto":   "string (可选, 店铺图片URL)",
  "operating":   "int (可选, 0=非营业 1=营业中)",
  "position":    "int (可选, 位置编码)"
}
```

### 4.2 营业状态

| 方法 | 路径 | 说明 |
|------|------|------|
| PUT | `/shop/status/open` | 开始营业 |
| PUT | `/shop/status/rest` | 进入休息 |
| PUT | `/shop/status/temporary-close` | 暂时歇业 |
| PUT | `/shop/status/resume` | 恢复营业 |
| PUT | `/shop/status/permanent-close` | 永久停业 |

以上接口均无请求体，从 Token 提取店铺 ID。

**状态转换规则：**

```
                    ┌─→ 休息中(2) ←────────────┐
                    │       ↓                   │
审核通过 → 休息中(2) │  暂时歇业(3)               │
                    │       ↓                   │
                    │  恢复 → 休息中(2)          │
                    │                            │
                    └─→ 营业中(1) ←── 休息中(2) / 暂时歇业(3) 可转为营业
                         ↓
                    休息(2) / 暂时歇业(3)
                         ↓
                    永久停业(4) ← 任意状态均可转入
```

### 4.3 菜品管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/shop/dishes` | 添加菜品 |
| PUT | `/dish` | 更新菜品信息 |
| PUT | `/dish/{dishId}/status` | 更新菜品状态 |
| DELETE | `/dish/{dishId}` | 删除菜品（逻辑删除） |

**POST /shop/dishes** — 添加后菜品状态为 **待审核**（dishStatus=0）。

```
{
  "dishName":    "string (必填, 菜品名称)",
  "categoryId":  "BigInteger (必填, 分类ID)",
  "price":       "BigDecimal (必填, 价格)",
  "ingredients": "string (可选, 食材成分)",
  "dishPhoto":   "string (可选, 菜品图片URL)",
  "dishPhotoId": "BigInteger (可选, 图片ID)"
}
```

**PUT /dish** — 更新菜品信息，传入 Dish 对象。

```
{ "dishId": ..., "dishName": ..., "price": ..., ... }
```

**PUT /dish/{dishId}/status** — 变更菜品状态。

```
Query: ?status=<状态码>
```

状态码：`1`=上架, `2`=售罄, `3`=暂时下架。

### 4.4 订单处理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/order/shop` | 查看店铺订单 |
| PUT | `/order/shop/accept/{orderId}` | 接单 |
| PUT | `/order/shop/reject/{orderId}` | 拒单 |
| PUT | `/order/shop/preparing/{orderId}` | 标记出餐 |
| PUT | `/order/shop/delivering/{orderId}` | 标记配送中 |
| PUT | `/order/shop/ready/{orderId}` | 标记待取餐 |

订单处理流程：

```
待付款(0) → 已付款(1) → 接单 → 待出餐(2) → 出餐 → 配送中(3) → 送达 → 待取餐(4)
                         ↓ 拒单
                       已取消(7)
```

---

## 5. 管理员模块

> **角色：`admin`** | 需管理员 Token。

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/admin/me` | 获取当前登录管理员信息 |
| GET | `/admin/dishes/pending` | 查询待审核菜品 |
| PUT | `/admin/dishes/{dishId}/approve` | 审核菜品通过 |
| PUT | `/admin/dishes/{dishId}/reject` | 审核菜品不通过 |
| GET | `/admin/shops/pending` | 查询待审核店铺 |
| PUT | `/admin/shops/{shopId}/approve` | 审核店铺通过 |
| PUT | `/admin/shops/{shopId}/reject` | 审核店铺不通过 |
| GET | `/dish/all` | 查看所有菜品（含已删除） |

审核菜品通过 → 菜品状态变为 `ON_SHELF`（已上架）。

审核菜品不通过 → 菜品状态变为 `REVIEW_FAILED`（审核未通过）。

审核店铺通过 → 店铺状态变为 `RESTING`（休息中），店铺用户可登录管理。

审核店铺不通过 → 店铺状态变为 `REVIEW_FAILED`（审核未通过）。

---

## 6. 图片模块

> 所有角色通用，需登录 Token。

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/photo/upload` | 上传图片 |
| GET | `/photo/{photoId}` | 获取图片信息 |
| GET | `/photo/list` | 按关联查图片列表 |
| DELETE | `/photo/{photoId}` | 删除图片 |

**POST /photo/upload** — `multipart/form-data`。

| 参数 | 类型 | 说明 |
|------|------|------|
| `file` | MultipartFile | 图片文件 |
| `type` | String | 图片类型（见下表） |
| `commentId` | BigInteger | 关联评论ID（可选） |
| `shopId` | BigInteger | 关联店铺ID（可选） |
| `dishId` | BigInteger | 关联菜品ID（可选） |
| `userId` | BigInteger | 关联用户ID（可选） |
| `activityId` | BigInteger | 关联活动ID（可选） |

文件限制：最大 10MB，仅允许 `image/jpeg`、`image/png`、`image/gif`。

`extra` 返回：`{"photoId": "...", "url": "/uploads/..."}`

**GET /photo/list**

```
Query: ?relationId=<关联ID>&type=<类型>
```

**图片类型：**

| type | 说明 |
|------|------|
| `activities` | 活动图片 |
| `avatars` | 头像 |
| `comments` | 评论图片 |
| `dishes` | 菜品图片 |
| `shops` | 店铺图片 |

---

## 附录A：数据模型

### A.1 用户 (User)

| 字段 | 类型 | 说明 |
|------|------|------|
| `userId` | BigInteger | 主键 |
| `userAccount` | String | 登录账号 |
| `password` | String | BCrypt 密文 |
| `userName` | String | 昵称 |
| `phoneNum` | String | 手机号 |
| `address` | String | 地址 |
| `sex` | Integer | 0=未知, 1=男, 2=女 |
| `userPhoto` | String | 头像 URL |
| `createTime` | LocalDateTime | 注册时间 |
| `userStatus` | Integer | 见附录 B.1 |

### A.2 店铺 (Shop)

| 字段 | 类型 | 说明 |
|------|------|------|
| `shopId` | BigInteger | 主键 |
| `shopName` | String | 店铺名称 |
| `shopAccount` | String | 登录账号 |
| `password` | String | BCrypt 密文 |
| `shopStatus` | Integer | 见附录 B.2 |
| `shopType` | Integer | 店铺类型 |
| `operating` | Integer | 0=非营业, 1=营业中 |
| `shopSales` | BigInteger | 累计销量 |
| `deliveryFee` | BigDecimal | 配送费 |
| `shopPhone` | String | 联系电话 |
| `position` | Integer | 位置编码 |
| `shopScore` | BigDecimal | 综合评分 |
| `shopScorePerson` | Integer | 评分人数 |
| `shopPhoto` | String | 店铺图片 URL |
| `shopPhotoId` | BigInteger | 图片 ID |
| `createTime` | LocalDateTime | 创建时间 |

### A.3 菜品 (Dish)

| 字段 | 类型 | 说明 |
|------|------|------|
| `dishId` | BigInteger | 主键 |
| `dishName` | String | 菜品名称 |
| `dishStatus` | Integer | 见附录 B.3 |
| `forSale` | Integer | 0=非在售, 1=在售 |
| `categoryId` | BigInteger | 所属分类 ID |
| `price` | BigDecimal | 单价 |
| `dishSales` | BigInteger | 销量 |
| `dishScore` | BigDecimal | 评分 |
| `dishScorePerson` | Integer | 评分人数 |
| `dishPhotoId` | BigInteger | 图片 ID |
| `dishPhoto` | String | 图片 URL |
| `ingredients` | String | 食材成分 |
| `createTime` | LocalDateTime | 创建时间 |
| `shopId` | BigInteger | 所属店铺 ID |

### A.4 订单 (Order)

| 字段 | 类型 | 说明 |
|------|------|------|
| `orderId` | BigInteger | 主键 |
| `orderNum` | String | 订单号 |
| `orderStatus` | Integer | 见附录 B.4 |
| `orderPrice` | BigDecimal | 订单金额 |
| `createTime` | LocalDateTime | 创建时间 |
| `finishTime` | LocalDateTime | 完成时间 |
| `userId` | BigInteger | 下单用户 ID |

**OrderItemDTO**（订单商品项，非持久化）：

| 字段 | 类型 | 说明 |
|------|------|------|
| `dishId` | BigInteger | 菜品 ID |
| `dishName` | String | 菜品名称 |
| `quantity` | Integer | 数量 |
| `price` | BigDecimal | 单价 |

### A.5 评论 (Comment)

| 字段 | 类型 | 说明 |
|------|------|------|
| `commentId` | BigInteger | 主键 |
| `context` | String | 评论内容 |
| `score` | BigDecimal | 评分 |
| `shopId` | BigInteger | 关联店铺（可选） |
| `userId` | BigInteger | 评论用户 |
| `dishId` | BigInteger | 关联菜品（可选） |
| `createTime` | LocalDateTime | 创建时间 |

### A.6 图片 (Photo)

| 字段 | 类型 | 说明 |
|------|------|------|
| `photoId` | BigInteger | 主键 |
| `url` | String | 访问路径 |
| `commentId` | BigInteger | 关联评论（可选） |
| `shopId` | BigInteger | 关联店铺（可选） |
| `dishId` | BigInteger | 关联菜品（可选） |
| `userId` | BigInteger | 关联用户（可选） |
| `activityId` | BigInteger | 关联活动（可选） |

---

## 附录B：状态码表

### B.1 用户状态

| 码 | 枚举 | 含义 |
|----|------|------|
| 0 | NORMAL | 正常 |
| 1 | RESTRICTED | 受限（禁用） |
| 2 | CANCELLED | 已注销 |

### B.2 店铺状态

| 码 | 枚举 | 含义 |
|----|------|------|
| 0 | PENDING_REVIEW | 待审核 |
| 1 | OPEN | 营业中 |
| 2 | RESTING | 休息中 |
| 3 | TEMPORARILY_CLOSED | 暂时歇业 |
| 4 | PERMANENTLY_CLOSED | 永久停业 |
| 5 | REVIEW_FAILED | 审核未通过 |

### B.3 菜品状态

| 码 | 枚举 | 含义 |
|----|------|------|
| 0 | PENDING_REVIEW | 待审核 |
| 1 | ON_SHELF | 已上架 |
| 2 | SOLD_OUT | 已售罄 |
| 3 | TEMPORARILY_OFF_SHELF | 暂时下架 |
| 4 | DELETED | 已删除（逻辑删除） |
| 5 | REVIEW_FAILED | 审核未通过 |

### B.4 订单状态

| 码 | 枚举 | 含义 |
|----|------|------|
| 0 | PENDING_PAYMENT | 待付款 |
| 1 | PAID | 已付款 |
| 2 | PREPARING | 待出餐 |
| 3 | DELIVERING | 配送中 |
| 4 | READY_FOR_PICKUP | 待取餐 |
| 5 | PICKED_UP_UNEVALUATED | 已取餐未评价 |
| 6 | EVALUATED | 已评价 |
| 7 | CANCELLED | 已取消 |
| 8 | REFUNDING | 退款中 |
| 9 | REFUNDED | 已退款 |

### B.5 分类状态

| 码 | 枚举 | 含义 |
|----|------|------|
| 0 | NOT_USED | 未启用 |
| 1 | IN_USE | 使用中 |
| 2 | TEMPORARILY_DISABLED | 暂不可用 |
