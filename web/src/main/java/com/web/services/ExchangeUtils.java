package com.web.services;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExchangeUtils {

    public static HttpEntity getEntity(String token){
        HttpHeaders headers = new HttpHeaders();
        if(token != null && !token.equals(""))
            headers.add("Authorization", "Bearer " + token);
        return new HttpEntity<>(headers);
    }

    public static HttpEntity getEntity(Object object, MediaType mediaType, String token){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        if(token != null && !token.equals(""))
            headers.add("Authorization", "Bearer " + token);
        return new HttpEntity<>(object, headers);
    }

    public static URI getUri(String serviceUrl, String subOperation, Map<String, String> queryParams){
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serviceUrl);

        if(queryParams != null) {
            for (String param : queryParams.keySet()) {
                builder.queryParam(param, queryParams.get(param));
            }
        }

        return builder.path("/" + subOperation).build().toUri();
    }

    public static <T, K> String multipartPostData(String serviceUrl, String operation, Class<T> clazz, RestTemplate restTemplate, Map<String, String> params, MultiValueMap<String, Object> data, String token) {
        HttpEntity entity;
        if(data != null){
            entity = getEntity(data, MediaType.MULTIPART_FORM_DATA, token);
        }else{
            entity = getEntity(token);
        }
        URI uri = ExchangeUtils.getUri(serviceUrl, operation, params);
        ResponseEntity<String> res = restTemplate.postForEntity(uri, entity, String.class);
        return res.getBody();
    }

    public static <T, K> T postData(String serviceUrl, String operation, Class<T> clazz, RestTemplate restTemplate, Map<String, String> params, K data, String token) {
        HttpEntity entity;
        if(data != null){
            entity = getEntity(data, MediaType.APPLICATION_JSON, token);
        }else{
            entity = getEntity(token);
        }
        URI uri = ExchangeUtils.getUri(serviceUrl, operation, params);
        T res = restTemplate.postForObject(uri, data, clazz);
        return res;
    }

    public static <T, K> List postListData(String serviceUrl, String operation, RestTemplate restTemplate, Map<String, String> params, K data, String token) {
        HttpEntity entity;
        if(data != null){
            entity = getEntity(data, MediaType.APPLICATION_JSON, token);
        }else{
            entity = getEntity(token);
        }
        URI uri = ExchangeUtils.getUri(serviceUrl, operation, params);
        List res = restTemplate.postForObject(uri, data, List.class);
        return res;
    }

    public static <T> T exchangeData(String serviceUrl, String operation, HttpMethod method, ParameterizedTypeReference<T> clazz, RestTemplate restTemplate, Map<String, String> params, String token) {
        HttpEntity entity = ExchangeUtils.getEntity(token);
        URI uri = ExchangeUtils.getUri(serviceUrl, operation, params);
        ResponseEntity<T> res = restTemplate.exchange(uri, method, entity, clazz);
        return res.getBody();
    }

    public static <T>  ResponseEntity<T>  exchangeDataFullResponse(String serviceUrl, String operation, HttpMethod method, ParameterizedTypeReference<T> clazz, RestTemplate restTemplate, Map<String, String> params, String token) {
        HttpEntity entity = ExchangeUtils.getEntity(token);
        URI uri = ExchangeUtils.getUri(serviceUrl, operation, params);
        ResponseEntity<T> res = restTemplate.exchange(uri, method, entity, clazz);
        return res;
    }
}
