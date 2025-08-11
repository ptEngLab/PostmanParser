package com.postman.converter.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.postman.converter.util.CommonUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostmanRequest {
    private String method;
    private PostmanUrl url;
    private List<PostmanKeyValue> header;
    private PostmanBody body;
    private PostmanAuth auth;
    private List<PostmanKeyValue> variable; // request-level variables
    private String description;

    private boolean hasUrl() {
        return url != null;
    }
    public boolean hasAuth() {
        return auth != null && auth.hasAuth();
    }

    public boolean hasBody() {
        return body != null && StringUtils.isNotBlank(body.getMode());
    }

    public boolean hasQueryParams() {
        return hasUrl() && url.hasQueryParameters();
    }

    public boolean hasFormData() {
        return hasBody() && body.hasFormData();
    }

    public boolean hasUrlEncodedPayload() {
        return hasBody() && body.hasUrlEncoded();
    }

    public boolean hasProtocol() {
        return hasUrl() && url.hasProtocol();
    }

    public boolean hasHost() {
        return hasUrl() && url.hasHost();
    }

    public boolean hasHeaders() {
        return header != null && !header.isEmpty();
    }

    public boolean hasPath() {
        return hasUrl() && url.hasPath();
    }

    public String getRequestUrlWithoutQueryParams(Map<String, String> postmanVariables) {
        return hasQueryParams()
                ? buildBaseUrl(postmanVariables)
                : CommonUtils.replaceVariables(url.getRaw(), postmanVariables);
    }


    private String resolveAndJoin(List<String> parts, String delimiter, Map<String, String> postmanVariables) {
        if (parts == null || parts.isEmpty()) return "";
        return parts.stream()
                .map(p -> CommonUtils.replaceVariables(p, postmanVariables))
                .collect(Collectors.joining(delimiter));
    }

    public String getHost(Map<String, String> postmanVariables) {
        return hasHost() ? resolveAndJoin(url.getHost(), ".", postmanVariables) : "";
    }

    public String getPath(Map<String, String> postmanVariables) {
        return hasPath() ? resolveAndJoin(url.getPath(), "/", postmanVariables) : "";
    }

    public String getProtocol() {
        return StringUtils.defaultString(url.getProtocol());
    }

    public String buildBaseUrl(Map<String, String> postmanVariables) {
        return getProtocol() + "://" + getHost(postmanVariables) + getPath(postmanVariables);

    }

    public Map<String, String> getHeaders(Map<String, String> postmanVariables) {
        Map<String, String> headers = hasHeaders()
                ? CommonUtils.extractKeyValuePairs(header, postmanVariables)
                : new LinkedHashMap<>();
        return addBasicAuthHeaders(headers, postmanVariables);
    }

    public Map<String, String> getQueryParams(Map<String, String> postmanVariables) {
        return hasQueryParams()
                ? CommonUtils.extractKeyValuePairs(url.getQuery(), postmanVariables)
                : new LinkedHashMap<>();
    }

    public Map<String, String> getUrlEncodedPayload(Map<String, String> postmanVariables) {
        return hasUrlEncodedPayload()
                ? CommonUtils.extractKeyValuePairs(body.getUrlencoded(), postmanVariables)
                : new LinkedHashMap<>();
    }

    public Map<String, String> getFormDataPayload(Map<String, String> postmanVariables) {
        return hasFormData()
                ? CommonUtils.extractKeyValuePairs(body.getFormdata(), postmanVariables)
                : new LinkedHashMap<>();
    }

    public boolean hasJsonPayload() {
        return hasBody() && body.hasRawPayload() && body.isJsonPayload();
    }

    public boolean hasXmlPayload() {
        return hasBody() && body.hasRawPayload() && body.isXmlPayload();
    }

    public Object getJsonPayload() {
        return hasJsonPayload() ? CommonUtils.parseJson(body.getRaw()) : null;
    }

    public Object getXmlPayload() {
        return hasXmlPayload() ? body.getRaw() : null;
    }


    public Map<String, String> addBasicAuthHeaders(Map<String, String> headers, Map<String, String> postmanVariables) {

        if (hasAuth() && auth.hasBasicAuth()) {
            String username = CommonUtils.replaceVariables(auth.getBasicAuthUsername(), postmanVariables);
            String password = CommonUtils.replaceVariables(auth.getBasicAuthPassword(), postmanVariables);

            if (StringUtils.isNoneBlank(username, password)) {
                String credentials = username + ":" + password;
                String encoded = Base64.getEncoder().encodeToString(credentials.getBytes());
                headers.put("Authorization", "Basic " + encoded);
            }
        }
        return headers;
    }
}
