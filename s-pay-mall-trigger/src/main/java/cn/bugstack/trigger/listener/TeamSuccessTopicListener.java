package cn.bugstack.trigger.listener;


import cn.bugstack.api.dto.NotifyRequestDTO;
import cn.bugstack.domain.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSON;

@Slf4j
@Component
public class TeamSuccessTopicListener {
    private final OrderService orderService;

    public TeamSuccessTopicListener(OrderService orderService) {
        this.orderService = orderService;
    }

    //指定消费队列
    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(value = "${spring.rabbitmq.config.consumer.topic_team_success.queue}"),
            exchange = @Exchange(value = "${spring.rabbitmq.config.consumer.topic_team_success.exchange}", type = ExchangeTypes.TOPIC),
            key = "${spring.rabbitmq.config.consumer.topic_team_success.routing-key}"
        )
    )
    public void handleMessage(String message) {
        try {
            NotifyRequestDTO request = JSON.parseObject(message, NotifyRequestDTO.class);
            log.info("拼团成功，开始结算{}", JSON.toJSONString(request));
            //
            orderService.changeOrderMarketSettlement(request.getOutTradeNoList());
        } catch (Exception e) {
            log.error("接受消息 {}", message,e);
            throw e;
        }
    }
}
