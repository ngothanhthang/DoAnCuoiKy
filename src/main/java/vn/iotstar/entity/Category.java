package vn.iotstar.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.DBRef;

import lombok.*;
import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "categories")
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    private String id;
    
    @Field(name = "category_name")
    private String name;
    
    @Field(name = "images")
    private String images;
    
    @Field(name = "status")
    private boolean status;
    
    @Field(name = "description")
    private String description;
    
    @DBRef
    private List<Product> products;
}