package kafka.reply.producer.service;

import java.util.concurrent.CompletableFuture;

import poc.kafka.model.Message;

public interface ISenderMessage {

	Message send(Message msg);
	
}
