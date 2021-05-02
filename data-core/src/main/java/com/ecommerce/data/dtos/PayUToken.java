package com.ecommerce.data.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayUToken {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_expire")
    private String tokenExpire;

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("grant_type")
    private String grant_type;

    @JsonProperty("client_id")
    private String client_id;

    @JsonProperty("client_secret")
    private String client_secret;

}
