package com.neotech.adminuser.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String email;
    private String password;
    private String role;
    private String fullname;

    public User(String email, String password, String role, String fullname) {

        this.email = email;
        this.password = password;
        this.role = role;
        this.fullname = fullname;
    }
}
