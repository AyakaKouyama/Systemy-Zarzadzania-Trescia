package com.ecommerce.data.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderFormDto {

    private String name;
    private String surrname;
    private String email;
    private String phone;
    private String street;
    private String postalCode;
    private String city;
}
