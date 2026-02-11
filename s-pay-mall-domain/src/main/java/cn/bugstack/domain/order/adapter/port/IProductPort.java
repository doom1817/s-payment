package cn.bugstack.domain.order.adapter.port;

import cn.bugstack.domain.order.model.entity.MarketPayDiscountEntity;
import cn.bugstack.domain.order.model.entity.ProductEntity;

public interface IProductPort {

    /**
     * 模拟查询商品信息
     *
     * @param productId 商品ID
     * @return 商品实体对象
     */
    ProductEntity queryProductByProductId(String productId);

    /**
     * 锁定营销支付订单
     *
     * @param userId    用户ID
     * @param teamId    拼单组队ID
     * @param activityId 活动ID
     * @param productId 商品ID
     * @param orderId   订单ID
     * @return 锁定营销支付订单信息
     */
    MarketPayDiscountEntity lockMarketPayOrder(String userId, String teamId, Long activityId, String productId, String orderId);

}
