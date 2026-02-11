package cn.bugstack.domain.order.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: doom
 * @Date: 2026/02/11/16:13
 * @Description:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketPayDiscountEntity {
    /**
     * 订单原始价格
     */
    private BigDecimal originalPrice;
    /**
     * 订单优惠价格
     */
    private BigDecimal deductionPrice;
    /**
     * 订单支付价格
     */
    private BigDecimal payPrice;
}
