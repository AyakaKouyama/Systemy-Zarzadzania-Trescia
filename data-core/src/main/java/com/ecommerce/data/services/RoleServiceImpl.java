package com.ecommerce.data.services;

import com.ecommerce.data.entities.Role;
import com.ecommerce.data.repositories.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository){
        this.roleRepository = roleRepository;
    }

    @Override
    public Role findPartnerRole() {
        return roleRepository.findRoleByRoleName("PARTNER");
    }
}
