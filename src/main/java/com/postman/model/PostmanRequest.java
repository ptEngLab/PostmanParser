package com.postman.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.postman.util.CommonUtils.*;
import static com.postman.util.PostmanUtilConstants.*;

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
    private Map<String, Object> decodedClientAssertion;

    private boolean urlExists() { return url != null; }
    public boolean hasAuth() { return auth != null && auth.hasAuth(); }
    public boolean hasBody() { return body != null && StringUtils.isNotBlank(body.getMode()); }
    public boolean hasQueryParams() { return urlExists() && url.hasQueryParameters(); }
    public boolean hasFormData() { return hasBody() && body.hasFormData(); }
    public boolean hasUrlEncodedPayload() { return hasBody() && body.hasUrlEncoded(); }
    public boolean hasProtocol() { return urlExists() && url.hasProtocol(); }
    public boolean hasHost() { return urlExists() && url.hasHost(); }
    public boolean hasHeaders() { return header != null && !header.isEmpty(); }
    public boolean hasPath() { return urlExists() && url.hasPath(); }
    public boolean hasJsonPayload() { return hasBody() && body.hasRawPayload() && body.isJsonPayload(); }
    public boolean hasXmlPayload() { return hasBody() && body.hasRawPayload() && body.isXmlPayload(); }

    public String getRequestUrlWithoutQueryParams(Map<String, String> vars) {
        if( !urlExists() ) return "";
        return hasQueryParams()  ? buildBaseUrl(vars) : replaceVariables(url.getRaw(), vars) ;
    }

    public String getHost(Map<String, String> vars) {
        return hasHost() ? resolveAndJoin(url.getHost(), ".", vars) : "";
    }

    public String getPath(Map<String, String> vars) {
        return hasPath() ? resolveAndJoin(url.getPath(), PATH_SEPARATOR, vars) : "";
    }

    public String getProtocol() {
        return hasProtocol() ? url.getProtocol() : "";
    }

    public String buildBaseUrl(Map<String, String> vars) {
        String protocol = getProtocol();
        String host = getHost(vars);
        String path = getPath(vars);
        StringBuilder urlBuilder = new StringBuilder();
        if(StringUtils.isNotBlank(protocol)) urlBuilder.append(protocol).append(PROTOCOL_SUFFIX);
        if(StringUtils.isNotBlank(host)) urlBuilder.append(host);
        if(StringUtils.isNotBlank(path)) urlBuilder.append(PATH_SEPARATOR).append(path);
        return urlBuilder.toString();
    }

    public Map<String, String> getHeaders(Map<String, String> vars) {
        Map<String, String> headers = hasHeaders()
                ? extractKeyValuePairs(header, vars)
                : new LinkedHashMap<>();
        return addBasicAuthHeaders(headers, vars);
    }

    public Map<String, String> getQueryParams(Map<String, String> vars) {
        return hasQueryParams()
                ? extractKeyValuePairs(url.getQuery(), vars)
                :  Collections.emptyMap();
    }

    public Map<String, String> getUrlEncodedPayload(Map<String, String> vars) {
        if(!hasUrlEncodedPayload()) return new LinkedHashMap<>();
        Map<String, String> payload = extractKeyValuePairs(body.getUrlencoded(), vars);
        return payload.isEmpty() ? Collections.emptyMap() : payload;
    }

    public Map<String, String> getFormDataPayload(Map<String, String> vars) {
        return hasFormData()
                ? extractKeyValuePairs(body.getFormdata(), vars)
                : Collections.emptyMap();
    }

    public Object getJsonPayload(Map<String, String> postmanVariables) {
        if(!hasJsonPayload()) return null;
        String resolvedRaw = replaceVariables(body.getRaw(), postmanVariables);
        return parseJson(resolvedRaw);
    }

    public Object getXmlPayload(Map<String, String> postmanVariables) {
        if(!hasXmlPayload()) return null;
        return replaceVariables(body.getRaw(), postmanVariables);
    }

    public Map<String, String> addBasicAuthHeaders(Map<String, String> headers, Map<String, String> vars) {
        if (hasAuth() && auth.hasBasicAuth()) {
            String username = replaceVariables(auth.getBasicAuthUsername(), vars);
            String password = replaceVariables(auth.getBasicAuthPassword(), vars);
            if (StringUtils.isNoneBlank(username, password)) {
                String credentials = username + COLON + password;
                String encoded = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
                headers.put("Authorization", BASIC_AUTH_PREFIX + encoded);
            }
        }
        return headers;
    }

    private Map<String , Object> getDecodedClientAssertion(Map<String, String> vars) {
        if(decodedClientAssertion != null) return decodedClientAssertion;
        Map<String, String> clientAssertion = getUrlEncodedPayload(vars);
        if (clientAssertion != null && !clientAssertion.isEmpty() && clientAssertion.containsKey("client_assertion")) {
            decodedClientAssertion = decodeJwt(clientAssertion.get("client_assertion"));
        }
        return decodedClientAssertion;
    }

    public Object getJwtHeader(Map<String, String> vars) {
        Map<String, Object> decoded = getDecodedClientAssertion(vars);
        return decoded != null ? decoded.get("header") : null;
    }

    public Object getJwtPayload(Map<String, String> vars) {
        Map<String, Object> decoded = getDecodedClientAssertion(vars);
        return decoded != null ? decoded.get("payload") : null;
    }

}
