package com.fict.eCommerceWebApp.services;

import com.fict.eCommerceWebApp.entities.RoleEntity;
import com.fict.eCommerceWebApp.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    private RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public RoleEntity findRoleByName(String name) {
        return roleRepository.findByName(name);
    }
}