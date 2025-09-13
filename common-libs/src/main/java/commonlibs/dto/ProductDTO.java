package commonlibs.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Data Transfer Object representing a Product.
 * <p>
 * This class is used to transfer product data between services
 * and can be serialized for caching or messaging purposes.
 */
@Getter
@Setter
public class ProductDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Unique identifier of the product */
    private Long id;

    /** Name of the product */
    private String name;

    /** Description of the product */
    private String description;

    /** Price of the product */
    private BigDecimal price;
}
