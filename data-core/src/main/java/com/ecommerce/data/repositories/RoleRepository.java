package com.ecommerce.data.repositories;

import com.ecommerce.data.entities.Product;
import com.ecommerce.data.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("SELECT r FROM Role r WHERE r.role = :name")
    Role findRoleByRoleName(@Param("name")String name);
}
