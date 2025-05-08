package vn.iotstar.dto;

import java.math.BigDecimal;

public class ProductDTO_2 {
	private String id;  // Thay đổi từ Long sang String
	private String name;
	private BigDecimal price;
	private int quantity;
	private String imageUrl;
	//danh mục
	private String categoryId;  // Thay đổi từ Long sang String và sửa tên biến để tuân theo quy ước camelCase
	private String categoryName;  // Sửa tên biến để tuân theo quy ước camelCase
	private int status;
	private String description;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategoryId() {  // Thay đổi kiểu trả về và sửa tên phương thức
		return categoryId;
	}
	public void setCategoryId(String categoryId) {  // Thay đổi kiểu tham số và sửa tên biến
		this.categoryId = categoryId;
	}
	public String getCategoryName() {  // Sửa tên phương thức
		return categoryName;
	}
	public void setCategoryName(String categoryName) {  // Sửa tên biến
		this.categoryName = categoryName;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {  // Thay đổi kiểu trả về
		return id;
	}
	public void setId(String id) {  // Thay đổi kiểu tham số
		this.id = id;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	// Thêm các phương thức hỗ trợ để tương thích ngược với code cũ
	
	// Phương thức này giúp chuyển đổi từ String ID sang Long nếu cần
	public Long getIdAsLong() {
		try {
			return id != null ? Long.parseLong(id) : null;
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	// Phương thức này giúp chuyển đổi từ String categoryId sang Long nếu cần
	public Long getCategoryIdAsLong() {
		try {
			return categoryId != null ? Long.parseLong(categoryId) : null;
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	// Phương thức này cho phép set ID bằng Long (tương thích ngược)
	public void setId(Long id) {
		this.id = id != null ? id.toString() : null;
	}
	
	// Phương thức này cho phép set categoryId bằng Long (tương thích ngược)
	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId != null ? categoryId.toString() : null;
	}
}
