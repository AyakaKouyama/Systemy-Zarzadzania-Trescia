package com.web.services;

import com.ecommerce.data.dtos.PartnerRegisterDto;
import com.ecommerce.data.entities.Partner;
import com.ecommerce.data.entities.Product;
import com.ecommerce.data.entities.Role;
import com.ecommerce.data.entities.User;
import com.ecommerce.data.exceptions.AdminException;
import com.ecommerce.data.services.DbPartnerService;
import com.ecommerce.data.services.RoleService;
import com.ecommerce.data.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class PartnerService {

    private final DbPartnerService dbPartnerService;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final UserService userService;

    @Autowired
    public PartnerService(DbPartnerService dbPartnerService, PasswordEncoder passwordEncoder, RoleService roleService, UserService userService){
        this.dbPartnerService = dbPartnerService;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.userService = userService;
    }

    public void createPartner(PartnerRegisterDto partnerRegisterDto){
        User partner = new User();
        partner.setLogin(partnerRegisterDto.getEmail());
        partner.setPassword(passwordEncoder.encode(partnerRegisterDto.getPassword()));
        partner.setEnabled(true);
        partner.setCreateDate(new Date());
        partner.setLinkCode(UUID.randomUUID().toString());
        partner.setName(partnerRegisterDto.getName());
        partner.setSurname(partnerRegisterDto.getSurname());

        Role role = roleService.findPartnerRole();
        partner.setRole(role);

        dbPartnerService.createPartner(partner);
    }

    public List<User> getAllPartners(){
        List<User> users =  dbPartnerService.getAllPartners();
        for(User user : users){
            user.setOrdersSize(user.getOrders().size());
            user.setOrders(null);
        }

        return users;
    }

    public void activatePartner(Long partnerId){
        User partner  = dbPartnerService.findById(partnerId);
        if (partner != null) {
            partner.setEnabled(!partner.getEnabled());
            dbPartnerService.createPartner(partner);
        }else{
            throw new AdminException("Cannot activate/deactivate partner. Partner with id " + partnerId + " doesn't exist.");
        }
    }
}
