# S-Pay-Mall DDD 架构设计分析

## 一、项目概述

S-Pay-Mall 是一个基于 DDD（领域驱动设计）架构的小型支付系统项目，采用 Spring Boot + Maven 构建，实现了支付宝支付、微信登录、订单管理等功能。项目严格遵循 DDD 的分层架构思想，将业务逻辑与技术实现有效分离。

## 二、整体架构设计

### 2.1 模块划分

项目采用多模块 Maven 结构，包含以下核心模块：

```
s-pay-mall-ddd
├── s-pay-mall-app          # 应用层（启动入口）
├── s-pay-mall-domain       # 领域层（核心业务逻辑）
├── s-pay-mall-infrastructure # 基础设施层（技术实现）
├── s-pay-mall-trigger      # 接口层（触发器）
├── s-pay-mall-types        # 类型层（通用类型）
└── s-pay-mall-api          # API层（接口定义）
```

### 2.2 依赖关系

```
s-pay-mall-app (启动模块)
    ├── s-pay-mall-trigger
    ├── s-pay-mall-infrastructure
    └── s-pay-mall-domain
            └── s-pay-mall-types
```

**依赖原则**：
- 外层依赖内层，内层不依赖外层
- 领域层独立，不依赖任何其他业务模块
- 基础设施层实现领域层定义的接口

## 三、DDD 分层架构详解

### 3.1 领域层（Domain Layer）- s-pay-mall-domain

**职责**：封装核心业务逻辑，不依赖任何外部框架

#### 3.1.1 领域模型设计

**聚合根（Aggregate）**：
- [CreateOrderAggregate](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-domain\src\main\java\cn\bugstack\domain\order\model\aggregate\CreateOrderAggregate.java) - 订单创建聚合

```java
@Data
@Builder
public class CreateOrderAggregate {
    private String userId;              // 用户ID
    private ProductEntity productEntity; // 商品实体
    private OrderEntity orderEntity;     // 订单实体
}
```

**实体（Entity）**：
- [OrderEntity](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-domain\src\main\java\cn\bugstack\domain\order\model\entity\OrderEntity.java) - 订单实体
- [PayOrderEntity](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-domain\src\main\java\cn\bugstack\domain\order\model\entity\PayOrderEntity.java) - 支付单实体
- [ProductEntity](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-domain\src\main\java\cn\bugstack\domain\order\model\entity\ProductEntity.java) - 商品实体
- [ShopCartEntity](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-domain\src\main\java\cn\bugstack\domain\order\model\entity\ShopCartEntity.java) - 购物车实体

**值对象（Value Object）**：
- [OrderStatusVO](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-domain\src\main\java\cn\bugstack\domain\order\model\valobj\OrderStatusVO.java) - 订单状态枚举

```java
public enum OrderStatusVO {
    CREATE("CREATE", "创建完成"),
    PAY_WAIT("PAY_WAIT", "等待支付"),
    PAY_SUCCESS("PAY_SUCCESS", "支付成功"),
    DEAL_DONE("DEAL_DONE", "交易完成"),
    CLOSE("CLOSE", "超时关单")
}
```

#### 3.1.2 领域服务（Domain Service）

**抽象模板模式**：
- [AbstractOrderService](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-domain\src\main\java\cn\bugstack\domain\order\service\AbstractOrderService.java) - 抽象订单服务，定义下单标准流程
- [OrderService](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-domain\src\main\java\cn\bugstack\domain\order\service\OrderService.java) - 具体订单服务实现

**设计亮点**：
- 使用模板方法模式，在抽象类中定义下单的标准流程
- 具体实现类（如支付宝、微信）只需实现抽象方法
- 实现了开闭原则，易于扩展新的支付方式

```java
public abstract class AbstractOrderService implements IOrderService {
    // 标准下单流程
    public PayOrderEntity createOrder(ShopCartEntity shopCartEntity) {
        // 1. 查询未支付订单
        // 2. 查询商品 & 聚合订单
        // 3. 保存订单
        // 4. 创建支付单
    }

    // 抽象方法，由子类实现
    protected abstract void doSaveOrder(CreateOrderAggregate orderAggregate);
    protected abstract PayOrderEntity doPrepayOrder(...);
}
```

#### 3.1.3 领域事件（Domain Event）

- [PaySuccessMessageEvent](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-domain\src\main\java\cn\bugstack\domain\order\event\PaySuccessMessageEvent.java) - 支付成功事件

```java
@Component
public class PaySuccessMessageEvent extends BaseEvent<PaySuccessMessageEvent.PaySuccessMessage> {
    @Override
    public String topic() {
        return "pay_success";
    }
}
```

#### 3.1.4 仓储接口（Repository Interface）

- [IOrderRepository](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-domain\src\main\java\cn\bugstack\domain\order\adapter\repository\IOrderRepository.java) - 订单仓储接口

**设计理念**：
- 领域层定义接口，不关心具体实现
- 像厨师需要各种材料一样，通过接口（管道）获取所需资源
- 实现依赖倒置原则

#### 3.1.5 端口接口（Port Interface）

- [IProductPort](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-domain\src\main\java\cn\bugstack\domain\order\adapter\port\IProductPort.java) - 商品端口接口
- [ILoginPort](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-domain\src\main\java\cn\bugstack\domain\auth\adapter\port\ILoginPort.java) - 登录端口接口

### 3.2 基础设施层（Infrastructure Layer）- s-pay-mall-infrastructure

**职责**：提供技术实现，实现领域层定义的接口

#### 3.2.1 仓储实现（Repository Implementation）

- [OrderRepository](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-infrastructure\src\main\java\cn\bugstack\infrastructure\adapter\repository\OrderRepository.java) - 订单仓储实现

**功能特点**：
- 实现 IOrderRepository 接口
- 整合 DAO、Redis、EventBus 等技术组件
- 处理领域对象与持久化对象的转换

```java
@Repository
public class OrderRepository implements IOrderRepository {
    @Resource
    private IOrderDao orderDao;
    @Resource
    private IRedisService redisService;
    @Resource
    private EventBus eventBus;

    @Override
    public void changeOrderPaySuccess(String orderId) {
        // 更新数据库
        orderDao.changeOrderPaySuccess(order);
        // 发送MQ消息
        eventBus.post(JSON.toJSONString(paySuccessMessage));
    }
}
```

#### 3.2.2 端口实现（Port Implementation）

- [ProductPort](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-infrastructure\src\main\java\cn\bugstack\infrastructure\adapter\port\ProductPort.java) - 商品端口实现
- [LoginPort](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-infrastructure\src\main\java\cn\bugstack\infrastructure\adapter\port\LoginPort.java) - 登录端口实现

**适配器模式应用**：
- 将外部 RPC 调用适配为领域层需要的接口
- 实现领域实体与 DTO 的转换

```java
@Component
public class ProductPort implements IProductPort {
    private final ProductRPC productRPC;

    @Override
    public ProductEntity queryProductByProductId(String productId) {
        ProductDTO productDTO = productRPC.queryProductByProductId(productId);
        return ProductEntity.builder()
                .productId(productDTO.getProductId())
                .productName(productDTO.getProductName())
                .build();
    }
}
```

#### 3.2.3 数据访问层（DAO）

- [IOrderDao](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-infrastructure\src\main\java\cn\bugstack\infrastructure\dao\IOrderDao.java) - 订单数据访问接口
- [PayOrder](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-infrastructure\src\main\java\cn\bugstack\infrastructure\dao\po\PayOrder.java) - 订单持久化对象

**PO（Persistent Object）设计**：
- 与数据库表结构对应
- 包含缓存 key 生成方法
- 提供静态方法辅助缓存操作

```java
@Data
@Builder
public class PayOrder {
    private Long id;
    private String userId;
    private String productId;
    private String orderId;
    private String status;
    private String payUrl;

    public static String cacheKey(String userId, String orderId) {
        return "small_" + userId + "_" + orderId;
    }
}
```

#### 3.2.4 网关层（Gateway）

- [ProductRPC](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-infrastructure\src\main\java\cn\bugstack\infrastructure\gateway\ProductRPC.java) - 商品 RPC 调用
- [IWeixinApiService](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-infrastructure\src\main\java\cn\bugstack\infrastructure\gateway\IWeixinApiService.java) - 微信 API 服务

**技术栈**：
- 使用 Retrofit2 进行 HTTP 调用
- 封装第三方 API 接口

#### 3.2.5 缓存服务

- [IRedisService](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-infrastructure\src\main\java\cn\bugstack\infrastructure\redis\IRedisService.java) - Redis 接口
- [RedissonService](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-infrastructure\src\main\java\cn\bugstack\infrastructure\redis\RedissonService.java) - Redisson 实现

### 3.3 接口层（Interface Layer）- s-pay-mall-trigger

**职责**：接收外部请求，调用领域服务

#### 3.3.1 HTTP 控制器

- [AliPayController](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-trigger\src\main\java\cn\bugstack\trigger\http\AliPayController.java) - 支付宝支付控制器
- [LoginController](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-trigger\src\main\java\cn\bugstack\trigger\http\LoginController.java) - 登录控制器
- [WeixinPortalController](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-trigger\src\main\java\cn\bugstack\trigger\http\WeixinPortalController.java) - 微信门户控制器

**设计特点**：
- 控制器只负责接收请求和返回响应
- 业务逻辑委托给领域服务
- 处理支付回调验签等外部交互

```java
@RestController
public class AliPayController implements IPayService {
    @Resource
    private IOrderService orderService;

    @RequestMapping(value = "create_pay_order", method = RequestMethod.POST)
    public Response<String> createPayOrder(@RequestBody CreatePayRequestDTO dto) {
        PayOrderEntity payOrderEntity = orderService.createOrder(
            ShopCartEntity.builder()
                .userId(dto.getUserId())
                .productId(dto.getProductId())
                .build()
        );
        return Response.<String>builder()
            .data(payOrderEntity.getPayUrl())
            .build();
    }
}
```

#### 3.3.2 定时任务

- [TimeoutCloseOrderJob](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-trigger\src\main\java\cn\bugstack\trigger\job\TimeoutCloseOrderJob.java) - 超时关单任务
- [NoPayNotifyOrderJob](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-trigger\src\main\java\cn\bugstack\trigger\job\NoPayNotifyOrderJob.java) - 未支付通知任务

**定时任务设计**：
- 使用 Spring @Scheduled 注解
- 每 10 分钟执行一次
- 查询超时订单并关闭

```java
@Component
public class TimeoutCloseOrderJob {
    @Scheduled(cron = "0 0/10 * * * ?")
    public void exec() {
        List<String> orderIds = orderService.queryTimeoutCloseOrderList();
        for (String orderId : orderIds) {
            orderService.changeOrderClose(orderId);
        }
    }
}
```

#### 3.3.3 事件监听器

- [OrderPaySuccessListener](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-trigger\src\main\java\cn\bugstack\trigger\listener\OrderPaySuccessListener.java) - 支付成功监听器

**事件驱动架构**：
- 使用 Guava EventBus
- 监听支付成功事件
- 触发后续业务（发货、充值、开会员）

```java
@Slf4j
@Component
public class OrderPaySuccessListener {
    @Subscribe
    public void handleEvent(String paySuccessMessage) {
        log.info("收到支付成功消息，可以做接下来的事情了【发货、充值、开会员】");
    }
}
```

### 3.4 应用层（Application Layer）- s-pay-mall-app

**职责**：应用启动和配置管理

#### 3.4.1 启动类

- [Application](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-app\src\main\java\cn\bugstack\Application.java) - Spring Boot 启动类

#### 3.4.2 配置类

- [AliPayConfig](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-app\src\main\java\cn\bugstack\config\AliPayConfig.java) - 支付宝配置
- [RedisClientConfig](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-app\src\main\java\cn\bugstack\config\RedisClientConfig.java) - Redis 配置
- [Retrofit2Config](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-app\src\main\java\cn\bugstack\config\Retrofit2Config.java) - Retrofit2 配置
- [ThreadPoolConfig](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-app\src\main\java\cn\bugstack\config\ThreadPoolConfig.java) - 线程池配置
- [GuavaConfig](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-app\src\main\java\cn\bugstack\config\GuavaConfig.java) - Guava EventBus 配置

### 3.5 类型层（Types Layer）- s-pay-mall-types

**职责**：提供通用类型和工具类

#### 3.5.1 事件基类

- [BaseEvent](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-types\src\main\java\cn\bugstack\types\event\BaseEvent.java) - 领域事件基类

```java
public abstract class BaseEvent<T> {
    public abstract EventMessage<T> buildEventMessage(T data);
    public abstract String topic();

    @Data
    @Builder
    public static class EventMessage<T> {
        private String id;
        private Date timestamp;
        private T data;
    }
}
```

#### 3.5.2 枚举和常量

- [ResponseCode](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-types\src\main\java\cn\bugstack\types\enums\ResponseCode.java) - 响应码枚举
- [Constants](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-types\src\main\java\cn\bugstack\types\common\Constants.java) - 常量定义

#### 3.5.3 异常类

- [AppException](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-types\src\main\java\cn\bugstack\types\exception\AppException.java) - 应用异常

#### 3.5.4 SDK 工具类

- 微信 SDK 相关工具类（签名、XML 处理等）

### 3.6 API 层 - s-pay-mall-api

**职责**：定义对外接口

- [IPayService](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-api\src\main\java\cn\bugstack\api\IPayService.java) - 支付服务接口
- [CreatePayRequestDTO](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-api\src\main\java\cn\bugstack\api\dto\CreatePayRequestDTO.java) - 创建支付请求 DTO
- [Response](file:///d:\LongTime\s-pay-mall\s-pay-mall-ddd\s-pay-mall-api\src\main\java\cn\bugstack\api\response\Response.java) - 统一响应对象

## 四、DDD 核心设计模式

### 4.1 仓储模式（Repository Pattern）

**实现方式**：
- 领域层定义仓储接口（IOrderRepository）
- 基础设施层实现仓储接口（OrderRepository）
- 领域服务通过接口访问数据，不依赖具体实现

**优势**：
- 隔离领域逻辑与数据访问
- 便于单元测试（可 Mock 接口）
- 支持多种数据源切换

### 4.2 适配器模式（Adapter Pattern）

**应用场景**：
- Port 接口：领域层定义的端口接口
- Adapter 实现：基础设施层的适配器实现
- 将外部系统（RPC、API）适配为领域需要的接口

**示例**：
```
领域层：IProductPort（接口）
    ↓
基础设施层：ProductPort（适配器）
    ↓
外部系统：ProductRPC（RPC 调用）
```

### 4.3 模板方法模式（Template Method Pattern）

**应用场景**：
- AbstractOrderService 定义下单标准流程
- OrderService 实现具体支付逻辑
- 易于扩展新的支付方式（微信支付、银联支付等）

**优势**：
- 复用公共流程
- 变化部分由子类实现
- 符合开闭原则

### 4.4 事件驱动架构（Event-Driven Architecture）

**实现方式**：
- 领域事件：PaySuccessMessageEvent
- 事件总线：Guava EventBus
- 事件监听：OrderPaySuccessListener

**业务场景**：
- 支付成功后触发后续业务
- 解耦支付与发货、充值等业务
- 支持异步处理

### 4.5 依赖倒置原则（Dependency Inversion Principle）

**体现**：
- 领域层不依赖基础设施层
- 通过接口（Repository、Port）实现依赖倒置
- 高层模块（领域）不依赖低层模块（基础设施）

## 五、业务流程分析

### 5.1 创建订单流程

```
1. 用户请求创建订单
   ↓
2. AliPayController 接收请求
   ↓
3. 调用 OrderService.createOrder()
   ↓
4. AbstractOrderService 标准流程：
   a. 查询未支付订单（防止重复下单）
   b. 查询商品信息（通过 ProductPort）
   c. 创建订单聚合（CreateOrderAggregate）
   d. 保存订单（通过 IOrderRepository）
   e. 创建支付单（doPrepayOrder）
   ↓
5. OrderRepository 实现持久化：
   a. 保存到数据库（IOrderDao）
   b. 存入缓存（IRedisService）
   ↓
6. 返回支付 URL 给用户
```

### 5.2 支付回调流程

```
1. 支付宝回调通知
   ↓
2. AliPayController.payNotify() 接收回调
   ↓
3. 验证签名（AlipaySignature）
   ↓
4. 调用 OrderService.changeOrderPaySuccess()
   ↓
5. OrderRepository 更新订单状态：
   a. 更新数据库状态
   b. 发送支付成功事件（EventBus）
   ↓
6. OrderPaySuccessListener 监听事件
   ↓
7. 触发后续业务（发货、充值等）
```

### 5.3 超时关单流程

```
1. 定时任务触发（每 10 分钟）
   ↓
2. TimeoutCloseOrderJob.exec()
   ↓
3. 查询超时订单（超过 30 分钟未支付）
   ↓
4. 遍历订单并关闭
   ↓
5. OrderService.changeOrderClose()
   ↓
6. OrderRepository 更新订单状态为 CLOSE
```

## 六、DDD 设计亮点

### 6.1 清晰的分层架构

- **领域层**：纯粹的业务逻辑，无框架依赖
- **基础设施层**：技术实现，可替换
- **接口层**：薄层，只负责请求转发
- **应用层**：组装和配置

### 6.2 领域模型驱动

- 聚合根（CreateOrderAggregate）管理业务一致性
- 实体（Entity）具有唯一标识
- 值对象（Value Object）不可变
- 领域事件解耦业务

### 6.3 依赖方向正确

```
接口层 → 领域层 ← 基础设施层
         ↑
      类型层
```

- 外层依赖内层
- 领域层独立
- 基础设施层实现领域接口

### 6.4 可扩展性强

- 新增支付方式：继承 AbstractOrderService
- 新增数据源：实现 IOrderRepository
- 新增外部系统：实现 Port 接口
- 新增业务事件：扩展 Event 监听

### 6.5 可测试性好

- 领域逻辑独立，易于单元测试
- 接口可 Mock，便于测试
- 业务与技术分离

## 七、技术栈总结

| 层级 | 技术栈 |
|------|--------|
| 应用框架 | Spring Boot 2.7.12 |
| 持久化 | MyBatis + MySQL |
| 缓存 | Redis + Redisson |
| 支付 | 支付宝 SDK |
| HTTP 客户端 | Retrofit2 |
| 事件总线 | Guava EventBus |
| 工具库 | Guava、Fastjson、Commons Lang3 |
| 构建工具 | Maven |

## 八、总结

S-Pay-Mall 项目是一个典型的 DDD 架构实践案例，具有以下特点：

1. **严格的分层架构**：六层架构清晰，职责明确
2. **领域模型完整**：聚合根、实体、值对象、领域事件齐全
3. **设计模式丰富**：仓储、适配器、模板方法、事件驱动等
4. **依赖方向正确**：遵循依赖倒置原则
5. **可扩展性强**：易于添加新功能和新支付方式
6. **代码质量高**：命名规范，注释清晰，结构合理

该项目为小型支付系统提供了良好的 DDD 架构参考，适合学习和实践领域驱动设计思想。
