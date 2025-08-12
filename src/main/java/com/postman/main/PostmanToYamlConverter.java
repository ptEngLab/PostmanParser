package com.postman.main;

import com.postman.model.PostmanCollection;
import com.postman.model.PostmanKeyValue;
import com.postman.service.ConfigGenerator;
import com.postman.service.PostmanParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

public class PostmanToYamlConverter {

    private static final Logger logger = LoggerFactory.getLogger(PostmanToYamlConverter.class);

    public static void main(String[] args) {
        validateArgs(args);

        String postmanCollectionFile = args[0];
        String postmanEnvironmentFile = args.length > 1 ? args[1] : null;
        String outputFile = args.length > 2 ? args[2] : "api_config.yaml";

        PostmanParser postmanParser = new PostmanParser();

        try {
            PostmanCollection postmanCollection = postmanParser.parseCollection(new File(postmanCollectionFile));
            List<PostmanKeyValue> collectionVars = postmanCollection.getVariable();

            List<PostmanKeyValue> environmentVars = postmanEnvironmentFile != null
                    ? postmanParser.parseEnvironment(new File(postmanEnvironmentFile)).getValues()
                    : Collections.emptyList();

            // Merge variables (environment overrides collection)
            Map<String, String> variables = new HashMap<>();
            for (PostmanKeyValue var : collectionVars)  variables.put(var.getKey(), var.getValue());
            for (PostmanKeyValue var : environmentVars)  variables.put(var.getKey(), var.getValue());

            ConfigGenerator configGenerator = new ConfigGenerator(variables);
            Map<String, Object> apiConfig = configGenerator.generateConfig(postmanCollection);
            configGenerator.writeConfigToFile(apiConfig, Paths.get(outputFile));

            printSummary(apiConfig, outputFile);


        } catch (Exception e) {
            logger.error("Failed to parse Postman files or generate YAML config", e);
            System.exit(1);
        }
    }

    private static void validateArgs(String[] args) {
        if (args.length < 1) {
            logger.error("Usage: java -jar PostmanToYamlConverter.jar <postman-collection.json> [postman-environment.json] [output-file.yaml]");
            System.exit(1);
        }
    }

    private static void printSummary(Map<String, Object> apiConfig, String outputFile) {
        logger.info("=========================================");
        logger.info("Postman to YAML Converter Summary");
        logger.info("=========================================");
        logger.info("Successfully converted Postman collection and environment variables to YAML configuration.");
        logger.info("Output file: {}", outputFile);

        Object configSection = apiConfig.get("api_config");

        if(configSection instanceof Map<?,?> configMap) {
            logger.info("Total API endpoints configured: {}", configMap.size());
        } else {
            logger.warn("No API configurations found in the generated YAML.");
        }
    }

}
