package com.postman.converter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postman.converter.model.PostmanCollection;
import com.postman.converter.util.CommonUtils;
import lombok.AllArgsConstructor;

import java.io.File;

@AllArgsConstructor
public class PostmanParser {
    private final ObjectMapper objectMapper;

    public PostmanCollection parseCollection(File file) throws Exception {
        CommonUtils.validateFile(file);
        return objectMapper.readValue(file, PostmanCollection.class);
    }

}
