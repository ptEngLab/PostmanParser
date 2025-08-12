package com.postman.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostmanCollection {
    private PostmanInfo info;
    private List<PostmanItem> item;
    private List<PostmanKeyValue> variable;
    private List<PostmanEvent> event;


    public List<PostmanItem> getItem() {
        return item != null ? item : Collections.emptyList();
    }

    public boolean hasVariables() {
        return variable != null && !variable.isEmpty();
    }

    public List<PostmanKeyValue> getVariable() {
        return hasVariables() ? variable : Collections.emptyList();
    }

}
