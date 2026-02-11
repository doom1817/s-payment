package cn.bugstack.domain.order.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: doom
 * @Date: 2026/02/11/17:01
 * @Description:
 */
@Getter
@AllArgsConstructor
public enum MarketTypeVO {
    NO_MARKET(0, "无营销"),
    GROUP_BUY_MARKET(1, "拼团营销");

    private Integer code;
    private String desc;

    public static MarketTypeVO valueOf(Integer code) {
        switch ( code){
            case 0:
                return NO_MARKET;
            case 1:
                return GROUP_BUY_MARKET;
        }
    throw new RuntimeException("营销类型不存在");
    }
}
