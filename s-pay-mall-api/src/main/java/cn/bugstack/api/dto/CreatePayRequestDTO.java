package cn.bugstack.api.dto;

import lombok.Data;

@Data
public class CreatePayRequestDTO {

    // 用户ID 【实际产生中会通过登录模块获取，不需要透彻】
    private String userId;
    // 产品编号
    private String productId;
    // 拼单组队ID
    private String teamId;
    // 活动ID
    private Long activityId;
    // 营销类型：0无营销，1拼团营销
    private Integer marketType = 0;
}
