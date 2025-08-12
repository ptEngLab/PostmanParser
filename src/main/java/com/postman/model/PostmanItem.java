package com.postman.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostmanItem {
    private String name;
    private List<PostmanItem> item; // sub-items (folders)
    private PostmanRequest request;
    private List<PostmanResponse> response;
    private List<PostmanEvent> event;
    private List<PostmanKeyValue> variable; // variables specific to this item

    public boolean isFolder() {
        return item != null && !item.isEmpty();
    }

    public boolean hasRequest() {
        return request != null;
    }

    public boolean hasEvents() {
        return event != null && !event.isEmpty();
    }

    public boolean hasVariables() {
        return variable != null && !variable.isEmpty();
    }

}
