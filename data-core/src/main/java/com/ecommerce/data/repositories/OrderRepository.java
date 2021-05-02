package com.ecommerce.data.repositories;

import com.ecommerce.data.entities.Image;
import com.ecommerce.data.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
