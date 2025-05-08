package vn.iotstar.dto;

public class CategoryDTO_2 {
    private String id;  // Thay đổi từ Long sang String
    private String name;
    
    public String getId() {  // Thay đổi kiểu trả về
        return id;
    }
    
    public void setId(String id) {  // Thay đổi kiểu tham số
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}
