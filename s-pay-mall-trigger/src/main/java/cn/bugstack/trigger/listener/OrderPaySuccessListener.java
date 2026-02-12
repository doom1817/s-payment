package cn.bugstack.trigger.listener;

import cn.bugstack.domain.goods.service.IGoodsService;
import cn.bugstack.domain.order.event.PaySuccessMessageEvent;
import com.alibaba.fastjson2.JSON;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 支付成功回调消息
 */
@Slf4j
@Component
public class OrderPaySuccessListener {



    @Resource
    private IGoodsService goodsService;


    @Subscribe
    public void handleEvent(String paySuccessMessageJSON) {
        log.info("收到支付成功消息，可以做接下来的事情了【发货、充值、开会员】paySuccessMessage：{}", paySuccessMessageJSON);
        PaySuccessMessageEvent.PaySuccessMessage paySuccessMessage =
                JSON.parseObject(paySuccessMessageJSON, PaySuccessMessageEvent.PaySuccessMessage.class);
        log.info("模拟发货，单号{}", paySuccessMessage.getTradeNo());
        goodsService.changeOrderDealDone(paySuccessMessage.getTradeNo());
    }

}
