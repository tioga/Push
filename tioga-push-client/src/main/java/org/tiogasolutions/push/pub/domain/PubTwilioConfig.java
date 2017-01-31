package org.tiogasolutions.push.pub.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PubTwilioConfig {

    private final String accountSid;
    private final String authToken;

    private final String testFromNumber;
    private final String testToNumber;

    @JsonCreator
    public PubTwilioConfig(@JsonProperty("accountSid") String accountSid,
                           @JsonProperty("authToken") String authToken,
                           @JsonProperty("testFromNumber") String testFromNumber,
                           @JsonProperty("testToNumber") String testToNumber) {

        this.accountSid = accountSid;
        this.authToken = authToken;
        this.testFromNumber = testFromNumber;
        this.testToNumber = testToNumber;
    }

    public String getAccountSid() {
        return accountSid;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getTestFromNumber() {
        return testFromNumber;
    }

    public String getTestToNumber() {
        return testToNumber;
    }
}
