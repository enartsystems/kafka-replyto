/**
 * 
 */
package poc.kafka.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author manuel
 *
 */
@Data
@Builder
public class Item<T> {
	/**
	 * key of item
	 */
   private String key;
   /**
    * value of item
    */
   private T value;
}
