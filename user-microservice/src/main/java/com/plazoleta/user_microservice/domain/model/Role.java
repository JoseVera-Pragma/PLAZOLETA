package com.plazoleta.user_microservice.domain.model;


import java.util.Objects;

public class Role {
    private Long id;
    private RoleList name;
    private String description;

    public Role(){}

    public Role(Long id, RoleList name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public boolean isAdmin() {
        return RoleList.ROLE_ADMIN.equals(name);
    }
    public boolean isOwner() {
        return RoleList.ROLE_OWNER.equals(name);
    }
    public boolean isEmployed() {
        return RoleList.ROLE_EMPLOYED.equals(name);
    }
    public boolean isCustomer() {
        return RoleList.ROLE_CUSTOMER.equals(name);
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