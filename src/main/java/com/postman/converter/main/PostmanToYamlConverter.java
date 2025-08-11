package com.postman.converter.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostmanToYamlConverter {

    private static final Logger logger = LoggerFactory.getLogger(PostmanToYamlConverter.class);

    public static void main(String[] args) {

        String[] preparedArgs = prepareArgs(args);
        String postmanCollectionFile = preparedArgs[0];
        String postmanEnvironmentFile = preparedArgs.length > 1 ? preparedArgs[1]: null;
        String outputFile = preparedArgs.length > 2 ? preparedArgs[2]: "api_config.yaml";


    }

    private static String[] prepareArgs(String[] args) {
        if( args.length < 1) {
            logger.error("Usage: java -jar PostmanToYamlConverter.jar <postman-collection.json> [postman-environment.json] [output-file.yaml]");
            System.exit(1);
        }
        return args;
    }
}
