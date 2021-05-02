package com.ecommerce.data.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderResponse {

    private Status status;
    private String redirectUri;
    private String orderId;
}
