package commonlibs.kafka.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Kafka message used to request a Product from the DB-Fetcher service.
 * <p>
 * The message contains a unique correlationId to match responses
 * and the productId to identify which product to fetch.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFetchRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Unique identifier to correlate request and response */
    private String correlationId;

    /** ID of the product being requested */
    private Long productId;
}
