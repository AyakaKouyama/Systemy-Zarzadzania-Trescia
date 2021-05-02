package com.ecommerce.data.services;

import com.ecommerce.data.entities.Cart;
import com.ecommerce.data.entities.Category;
import com.ecommerce.data.exceptions.AdminException;
import com.ecommerce.data.repositories.CartRepository;
import com.ecommerce.data.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }


    @Override
    public Cart getCartBySessionId(String sessionId) {
        return cartRepository.findBySessionId(sessionId);
    }

    @Override
    public void save(Cart cart) {
        cartRepository.save(cart);
    }
}
