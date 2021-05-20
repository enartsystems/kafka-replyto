package kafka.reply.producer;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import kafka.reply.producer.service.ISenderMessage;
import lombok.extern.slf4j.Slf4j;
import poc.kafka.model.Message;


@SpringBootApplication
@Slf4j
public class Application implements CommandLineRunner{
	static int THREADS = 10;
	public static void main(String[] args) {
	    SpringApplication.run(Application.class, args);
	  }
	@Autowired
	ISenderMessage sender;
	ExecutorService executor = Executors.newFixedThreadPool(THREADS);
	@Override
	public void run(String... args) throws Exception {
		
		 for (int i = 0; i < 10; i++) {
	            Runnable worker = new Runnable() {
					@Override
					public void run() {
						Message<String> msg = new Message<String>();
						msg.setUuid(UUID.randomUUID().toString());
						msg.setTransaction(String.valueOf(System.currentTimeMillis()));
						msg.setData("envio mensaje");
                        Message out = sender.send(msg);
                        if(out.getTransaction().equals(msg.getTransaction())) {
                        	log.info(out.toString());	
                        }else {
                        	log.error("Transacciones incorrectas enviada{"+msg.getTransaction()+"} , recibida {"+out.getTransaction()+"}");
                        }
						
						
					}
				};
	            executor.execute(worker);
	        }
	        // This will make the executor accept no new threads
	        // and finish all existing threads in the queue
	        executor.shutdown();
	     
		
	}
}
