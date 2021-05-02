package com.ecommerce.data.services;

import com.ecommerce.data.entities.Partner;
import com.ecommerce.data.entities.User;
import com.ecommerce.data.repositories.PartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DbPartnerServiceImpl implements DbPartnerService {

    private final PartnerRepository partnerRepository;

    @Autowired
    public DbPartnerServiceImpl(PartnerRepository partnerRepository){
        this.partnerRepository = partnerRepository;
    }

    @Override
    public void createPartner(User partner) {
        partnerRepository.save(partner);
    }

    @Override
    public List<User> getAllPartners() {
        return partnerRepository.getAllPartners();
    }

    @Override
    public User findById(Long partnerId) {
        return partnerRepository.findById(partnerId).orElse(null);
    }
}
