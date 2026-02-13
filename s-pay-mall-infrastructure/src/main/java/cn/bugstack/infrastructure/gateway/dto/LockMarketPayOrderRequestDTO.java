package cn.bugstack.infrastructure.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: doom
 * @Date: 2026/02/04/21:26
 * @Description:
 * 锁定聚合订单请求参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LockMarketPayOrderRequestDTO {
    //用户ID
    private String userId;
    //拼单组队ID -可为空，为空则创建新的组队ID
    private String teamId;
    //活动ID
    private Long activityId;
    //商品ID
    private String goodsId;
    //渠道
    private String channel;
    //来源
    private String source;
    //外部交易单号
    private String outTradeNo;
    //回调接口
    private NotifyConfigVO notifyConfigVO;

    public void setNotifyUrl(String notifyUrl){
        NotifyConfigVO notifyConfigVO = new NotifyConfigVO();
        notifyConfigVO.setNotifyType("HTTP");
        notifyConfigVO.setNotifyUrl(notifyUrl);
        this.notifyConfigVO = notifyConfigVO;
    }
    public void setNotifyMQ(){
        NotifyConfigVO notifyConfigVO = new NotifyConfigVO();
        notifyConfigVO.setNotifyType("MQ");
        this.notifyConfigVO = notifyConfigVO;
    }

    @Data
    public static class NotifyConfigVO{
        private String notifyType;
        private String notifyMQ;
        private String notifyUrl;
    }
}
