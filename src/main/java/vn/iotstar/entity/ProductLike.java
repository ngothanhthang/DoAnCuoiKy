package vn.iotstar.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "productLikes")
@CompoundIndex(name = "user_product_idx", def = "{'user.$id': 1, 'product.$id': 1}", unique = true)
public class ProductLike implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @DBRef
    private User user;  // Người dùng đã like sản phẩm

    @DBRef
    private Product product;  // Sản phẩm đã được like

    private boolean liked = true;  // Trạng thái like (true = liked, false = unliked)
}
