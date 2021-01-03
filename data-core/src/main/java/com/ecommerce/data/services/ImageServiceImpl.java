package com.ecommerce.data.services;

import com.ecommerce.data.entities.Image;
import com.ecommerce.data.repositories.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ImageServiceImpl implements ImageService{

    private final ImageRepository imageRepository;

    @Autowired
    public ImageServiceImpl(ImageRepository imageRepository){
        this.imageRepository = imageRepository;
    }
    @Override
    public void save(Image image) {
        imageRepository.save(image);
    }
}
