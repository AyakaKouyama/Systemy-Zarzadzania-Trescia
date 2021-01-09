package com.web.conf;

import com.ecommerce.data.exceptions.ApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

@Slf4j
@Component
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

    private ResponseErrorHandler errorHandler;
    private ObjectMapper objectMapper;

    public RestTemplateResponseErrorHandler() {
        this.objectMapper = new ObjectMapper();
        this.errorHandler = new DefaultResponseErrorHandler();
    }

    @Override
    public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
        return errorHandler.hasError(clientHttpResponse);
    }

    @Override
    public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
        String response = IOUtils.toString(clientHttpResponse.getBody());
        String message = getMessage(response);
        log.error(message);

        if(!clientHttpResponse.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
            message = "Unknown error. Please try again later.";
        }

        throw new ApiException(message);
    }

    private String getMessage(String exceptionBody) {
        try {
            com.ecommerce.data.dtos.Error error = objectMapper.readValue(exceptionBody, com.ecommerce.data.dtos.Error.class);
            return error.getMessage();
        } catch (IOException e) {
            log.warn("Cannot deserialize response body.");
            return null;
        }
    }
}
