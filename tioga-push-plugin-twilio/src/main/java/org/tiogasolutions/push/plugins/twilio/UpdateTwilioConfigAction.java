package org.tiogasolutions.push.plugins.twilio;

import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.pub.internal.RequestErrors;
import org.tiogasolutions.push.pub.internal.ValidatableAction;
import org.tiogasolutions.push.pub.internal.ValidationUtils;

import java.util.Map;

public class UpdateTwilioConfigAction implements ValidatableAction {

    private final DomainProfileEntity domain;
    private final String accountSid;
    private final String authToken;
    private final String testFromNumber;
    private final String testToNumber;

    public UpdateTwilioConfigAction(DomainProfileEntity domain, Map<String, String> params) {

        this.domain = domain;

        this.accountSid = params.get("accountSid");
        this.authToken = params.get("authToken");

        this.testFromNumber = params.get("testFromNumber");
        this.testToNumber = params.get("testToNumber");
    }

    public UpdateTwilioConfigAction(DomainProfileEntity domain, String accountSid, String authToken, String testFromNumber, String testToNumber) {
        this.domain = domain;
        this.accountSid = accountSid;
        this.authToken = authToken;
        this.testFromNumber = testFromNumber;
        this.testToNumber = testToNumber;
    }


    @Override
    public RequestErrors validate(RequestErrors errors) {
        ValidationUtils.requireValue(errors, accountSid, "The Twilio account SID must be specified.");
        ValidationUtils.requireValue(errors, authToken, "The Twilio Authentication Token must be specified.");

        ValidationUtils.requireValue(errors, testFromNumber, "The Twilio Originating Phone Number must be specified.");
        ValidationUtils.requireValue(errors, testToNumber, "The Twilio SMS Recipient must be specified.");
        return errors;
    }

    public DomainProfileEntity getDomain() {
        return domain;
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
