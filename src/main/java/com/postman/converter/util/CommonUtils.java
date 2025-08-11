package com.postman.converter.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postman.converter.model.PostmanKeyValue;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {

    private static final Pattern POSTMAN_VARIABLE_PATTERN = Pattern.compile("\\{\\{(.*?)}}");
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(CommonUtils.class);

    public static Map<String, String> extractKeyValuePairs(List<PostmanKeyValue> keyValues,
                                                           Map<String, String> postmanVariables) {
        Map<String, String> map =  new LinkedHashMap<>();

        for (PostmanKeyValue keyValue : keyValues) {
            if (!keyValue.isDisabled() && StringUtils.isNotBlank(keyValue.getKey())) {
                String resolvedValue = replaceVariables(keyValue.getValue(), postmanVariables);
                map.put(keyValue.getKey(), resolvedValue);
            }
        }
        return map;

    }
    
    public static String replaceVariables(String value, Map<String, String> postmanVariables) {
        if (StringUtils.isBlank(value) || MapUtils.isEmpty(postmanVariables)) {
            return value;
        }

        Matcher matcher = POSTMAN_VARIABLE_PATTERN.matcher(value);

        StringBuilder result = new StringBuilder();
        int lastEnd = 0;
        while (matcher.find()) {
            result.append(value, lastEnd, matcher.start());
            String variableName = matcher.group(1).trim();

            String replacement = postmanVariables.getOrDefault(variableName, "${"+variableName+"}");
            result.append(replacement);
            lastEnd = matcher.end();
        }
        result.append(value, lastEnd, value.length());
        return result.toString();
    }

    public static Object parseJson(String json) {
        try {
            return JSON_MAPPER.readValue(json, Object.class);
        } catch (Exception e) {
            logger.warn("Failed to parse JSON: {}", json, e);
            return json;
        }
    }

    public static void validateFile(File file) {
        if(!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("Invalid file: " + file.getAbsolutePath());
        }
    }

    public static File validateInputFile(Path path) {
        File file = path.toFile();
        validateFile(file);
        if (!file.canRead()) {
            throw new IllegalArgumentException("Cannot read file: " + file.getAbsolutePath());
        }
        return file;
    }
}
