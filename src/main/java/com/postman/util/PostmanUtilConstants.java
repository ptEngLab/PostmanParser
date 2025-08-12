package com.postman.util;

import java.io.Serial;
import java.io.Serializable;

public class PostmanUtilConstants implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    public static final String BASIC_AUTH_PREFIX = "Basic ";
    public static final String PROTOCOL_SUFFIX = "://";
    public static final String PATH_SEPARATOR = "/";
    public static final String COLON = ":";
    public static final String CONFIG_PREFIX = "lr_";
    public static final String API_CONFIG_KEY = "api_config";
    public static final String NAME_KEY = "name";
    public static final String METHOD_KEY = "method";
    public static final String URL_KEY = "url";
    public static final String QUERY_STRING_KEY = "queryString";
    public static final String PAYLOAD_KEY = "payload";
    public static final String HEADERS_KEY = "headers";
    public static final String JWT_CONFIG_KEY = "jwt_config";
}
