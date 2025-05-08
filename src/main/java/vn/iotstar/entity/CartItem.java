package vn.iotstar.entity;

import org.springframework.data.mongodb.core.mapping.DBRef;

public class CartItem {
    private String id;
    
    @DBRef
    private Product product;
    
    private int quantity;
    
    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
