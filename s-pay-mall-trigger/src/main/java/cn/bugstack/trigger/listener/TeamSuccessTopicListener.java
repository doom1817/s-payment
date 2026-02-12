package cn.bugstack.trigger.listener;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TeamSuccessTopicListener {
    //指定消费队列
    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(value = "${spring.rabbitmq.config.consumer.topic_team_success.queue}"),
            exchange = @Exchange(value = "${spring.rabbitmq.config.consumer.topic_team_success.exchange}", type = ExchangeTypes.TOPIC),
            key = "${spring.rabbitmq.config.consumer.topic_team_success.routing-key}"
        )
    )
    public void handleMessage(String message) {
        log.info("接受消息 {}", message);
    }
}
