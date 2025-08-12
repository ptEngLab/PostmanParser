package com.postman.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostmanBodyOptions {
    private PostmanBodyRawOptions raw;

    private boolean hasRaw() {
        return raw != null;
    }

    public boolean hasJsonLanguage() {
        return hasRaw() && raw.isJsonLanguage();
    }
    public boolean hasXmlLanguage() {
        return hasRaw() && raw.isXmlLanguage();
    }
    public boolean hasTextLanguage() {
        return hasRaw() && raw.isTextLanguage();
    }
    public boolean hasJavaScriptLanguage() {
        return hasRaw() && raw.isJavaScriptLanguage();
    }
    public boolean hasHtmlLanguage() {
        return hasRaw() && raw.isHtmlLanguage();
    }
}
