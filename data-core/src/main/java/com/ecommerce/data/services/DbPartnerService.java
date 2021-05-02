package com.ecommerce.data.services;

import com.ecommerce.data.entities.Partner;
import com.ecommerce.data.entities.User;

import java.util.List;

public interface DbPartnerService {

    void createPartner(User partner);
    List<User> getAllPartners();
    User findById(Long partnerId);
}
