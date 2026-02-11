package cn.bugstack.infrastructure.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: doom
 * @Date: 2026/02/04/21:25
 * @Description:
 *    锁定聚合订单响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LockMarketPayOrderResponseDTO {
    //预购订单ID
    private String orderId;
    //原始价格
    private BigDecimal originalPrice;
    //折扣金额
    private BigDecimal deductionPrice;
    //支付金额
    private BigDecimal payPrice;
    //交易订单状态
    private Integer tradeOrderStatus;

}
