package com.ecommerce.data.services;

import com.ecommerce.data.entities.Image;
import com.ecommerce.data.exceptions.AdminException;
import com.ecommerce.data.exceptions.ApiException;
import com.ecommerce.data.repositories.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


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

    @Override
    public void delete(String id) throws ApiException {
        Optional<Image> image = imageRepository.findById(Long.parseLong(id));
        if(image.isPresent()) {
            try {
                imageRepository.delete(image.get());
            }catch (Exception e){
                throw  new ApiException("Cannot delete image. An error occurred during the process.");
            }
        }else{
            throw new AdminException("Cannot delete image. Image with given id" + id + "not found.");
        }
    }
}
