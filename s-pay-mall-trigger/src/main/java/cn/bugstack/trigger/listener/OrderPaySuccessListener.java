package cn.bugstack.trigger.listener;

import cn.bugstack.domain.goods.service.IGoodsService;
import cn.bugstack.domain.order.event.PaySuccessMessageEvent;
import com.alibaba.fastjson2.JSON;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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


//    @Subscribe 旧版发布订阅方式

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "${spring.rabbitmq.config.consumer.topic_order_pay_success.queue}"),
                    exchange = @Exchange(value = "${spring.rabbitmq.config.consumer.topic_order_pay_success.exchange}", type = ExchangeTypes.TOPIC),
                    key = "${spring.rabbitmq.config.consumer.topic_order_pay_success.routing-key}"
            )
    )
    public void listener(String paySuccessMessageJSON){
        try{
            log.info("收到支付成功消息，可以做接下来的事情了【发货、充值、开会员】paySuccessMessage：{}", paySuccessMessageJSON);
            PaySuccessMessageEvent.PaySuccessMessage paySuccessMessage =
                    JSON.parseObject(paySuccessMessageJSON, PaySuccessMessageEvent.PaySuccessMessage.class);
            log.info("模拟发货，单号{}", paySuccessMessage.getTradeNo());
            //变更订单状态 - 发货完成&结算
            goodsService.changeOrderDealDone(paySuccessMessage.getTradeNo());
        } catch (Exception e) {
            log.error("接受消息 {}", paySuccessMessageJSON,e);
            throw e;
        }
    }


}
