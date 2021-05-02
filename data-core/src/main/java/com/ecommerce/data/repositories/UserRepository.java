package com.ecommerce.data.repositories;

import com.ecommerce.data.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLogin(String login);

    @Query("SELECT p FROM User p WHERE p.linkCode = :refLink")
    User findPartnerByRefLink(@Param("refLink")String refLink);
}
