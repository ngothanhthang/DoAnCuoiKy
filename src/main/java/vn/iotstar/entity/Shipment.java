package vn.iotstar.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "shipments")
public class Shipment implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    private String id;

    @DBRef
    @Field(name = "order")
    private Order order;

    @DBRef
    @Field(name = "shipper")
    private User shipper;

    @Field(name = "status")
    private String status;

    @Field(name = "shipped_at")
    private LocalDateTime shippedAt;
}
