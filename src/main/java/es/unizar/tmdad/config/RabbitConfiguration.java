package es.unizar.tmdad.config;

import es.unizar.tmdad.listener.MessageListener;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {

    @Value("${chat.exchanges.output}")
    private String outputExchangeName;

    @Value("${chat.exchanges.input-0}")
    private String inputIncomingExchangeName;

    @Value("${chat.exchanges.input-1}")
    private String inputReprocessExchangeName;

    @Value("${chat.queues.input-0:incoming-messages}")
    private String queueNameIncoming;

    @Value("${chat.queues.input-1:reprocess-messages}")
    private String queueNameReprocess;

    @Value("${application.name}")
    private String appName;

    @Bean("incoming-queue")
    Queue queueIncoming() {
        return new Queue(getQueueName(appName, inputIncomingExchangeName), true, false, false);
    }

    @Bean("reprocess-queue")
    Queue queueReprocess() {
        return new Queue(getQueueName(appName, inputReprocessExchangeName), true, false, false);
    }

    @Bean
    FanoutExchange exchangeOutput() {
        return new FanoutExchange(outputExchangeName);
    }

    @Bean("incoming-exchange")
    FanoutExchange exchangeInputIncoming() {
        return new FanoutExchange(inputIncomingExchangeName);
    }

    @Bean("reprocess-exchange")
    DirectExchange exchangeInputReprocess() {
        return new DirectExchange(inputReprocessExchangeName);
    }

    @Bean
    Binding bindingIncoming(@Qualifier("incoming-queue") Queue queue, @Qualifier("incoming-exchange") FanoutExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange);
    }

    @Bean
    Binding bindingReprocess(@Qualifier("reprocess-queue") Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("");
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(
                getQueueName(appName, inputIncomingExchangeName),
                getQueueName(appName, inputReprocessExchangeName));
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(MessageListener receiver) {
        return new MessageListenerAdapter(receiver, "apply");
    }

    private String getQueueName(String appName, String exchangeName) {
        return appName + "." + exchangeName;
    }
}
