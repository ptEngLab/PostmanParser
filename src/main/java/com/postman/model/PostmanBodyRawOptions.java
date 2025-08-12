package com.postman.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.Strings;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostmanBodyRawOptions {
    private String language;


    public boolean isLanguage(String expected) {
        return Strings.CI.equals(language, expected) ;
    }


    public boolean isJsonLanguage() { return isLanguage("json"); }
    public boolean isXmlLanguage() { return isLanguage("xml"); }
    public boolean isTextLanguage() { return isLanguage("text"); }
    public boolean isJavaScriptLanguage() { return isLanguage("javascript"); }
    public boolean isHtmlLanguage() { return isLanguage("html"); }
}
