package com.example.auth.model;

import com.example.auth.common.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("users")
public class User {
    @Id
    @Column("id")
    private Integer id;

    @Column("email")
    private String email;

    @Column("password")
    private String password;

    @Column("role")
    private UserRole role;
}
