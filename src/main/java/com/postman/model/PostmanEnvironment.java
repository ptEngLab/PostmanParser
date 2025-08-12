package com.postman.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostmanEnvironment {
    private String id;
    private String name;
    private List<PostmanKeyValue> values;

    public boolean hasValues() {
        return values != null && !values.isEmpty();
    }

    public List<PostmanKeyValue> getValues() {
        return hasValues() ? values : Collections.emptyList();
    }
}

