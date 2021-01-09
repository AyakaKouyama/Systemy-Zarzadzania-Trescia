package com.ecommerce.data.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;

    private String login;

    private String password;

    public UserDto(String login){
        this.login = login;
    }
}
