package org.tiogasolutions.push.pub.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PubSesSettings {

    private final String accessKeyId;
    private final String secretKey;
    private final String endpoint;

    private final String testToAddress;
    private final String testFromAddress;
    private final String recipientOverride;

    @JsonCreator
    public PubSesSettings(@JsonProperty("accessKeyId") String accessKeyId,
                          @JsonProperty("secretKey") String secretKey,
                          @JsonProperty("endpoint") String endpoint,

                          @JsonProperty("testToAddress") String testToAddress,
                          @JsonProperty("testFromAddress") String testFromAddress,
                          @JsonProperty("recipientOverride") String recipientOverride) {

        this.accessKeyId = accessKeyId;
        this.secretKey = secretKey;
        this.endpoint = endpoint;

        this.testToAddress = testToAddress;
        this.testFromAddress = testFromAddress;
        this.recipientOverride = recipientOverride;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getRecipientOverride() {
        return recipientOverride;
    }

    public String getTestToAddress() {
        return testToAddress;
    }

    public String getTestFromAddress() {
        return testFromAddress;
    }
}
