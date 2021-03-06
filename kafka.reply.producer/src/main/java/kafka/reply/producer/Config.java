package kafka.reply.producer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import kafka.reply.commons.CompletableFutureReplyingOperations;
import kafka.reply.commons.CompletableFutureReplyingTemplate;
import poc.kafka.model.Message;


@Configuration
@EnableKafka
public class Config {
	@Value("${spring.kafka.bootstrap-servers}")
	  private String bootstrapServers;

	  @Value("${spring.kafka.consumer.group-id}")
	  private String groupId;

	  @Value("${kafka.topic.message.request}")
	  private String requestTopic;

	  @Value("${kafka.topic.message.reply}")
	  private String replyTopic;

	  @Value("${kafka.request-reply.timeout-ms}")
	  private Long replyTimeout;
	  
	 @Bean
	  public Map<String, Object> consumerConfigs() {
	    Map<String, Object> props = new HashMap<>();
	    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
	    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
	    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
	    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

	    return props;
	  }

	  @Bean
	  public Map<String, Object> producerConfigs() {
	    Map<String, Object> props = new HashMap<>();
	    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
	    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
	    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
	    
	    return props;
	  }

	  @Bean
	  public CompletableFutureReplyingOperations<String, String, String> replyKafkaTemplate() {
	    CompletableFutureReplyingTemplate<String, String, String> requestReplyKafkaTemplate =
	        new CompletableFutureReplyingTemplate<>(requestProducerFactory(),
	            replyListenerContainer());
	    requestReplyKafkaTemplate.setDefaultTopic(requestTopic);
	    requestReplyKafkaTemplate.setDefaultReplyTimeout(Duration.ofMillis(replyTimeout));
	    return requestReplyKafkaTemplate;
	  }

	  @Bean
	  public ProducerFactory<String, String> requestProducerFactory() {
	    return new DefaultKafkaProducerFactory<>(producerConfigs());
	  }

	  @Bean
	  public ConsumerFactory<String, String> replyConsumerFactory() {
	    return new DefaultKafkaConsumerFactory<>(consumerConfigs());
	  }

	  @Bean
	  public KafkaMessageListenerContainer<String, String> replyListenerContainer() {
	    ContainerProperties containerProperties = new ContainerProperties(replyTopic);
	    return new KafkaMessageListenerContainer<>(replyConsumerFactory(), containerProperties);
	  }

	  @Bean
	  public KafkaAdmin admin() {
	    Map<String, Object> configs = new HashMap<>();
	    configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
	    return new KafkaAdmin(configs);
	  }

	  @Bean
	  public NewTopic replyTopic() {
	    Map<String, String> configs = new HashMap<>();
	    configs.put("retention.ms", replyTimeout.toString());
	    return new NewTopic(replyTopic, 8, (short) 1).configs(configs);
	  }
}
