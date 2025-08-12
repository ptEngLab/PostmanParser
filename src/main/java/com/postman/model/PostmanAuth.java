package com.postman.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostmanAuth {
    private String type;
    private final Map<String, List<PostmanKeyValue>> authData = new HashMap<>();

    @JsonAnySetter
    public void setDynamicAuth(String key, List<PostmanKeyValue> value) {
        if (key.equalsIgnoreCase(type)) {
            authData.put(key.toLowerCase(), value);
        }
    }

    public boolean hasAuth() {
        List<PostmanKeyValue> params = getAuthParams();
        return params != null && !params.isEmpty();

    }

    public List<PostmanKeyValue> getAuthParams() {
        return authData.get(type != null ? type.toLowerCase() : null);
    }

    public boolean hasBasicAuth() {
        return "basic".equalsIgnoreCase(type) && hasAuth();
    }

    private String getBasicAuthValue(String key) {
        if (!hasBasicAuth()) return null;

        List<PostmanKeyValue> params = getAuthParams();
        if (CollectionUtils.isEmpty(params)) return null;

        return params.stream()
                .filter(kv -> key.equalsIgnoreCase(kv.getKey()))
                .findFirst()
                .map(PostmanKeyValue::getValue)
                .orElse(null);
    }

    public String getBasicAuthUsername() {
        return getBasicAuthValue("username");
    }

    public String getBasicAuthPassword() {
        return getBasicAuthValue("password");
    }

}
