package cn.bugstack.domain.order.model.entity;

import cn.bugstack.domain.order.model.valobj.MarketTypeVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 购物车实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShopCartEntity {

    /** 用户ID */
    private String userId;

    /** 商品ID */
    private String productId;
    /** 拼单组队ID */
    private String teamId;
    /** 活动ID */
    private Long activityId;
    /** 订单ID */
    private String orderId;
    /** 营销类型：0无营销，1拼团营销 */
    private MarketTypeVO marketTypeVO;


}
