package com.web.services;

import com.ecommerce.data.dtos.FileDto;
import com.ecommerce.data.entities.Image;
import com.ecommerce.data.entities.Product;
import com.ecommerce.data.exceptions.FileException;
import com.ecommerce.data.services.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import javax.xml.ws.ServiceMode;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FileService {

    private final String imagePath;
    private static final String SEP = File.separator;

    private final ImageService imageService;

    @Autowired
    public FileService(
            @Value("${files.imagePath}") String imagePath,
            ImageService imageService
    ){
        this.imagePath = imagePath;
        this.imageService = imageService;
    }

    public void saveFile(List<FileDto> files, Product product) throws FileException, IOException {
        for(FileDto file : files) {
            if(file.getData() != null && !file.getData().equals("")) {
                byte[] data = (Base64.getDecoder().decode(file.getData()));
                String[] extensions = file.getName().split("\\.");
                UUID filename = UUID.randomUUID();
                String strFilename = filename.toString() + "." + extensions[extensions.length - 1];
                String path = imagePath + SEP + strFilename;
                saveByteData(data, path);

                Image image = new Image();
                image.setAltName(file.getName());
                image.setPath(path);
                image.getProducts().add(product);
                image.setFileName(strFilename);
                image.setData(data);
                imageService.save(image);
            }
        }
    }

    public void saveByteData(byte[] data, String path) throws FileException {
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(data);
            log.info("File {} saved.", path);
        } catch (IOException e) {
            String message = "Error while saving file: " + path;
            log.error(message);
            throw new FileException(message);
        }
    }

    public List<String> decodeImages(List<Image> images){
        List<String> img = new ArrayList<>();

        for(Image image : images){
            String base64 = Base64.getEncoder().encodeToString(image.getData());
            img.add(base64);
        }

        return img;
    }
}
