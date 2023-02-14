package com.example.seatchoice.config.kafka;

import static org.apache.kafka.clients.consumer.ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

import com.example.seatchoice.dto.param.ChattingMessageParam;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
@EnableKafka
public class KafkaConfig {

    // kafka 프로듀서 설정
    @Bean
    public ProducerFactory<String, ChattingMessageParam> producerFactory() {
        return new DefaultKafkaProducerFactory<>(
            producerConfigs(), null, new JsonSerializer<>());
    }

    @Bean
    public KafkaTemplate<String, ChattingMessageParam> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> configurations = new HashMap<>();
        configurations.put(BOOTSTRAP_SERVERS_CONFIG, "43.200.67.139:9092");
        configurations.put(KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        configurations.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configurations.put(GROUP_ID_CONFIG, "seatChoice");
        return configurations;
    }


    // kafka 컨슈머 설정
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ChattingMessageParam> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ChattingMessageParam> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, ChattingMessageParam> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
            consumerConfigs(), null, new JsonDeserializer<>(ChattingMessageParam.class));
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> configurations = new HashMap<>();
        configurations.put(BOOTSTRAP_SERVERS_CONFIG, "43.200.67.139:9092");
        configurations.put(KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        configurations.put(VALUE_DESERIALIZER_CLASS_CONFIG,  JsonDeserializer.class);
        configurations.put(ENABLE_AUTO_COMMIT_CONFIG, false);
        configurations.put(ALLOW_AUTO_CREATE_TOPICS_CONFIG, false);
        configurations.put(GROUP_ID_CONFIG, "seatChoice");
        return configurations;
    }
}
