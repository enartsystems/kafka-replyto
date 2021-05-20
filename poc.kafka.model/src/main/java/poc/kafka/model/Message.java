/**
 * 
 */
package poc.kafka.model;

import lombok.Data;

/**
 * @author manuel
 *
 */
@Data
public class Message<T> {

	String uuid;
	String transaction;
	T data;
}
