package com.ecommerce.data.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
public class FileDto {

    public FileDto(String data, String name){
        this.data = data;
        this.name = name;
    }

    private String data;
    private String name;
}
