package com.ecommerce.data.repositories;

import com.ecommerce.data.entities.Cart;
import com.ecommerce.data.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findAll();

    Cart findBySessionId(@Param("sessionId") String sessionId);

}
