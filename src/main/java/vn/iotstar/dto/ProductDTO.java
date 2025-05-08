package vn.iotstar.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductDTO {

    private String id;
    private String name;
    private int status;
    private String description;
    private BigDecimal price;
    private int quantity;
    private String imageUrl;
    private String categoryId;
    private String categoryName;
	/*
	 * private Long vendorId; private String vendorName;
	 */
    private LocalDateTime createdAt;
    private double averageRating;
    private int totalSold;

}
