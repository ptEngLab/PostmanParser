package com.postman.converter.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostmanKeyValue {
    private String key;
    private String value;
    private String type;        // string, file, etc.
    private boolean disabled;   // true if disabled in Postman JSON
    private String description; // optional
    private String src;         // file path if type=file

    // Derived helper method - not serialized
    public boolean isEnabled() {
        return !disabled;
    }
}
