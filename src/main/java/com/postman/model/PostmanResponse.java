package com.postman.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostmanResponse {
    private String name;
    private PostmanRequest originalRequest;
    private String status;
    private int code;
    private List<PostmanKeyValue> header;
    private String body;
}
