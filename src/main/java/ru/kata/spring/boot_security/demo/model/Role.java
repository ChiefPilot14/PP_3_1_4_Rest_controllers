package ru.kata.spring.boot_security.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "roles")
@Table(name = "user_roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;

    public Role(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
