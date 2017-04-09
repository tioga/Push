package org.tiogasolutions.push.pub.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.lib.hal.HalItem;
import org.tiogasolutions.lib.hal.HalLinks;

public class PubConfig extends HalItem {

    private final String domainName;
    private final String domainPassword;
    private final int domainRetention;

    private final PubSesConfig sesConfig;
    private final PubSmtpConfig smtpConfig;
    private final PubTwilioConfig twilioConfig;
    private final PubXmppConfig xmppConfig;


    public PubConfig(HttpStatusCode httpStatusCode,
                     @JsonProperty("_links") HalLinks _links,
                     @JsonProperty("domainName") String domainName,
                     @JsonProperty("domainPassword") String domainPassword,
                     @JsonProperty("domainRetention") int domainRetention,

                     @JsonProperty("sesConfig") PubSesConfig sesConfig,
                     @JsonProperty("smtpConfig") PubSmtpConfig smtpConfig,
                     @JsonProperty("twilioConfig") PubTwilioConfig twilioConfig,
                     @JsonProperty("xmppConfig") PubXmppConfig xmppConfig) {

        super(httpStatusCode, _links);

        this.domainName = domainName;
        this.domainPassword = domainPassword;
        this.domainRetention = domainRetention;

        this.sesConfig = sesConfig;
        this.smtpConfig = smtpConfig;
        this.twilioConfig = twilioConfig;
        this.xmppConfig = xmppConfig;
    }

    public PubSesConfig getSesConfig() {
        return sesConfig;
    }

    public String getDomainName() {
        return domainName;
    }

    public String getDomainPassword() {
        return domainPassword;
    }

    public int getDomainRetention() {
        return domainRetention;
    }

    public PubSmtpConfig getSmtpConfig() {
        return smtpConfig;
    }

    public PubTwilioConfig getTwilioConfig() {
        return twilioConfig;
    }

    public PubXmppConfig getXmppConfig() {
        return xmppConfig;
    }
}
