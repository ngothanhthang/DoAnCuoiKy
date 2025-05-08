package vn.iotstar.entity;

import org.springframework.data.mongodb.core.mapping.DBRef;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
// Không còn là @Document vì được nhúng trong Order
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    @DBRef
    private Product product;

    private int quantity;

    private BigDecimal price;
    
    private boolean reviewed;
}
