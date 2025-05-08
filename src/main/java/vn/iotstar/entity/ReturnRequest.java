package vn.iotstar.entity;

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
@Document(collection = "return_requests")
public class ReturnRequest {
    
    @Id
    private String id;

    @DBRef
    @Field(name = "order")
    private Order order;

    @Field(name = "reason")
    private String reason;

    @Field(name = "image_url")
    private String imageUrl; // Lưu trữ URL của ảnh

    @Field(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Field(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
