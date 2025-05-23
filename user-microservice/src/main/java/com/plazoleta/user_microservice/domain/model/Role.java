package com.plazoleta.user_microservice.domain.model;


import java.util.Objects;

public class Role {
    private Long id;
    private RoleList name;
    private String description;

    public Role(){}

    public Role(String description, Long id, RoleList name) {
        if (name == null ) throw new IllegalArgumentException("Rol name is required");

        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoleList getName() {
        return name;
    }

    public void setName(RoleList name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}