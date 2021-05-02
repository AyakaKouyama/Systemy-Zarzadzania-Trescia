package com.ecommerce.data.dtos;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrderDetails {

    private String notifyUrl;
    private String continueUrl;
    private String customerIp;
    private String merchantPosId;
    private String description;
    private String currencyCode;
    private String totalAmount;
    private Buyer buyer;
    private List<PayUProduct> products = new ArrayList<>();

    private String address;
    private String city;
    private String postalCode;
    private String sessionId;

}
