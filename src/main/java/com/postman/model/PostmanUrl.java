package com.postman.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostmanUrl {
    private String raw;
    private String protocol;
    private List<String> host;
    private List<String> path;
    private List<PostmanKeyValue> query;
    private Map<String, String> variable; // path variables

    public boolean hasQueryParameters() {
        return CollectionUtils.isNotEmpty(query);
    }
    public boolean hasProtocol() {
        return StringUtils.isNotBlank(protocol);
    }
    public boolean hasHost() {
        return CollectionUtils.isNotEmpty(host);
    }
    public boolean hasPath() {
        return CollectionUtils.isNotEmpty(path);
    }


}
