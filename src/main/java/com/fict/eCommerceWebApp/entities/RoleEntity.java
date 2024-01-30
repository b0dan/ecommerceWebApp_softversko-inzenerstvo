package com.fict.eCommerceWebApp.entities;

import javax.persistence.*;
import java.util.Collection;

@Entity
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
    private Collection<UserEntity> users;

    public RoleEntity() {
        name = "";
    }

    public RoleEntity(String name, Collection<UserEntity> users) {
        this.name = name;
        this.users = users;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Collection<UserEntity> getUsers() {
        return users;
    }
    public void setUsers(Collection<UserEntity> users) {
        this.users = users;
    }
}