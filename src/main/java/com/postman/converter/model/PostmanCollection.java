package com.postman.converter.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostmanCollection {
    private PostmanInfo info;
    private List<PostmanItem> item;
    private List<PostmanKeyValue> variable;
    private List<PostmanEvent> event;

    public boolean hasItem() {
        return item != null && !item.isEmpty();
    }
    public boolean hasVariables() {
        return variable != null && !variable.isEmpty();
    }
}
