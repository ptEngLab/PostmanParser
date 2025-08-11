package com.postman.converter.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostmanBody {
    private String mode; // raw, formdata, urlencoded, file, graphql
    private String raw;
    private List<PostmanKeyValue> formdata;
    private List<PostmanKeyValue> urlencoded;
    private PostmanGraphQL graphql;
    private String file; // for file mode
    private PostmanBodyOptions options;

    public boolean hasUrlEncoded() {
        return CollectionUtils.isNotEmpty(urlencoded);
    }

    public boolean hasFormData() {
        return CollectionUtils.isNotEmpty(formdata);
    }

    public boolean hasRawPayload() {
        return StringUtils.isNoneBlank(raw) && Strings.CI.equals(mode, "raw");
    }

    public boolean isJsonPayload() {
        return hasRawPayload() && options != null && options.hasJsonLanguage();
    }

    public boolean isXmlPayload() {
        return hasRawPayload() && options != null && options.hasXmlLanguage();
    }

    public boolean isTextPayload() {
        return hasRawPayload() && options != null && options.hasTextLanguage();
    }

    public boolean hasGraphQLPayload() {
        return graphql != null;
    }

    public boolean hasFilePayload() {
        return StringUtils.isNotBlank(file) && Strings.CI.equals(mode, "file");
    }

}
