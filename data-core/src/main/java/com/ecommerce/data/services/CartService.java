package com.ecommerce.data.services;

import com.ecommerce.data.entities.Cart;
import com.ecommerce.data.entities.Category;
import com.ecommerce.data.exceptions.AdminException;

import java.util.List;

public interface CartService {

    Cart getCartBySessionId(String sessionId);
    void save(Cart cart);

}
