package com.ecommerce.data.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Error {

    private String timestamp;

    private String status;

    private String error;

    private String message;

    private String path;
}
