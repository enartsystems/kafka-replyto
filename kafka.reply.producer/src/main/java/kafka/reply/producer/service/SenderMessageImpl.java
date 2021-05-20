/**
 * 
 */
package kafka.reply.producer.service;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import kafka.reply.commons.CompletableFutureReplyingOperations;
import lombok.extern.slf4j.Slf4j;
import poc.kafka.model.Message;

/**
 * @author manuel
 *
 */
@Component
@Slf4j
public class SenderMessageImpl implements ISenderMessage {
	ObjectMapper mapper = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
	@Autowired
	private CompletableFutureReplyingOperations<String, String, String> requestReplyKafkaTemplate;
	@Value("${kafka.topic.message.request}")
	private String requestTopic;

	@Override
	public Message send(Message msg) {
		try {
			log.info("send {}",msg.toString());
			String retorno = Async(mapper.writeValueAsString(msg)).get();
			log.info("retorno {}",retorno);
			return mapper.readValue(retorno.getBytes(),Message.class);
		} catch (InterruptedException | ExecutionException | IOException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Failed to get Message", e);
		} 
	}

	
	private CompletableFuture<String> Async(String msg) {
		return requestReplyKafkaTemplate.requestReply(requestTopic, msg);
	}
}
