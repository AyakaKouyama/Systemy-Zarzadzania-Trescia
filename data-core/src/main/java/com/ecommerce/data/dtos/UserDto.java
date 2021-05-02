package com.ecommerce.data.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;

    private String login;

    private String password;

    private String role;

    private String link;

    private BigDecimal points;

    private int soldItems;

    private String name;

    private String surname;

    private boolean enabled;

    public UserDto(String login){
        this.login = login;
    }


}
