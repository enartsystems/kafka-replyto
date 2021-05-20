package kafka.reply.consumer;

import java.io.IOException;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.slf4j.Slf4j;
import poc.kafka.model.Message;

/**
 * 
 */

@SpringBootApplication
@Slf4j
public class Application implements CommandLineRunner{
	ObjectMapper mapper = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
	public static void main(String[] args) {
	    SpringApplication.run(Application.class, args);
	  }

	@Override
	public void run(String... args) throws Exception {
		
		
	}
	@KafkaListener(topics = "${kafka.topic.message.request}", containerFactory = "requestReplyListenerContainerFactory")
	  @SendTo()
	  public String receive(String smsg) {
	    log.info("received request for Message {} ", smsg.toString());
	    Message msg;
		try {
			msg = mapper.readValue(smsg.getBytes(), Message.class);
			Message<String> msg1 = new Message();
		    msg1.setTransaction(msg.getTransaction());
		    msg1.setUuid(UUID.randomUUID().toString());
		    msg1.setData("retorno " + msg.getTransaction());
		    log.info("sending reply {} ", msg1);
		    return  mapper.writeValueAsString(msg1);
			
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	    return null;
	  }
}
