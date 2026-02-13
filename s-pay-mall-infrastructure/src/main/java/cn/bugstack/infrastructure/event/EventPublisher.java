package cn.bugstack.infrastructure.event;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: doom
 * @Date: 2026/02/13/17:10
 * @Description:
 */
@Slf4j
@Component
public class EventPublisher {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.config.producer.topic_order_pay_success.exchange}")
    private String exchangeName;

    public void publish(String routingKey, Object message) {
        try {
            rabbitTemplate.convertAndSend(exchangeName,routingKey,message,m->{
                m.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                return m;
            });
        } catch (Exception e) {
            log.error("发送MQ消息失败 routingKey：{} message:{}", routingKey, message, e);
            throw e;
        }
    }
}
