package com.orthopedic.api.auth.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {
    public Role() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private java.util.UUID id;

    @Column(unique = true, nullable = false)
    private String name;

    public java.util.UUID getId() {
        return id;
    }

    public void setId(java.util.UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
