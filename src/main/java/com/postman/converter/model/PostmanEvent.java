package com.postman.converter.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostmanEvent {
    private String listen; // pre-request, test
    private PostmanScript script;
}
