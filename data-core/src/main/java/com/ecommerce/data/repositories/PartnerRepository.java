package com.ecommerce.data.repositories;

import com.ecommerce.data.entities.Partner;
import com.ecommerce.data.entities.Product;
import com.ecommerce.data.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PartnerRepository extends JpaRepository<User, Long> {

    @Query("SELECT p FROM User p WHERE p.role.role = 'PARTNER'")
    List<User> getAllPartners();

}
