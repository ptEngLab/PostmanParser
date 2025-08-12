package com.postman.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postman.model.PostmanCollection;
import com.postman.model.PostmanEnvironment;
import com.postman.util.CommonUtils;

import java.io.File;

public class PostmanParser {
    private final ObjectMapper objectMapper;

    public PostmanParser() {
        this.objectMapper = new ObjectMapper();
    }

    public PostmanCollection parseCollection(File file) throws Exception {
        CommonUtils.validateFile(file);
        return objectMapper.readValue(file, PostmanCollection.class);
    }

    public PostmanEnvironment parseEnvironment(File file) throws Exception {
        CommonUtils.validateFile(file);
        return objectMapper.readValue(file, PostmanEnvironment.class);
    }
}
