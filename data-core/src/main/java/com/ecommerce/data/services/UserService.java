package com.ecommerce.data.services;

import com.ecommerce.data.entities.Partner;
import com.ecommerce.data.entities.User;

public interface UserService {

    User findUserByLogin(String login);
    User findPartnerByRefLink(String ref);
}
