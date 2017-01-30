package org.tiogasolutions.push.pub.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.pub.PubItem;
import org.tiogasolutions.pub.PubLinks;
import org.tiogasolutions.pub.PubStatus;

public class PubDomain extends PubItem {

    private final String domainName;
    private final String domainPassword;
    private final int domainRetention;

    private final PubSesSettings sesSettings;
    private final PubSmtpSettings smtpSettings;
    private final PubTwilioSettings twilioSettings;
    private final PubXmppSettings xmppSettings;

    public PubDomain(@JsonProperty("_status") PubStatus _status,
                     @JsonProperty("_links") PubLinks _links,
                     @JsonProperty("domainName") String domainName,
                     @JsonProperty("domainPassword") String domainPassword,
                     @JsonProperty("domainRetention") int domainRetention,

                     @JsonProperty("sesSettings") PubSesSettings sesSettings,
                     @JsonProperty("smtpSettings") PubSmtpSettings smtpSettings,
                     @JsonProperty("twilioSettings") PubTwilioSettings twilioSettings,
                     @JsonProperty("xmppSettings") PubXmppSettings xmppSettings) {

        super(_status, _links);

        this.domainName = domainName;
        this.domainPassword = domainPassword;
        this.domainRetention = domainRetention;

        this.sesSettings = sesSettings;
        this.smtpSettings = smtpSettings;
        this.twilioSettings = twilioSettings;
        this.xmppSettings = xmppSettings;
    }

    public PubSesSettings getSesSettings() {
        return sesSettings;
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

    public PubSmtpSettings getSmtpSettings() {
        return smtpSettings;
    }

    public PubTwilioSettings getTwilioSettings() {
        return twilioSettings;
    }

    public PubXmppSettings getXmppSettings() {
        return xmppSettings;
    }
}
