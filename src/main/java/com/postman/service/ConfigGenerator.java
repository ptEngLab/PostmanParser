package com.postman.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.postman.model.PostmanCollection;
import com.postman.model.PostmanItem;
import com.postman.model.PostmanRequest;
import com.postman.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.postman.util.PostmanUtilConstants.*;

public class ConfigGenerator {

    private final ObjectMapper yamlMapper;
    private final Map<String, String> postmanVariables;
    private final Logger logger = LoggerFactory.getLogger(ConfigGenerator.class);

    public ConfigGenerator(Map<String, String> postmanVariables) {
        YAMLFactory yamlFactory = new YAMLFactory();
        yamlFactory.disable(YAMLGenerator.Feature.MINIMIZE_QUOTES);
        yamlFactory.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
        yamlFactory.disable(YAMLGenerator.Feature.SPLIT_LINES);
        yamlFactory.enable(YAMLGenerator.Feature.INDENT_ARRAYS);
        yamlFactory.enable(YAMLGenerator.Feature.LITERAL_BLOCK_STYLE);
        this.yamlMapper = new ObjectMapper(yamlFactory);
        this.postmanVariables = postmanVariables != null ? postmanVariables : Map.of();
    }

    public Map<String , Object> generateConfig(PostmanCollection postmanCollection) {
        Map<String , Object> root = new LinkedHashMap<>();
        Map<String , Object> apiConfig = new LinkedHashMap<>();
        AtomicInteger counter = new AtomicInteger(1);
        processItems(postmanCollection.getItem(), apiConfig, counter);
        root.put(API_CONFIG_KEY, apiConfig);
        return root;
    }

    private void processItems(List<PostmanItem> items, Map<String, Object> apiConfig, AtomicInteger counter) {

        for(PostmanItem item : items) {
            if(item.isFolder()) processItems(item.getItem(), apiConfig, counter);
            if(item.hasRequest()) {
                Map<String , Object> itemConfig = convertToEndpointConfig(item, counter.get());
                apiConfig.put(CONFIG_PREFIX + itemConfig.get(NAME_KEY).toString(), itemConfig);
                counter.incrementAndGet();
            }
        }
    }

    private Map<String, Object> convertToEndpointConfig(PostmanItem item, int counter) {
        Map<String, Object> itemConfig = new LinkedHashMap<>();
        if(item == null || !item.hasRequest()) throw new IllegalArgumentException("Item or request cannot be null");
        String configKey = CommonUtils.generateConfigKey(item.getName(), counter);

        PostmanRequest request = item.getRequest();

        itemConfig.put(NAME_KEY, configKey);
        itemConfig.put(METHOD_KEY, request.getMethod());
        itemConfig.put(URL_KEY, request.getRequestUrlWithoutQueryParams(postmanVariables));

        Map<String, String> queryParams = request.getQueryParams(postmanVariables);
        if(!queryParams.isEmpty())  itemConfig.put(QUERY_STRING_KEY, queryParams);

        Map<String, String> headers = request.getHeaders(postmanVariables);
        if(!headers.isEmpty())  itemConfig.put(HEADERS_KEY, headers);

        addPayloadToConfig(itemConfig, request);

        return itemConfig;
    }

    private void addPayloadToConfig(Map<String, Object> itemConfig, PostmanRequest request) {
        // JSON payload
        if (request.hasJsonPayload()) {
            Object jsonPayload = request.getJsonPayload(postmanVariables);
            if (jsonPayload != null) itemConfig.put(PAYLOAD_KEY, jsonPayload);
        }
        // XML payload
        else if (request.hasXmlPayload()) {
            Object xmlPayload = request.getXmlPayload(postmanVariables);
            if (xmlPayload != null) itemConfig.put(PAYLOAD_KEY, xmlPayload);
        }
        // Form data payload
        else if (request.hasFormData()) {
            Map<String, String> formData = request.getFormDataPayload(postmanVariables);
            if (!formData.isEmpty()) itemConfig.put(PAYLOAD_KEY, formData);
        }
        // URL-encoded payload
        else if (request.hasUrlEncodedPayload()) {
            Map<String, String> urlEncodedPayload = request.getUrlEncodedPayload(postmanVariables);
            if (!urlEncodedPayload.isEmpty()) {

                // Replace client_assertion with DevWeb variable placeholder
                if (urlEncodedPayload.containsKey("client_assertion")) {
                    urlEncodedPayload.put("client_assertion", "${jwt_token}");

                    // Decode the original JWT and store its header & payload
                    Map<String, Object> jwt = new LinkedHashMap<>();
                    if (request.getJwtHeader(postmanVariables) != null) {
                        jwt.put(HEADERS_KEY, request.getJwtHeader(postmanVariables));
                    }
                    if (request.getJwtPayload(postmanVariables) != null) {
                        jwt.put(PAYLOAD_KEY, request.getJwtPayload(postmanVariables));
                    }
                    if (!jwt.isEmpty()) {
                        itemConfig.put(JWT_CONFIG_KEY, jwt);
                    }
                }

                itemConfig.put(PAYLOAD_KEY, urlEncodedPayload);
            }
        }
    }

    public void writeConfigToFile(Map<String, Object> config, Path outputPath) {
        try {
            String rawYaml = yamlMapper.writeValueAsString(config);
            Files.writeString(outputPath, rawYaml);

        } catch (Exception e) {
            logger.error("Error writing configuration to file: {}", e.getMessage(), e);
        }
    }


}
