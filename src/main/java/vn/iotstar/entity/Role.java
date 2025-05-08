package vn.iotstar.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "roles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    private String id;  // Thay đổi từ Long sang String cho MongoDB

    @Field(name = "name")
    private String name;

    public static String ADMIN = "ADMIN";
    public static String USER = "USER";
}
