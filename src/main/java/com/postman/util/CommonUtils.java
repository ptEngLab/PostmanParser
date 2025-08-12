package com.postman.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postman.model.PostmanKeyValue;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    public static String resolveAndJoin(List<String> parts, String delimiter, Map<String, String> postmanVariables) {
        if (parts == null || parts.isEmpty()) return "";
        return parts.stream()
                .map(p -> replaceVariables(p, postmanVariables))
                .collect(Collectors.joining(delimiter));
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
            Object parsedJson = JSON_MAPPER.readValue(json, Object.class);
            if(parsedJson instanceof Map && ((Map<?, ?>) parsedJson).isEmpty()) return null;
            if(parsedJson instanceof List && ((List<?>) parsedJson).isEmpty()) return null;
            return parsedJson;

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


    public static String generateConfigKey(String name, int counter) {
        String sanitizedName;
        if (name == null || name.isBlank()) {
            sanitizedName = "unknown_endpoint";
        } else {
            sanitizedName = name
                    .replaceAll("[^a-zA-Z0-9_\\s-]", "_") // replace special chars with underscore
                    .replaceAll("[-\\s]+", "_")           // replace hyphens/spaces with underscore
                    .replaceAll("_+", "_")                // collapse multiple underscores into one
                    .replaceAll("^_|_$", "")               // remove leading/trailing underscores
                    .toLowerCase();
        }
        return String.format("%02d_%s", counter, sanitizedName);
    }



    public static boolean isLikelyJson(String content) {
        if (StringUtils.isBlank(content)) return false;
        String trimmed = content.trim();
        return (trimmed.startsWith("{") && trimmed.endsWith("}")) || (trimmed.startsWith("[") && trimmed.endsWith("]"));
    }

    public static boolean isLikelyXml(String content) {
        if (StringUtils.isBlank(content)) return false;
        String trimmed = content.trim();
        return trimmed.startsWith("<") && trimmed.endsWith(">");
    }

    public static Map<String , Object> decodeJwt(String jwt) {
        if (StringUtils.isBlank(jwt)) return Map.of();

        String[] parts = jwt.split("\\.");
        if (parts.length != 3) {
            logger.warn("Invalid JWT format: {}", jwt);
            return Map.of();
        }

        ObjectMapper mapper = new ObjectMapper();
        Map<String , Object> decoded = new LinkedHashMap<>();

        try {
            String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]), StandardCharsets.UTF_8);
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);

            decoded.put("header", mapper.readValue(headerJson, Map.class));
            decoded.put("payload", mapper.readValue(payloadJson, Map.class));

        } catch (Exception e) {
            logger.warn("Failed to decode JWT: {}", jwt, e);
            return Map.of();
        }
        return decoded;
    }


}
