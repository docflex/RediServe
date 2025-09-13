package commonlibs.kafka.messages;

import commonlibs.dto.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * Kafka message used to respond to a ProductFetchRequest.
 * <p>
 * Contains the correlationId from the request to allow the requester
 * to match the response with the original request, and the fetched ProductDTO.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFetchResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Correlation ID to match the response with the original request */
    private String correlationId;

    /** The fetched product data */
    private ProductDTO product;
}
