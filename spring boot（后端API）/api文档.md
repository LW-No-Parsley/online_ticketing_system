以下是基于项目代码生成的API文档的Markdown版本：

```markdown
# API 文档

## 鉴权说明

所有API请求都需要在Header中添加Authorization字段进行鉴权，格式为：
```
Authorization: Bearer <JWT_TOKEN>
```

### 获取Token
1. 用户登录后返回的token可用于后续请求
2. 管理员登录后返回的token可用于管理员接口

### 接口权限要求
- 公共接口：无需token
- 用户接口：需要有效的用户token
- 管理员接口：需要有效的管理员token

## 1. 管理员相关接口

### 获取所有用户
- **URL**: `/api/admin/users`
- **方法**: `GET`
- **鉴权**: 需要管理员权限
- **描述**: 获取所有用户的基本信息。
- **响应**:
  ```json
[
{
"id": 1,
"username": "123",
"email": ""
},
{
"id": 2,
"username": "19162139967",
"email": ""
}
]
```

### 获取所有订单
- **URL**: `/api/admin/orders`
- **方法**: `GET`
- **鉴权**: 需要管理员权限
- **描述**: 获取所有订单的详细信息。
- **响应**:
  ```json
  [
    {
      "orderId": 1,
      "date": "2025-06-13",
      "time": "上午",
      "status": "待支付",
      "username": "user1",
      "totalAmount": 100,
      "tickets": [...]
    },
    ...
  ]
  ```

### 删除用户
- **URL**: `/api/admin/users/{id}`
- **方法**: `DELETE`
- **鉴权**: 需要管理员权限
- **描述**: 根据用户ID删除用户。
- **响应**:
  - 成功:
    ```json
    {
      "success": true,
      "message": "用户删除成功"
    }
    ```
  - 失败:
    ```json
    {
      "success": false,
      "message": "用户不存在"
    }
    ```

---

## 2. 订单相关接口

### 创建订单
- **URL**: `/api/orders/create`
- **方法**: `POST`
- **鉴权**: 需要用户登录
- **描述**: 创建新订单。
- **请求体**:
  ```json
  {
    "date": "2025-06-13",
    "time": "上午"
  }
  ```
- **响应**:
  ```json
  {
    "success": true,
    "message": "订单创建成功",
    "orderId": 1
  }
  ```

### 获取订单状态
- **URL**: `/api/orders/status/{username}`
- **方法**: `GET`
- **描述**: 根据订单ID获取订单状态。
- **响应**:
  - 成功:
    ```json
    {
      "success": true,
      "status": "待支付"
    }
    ```
  - 失败:
    ```json
    {
      "success": false,
      "message": "订单不存在"
    }
    ```

### 更新订单状态
- **URL**: `/api/orders/update-status`
- **方法**: `POST`
- **鉴权**: 需要管理员权限
- **描述**: 更新订单状态。
- **请求体**:
  ```json
  {
    "orderId": 1,
    "status": "已支付"
  }
  ```
- **响应**:
  - 成功:
    ```json
    {
      "success": true,
      "message": "订单状态更新成功"
    }
    ```
  - 失败:
    ```json
    {
      "success": false,
      "message": "订单不存在"
    }
    ```

### 提交订单
- **URL**: `/api/orders/submit-order`
- **方法**: `POST`
- **鉴权**: 需要用户登录
- **描述**: 提交订单。
- **请求体**:
  ```json
  {
    "username": "user1",
    "selectedDate": "2025-06-13",
    "selectedTime": "上午",
    "tickets": [...],
    "totalAmount": 100
  }
  ```
- **响应**:
  ```json
  {
    "success": true,
    "message": "订单提交成功"
  }
  ```

### 根据用户名获取订单详情
- **URL**: `/api/orders/details/{username}`
- **方法**: `GET`
- **鉴权**: 需要用户登录
- **描述**: 根据用户名获取订单详情。
- **响应**:
  ```json
  {
    "success": true,
    "orders": [...]
  }
  ```

---

## 3. 用户认证相关接口

### 用户注册
- **URL**: `/api/auth/register`
- **方法**: `POST`
- **描述**: 用户注册。
- **请求体**:
  ```json
  {
    "username": "user1",
    "password": "password123"
  }
  ```
- **响应**:
  - 成功:
    ```json
    {
      "success": true,
      "message": "注册成功"
    }
    ```
  - 失败:
    ```json
    {
      "success": false,
      "message": "用户名已存在"
    }
    ```

### 用户登录
- **URL**: `/api/user/login`
- **方法**: `POST`
- **描述**: 用户登录。
- **请求体**:
  ```json
  {
    "username": "user1",
    "password": "password123"
  }
  ```
- **响应**:
  - 成功:
    ```json
    {
      "success": true,
      "message": "登录成功",
      "token": "jwt-token"
    }
    ```
  - 失败:
    ```json
    {
      "success": false,
      "message": "用户名或密码错误"
    }
    ```

### 获取用户信息
- **URL**: `/api/auth/user-info`
- **方法**: `GET`
- **描述**: 根据Token获取用户信息。
- **请求参数**:
  - `token`: 用户的JWT Token。
- **响应**:
  ```json
  {
    "success": true,
    "username": "user1",
    "email": "user1@example.com"
  }
  ```

---

## 4. 轮播图相关接口

### 获取轮播图列表
- **URL**: `/api/carousel`
- **方法**: `GET`
- **描述**: 获取所有激活的轮播图
- **响应**:
  ```json
  [
    {
      "id": 1,
      "imageUrl": "/uploads/carousel/uuid_filename.jpg",
      "title": "轮播图标题",
      "displayOrder": 1
    }
  ]
  ```

### 管理员获取所有轮播图
- **URL**: `/api/admin/carousel`
- **方法**: `GET`
- **鉴权**: 需要管理员权限
- **描述**: 获取所有轮播图(包括未激活的)
- **响应**: 同上

### 管理员添加轮播图
- **URL**: `/api/admin/carousel`
- **方法**: `POST`
- **鉴权**: 需要管理员权限
- **请求参数**:
  - `file`: 图片文件
  - `title`: 轮播图标题
  - `isActive`: 是否激活(默认true)
  - `displayOrder`: 显示顺序(默认0)
- **响应**: 返回创建的轮播图信息

### 管理员更新轮播图
- **URL**: `/api/admin/carousel/{id}`
- **方法**: `PUT`
- **鉴权**: 需要管理员权限
- **请求参数**: 同添加接口，所有参数可选
- **响应**: 返回更新的轮播图信息

### 管理员删除轮播图
- **URL**: `/api/admin/carousel/{id}`
- **方法**: `DELETE`
- **鉴权**: 需要管理员权限
- **响应**: 204 No Content

## 5. 票务相关接口

### 票库存管理

#### 获取所有票库存
- **URL**: `/api/ticket-inventory`
- **方法**: `GET`
- **鉴权**: 需要管理员权限
- **描述**: 获取所有票库存信息
- **响应**:
  ```json
  [
    {
      "id": 1,
      "date": "2025-06-13",
      "time": "上午",
      "totalTickets": 100,
      "soldTickets": 50,
      "status": 1,
      "dayStatus": 0
    },
    {
      "id": 2,
      "date": "2025-06-13",
      "time": "下午",
      "totalTickets": 100,
      "soldTickets": 30,
      "status": 1,
      "dayStatus": 0
    }
  ]
  ```

#### 获取特定票库存
- **URL**: `/api/ticket-inventory/{id}`
- **方法**: `GET`
- **鉴权**: 需要管理员权限
- **描述**: 根据ID获取特定票库存信息
- **响应**:
  ```json
  {
    "id": 1,
    "date": "2025-06-13",
    "time": "上午",
    "totalTickets": 100,
    "soldTickets": 50,
    "status": 1,
    "dayStatus": 0
  }
  ```

#### 创建票库存
- **URL**: `/api/ticket-inventory`
- **方法**: `POST`
- **鉴权**: 需要管理员权限
- **描述**: 创建新的票库存记录
- **请求体**:
  ```json
  {
    "date": "2025-06-14",
    "time": "上午",
    "totalTickets": 100,
    "soldTickets": 0,
    "status": 1,
    "dayStatus": 0
  }
  ```
- **响应**:
  ```json
  {
    "id": 3,
    "date": "2025-06-14",
    "time": "上午",
    "totalTickets": 100,
    "soldTickets": 0,
    "status": 1,
    "dayStatus": 0
  }
  ```

#### 更新票库存
- **URL**: `/api/ticket-inventory/{id}`
- **方法**: `PUT`
- **鉴权**: 需要管理员权限
- **描述**: 更新票库存信息
- **请求体**:
  ```json
  {
    "date": "2025-06-14",
    "time": "上午",
    "totalTickets": 120,
    "soldTickets": 10,
    "status": 1,
    "dayStatus": 0
  }
  ```
- **响应**:
  ```json
  {
    "id": 3,
    "date": "2025-06-14",
    "time": "上午",
    "totalTickets": 120,
    "soldTickets": 10,
    "status": 1,
    "dayStatus": 0
  }
  ```

#### 删除票库存
- **URL**: `/api/ticket-inventory/{id}`
- **方法**: `DELETE`
- **鉴权**: 需要管理员权限
- **描述**: 删除票库存记录
- **响应**:
  - 成功: 204 No Content
  - 失败: 404 Not Found

### 购票
- **URL**: `/api/tickets/purchase`
- **方法**: `POST`
- **描述**: 用户购票。
- **请求体**:
  ```json
  {
    "date": "2025-06-13",
    "time": "上午"
  }
  ```
- **响应**:
  - 成功:
    ```json
    {
      "success": true,
      "message": "购票成功"
    }
    ```
  - 失败:
    ```json
    {
      "success": false,
      "message": "所选日期和时间的票已售罄"
    }
    ```

---

如果需要进一步补充或修改，请告诉我！
