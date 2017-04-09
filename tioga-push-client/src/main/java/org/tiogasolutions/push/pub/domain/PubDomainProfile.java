package org.tiogasolutions.push.pub.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.lib.hal.HalItem;
import org.tiogasolutions.lib.hal.HalLinks;

public class PubDomainProfile extends HalItem {

    private final String profileId;
    private final String revision;

    private final String domainName;
    private final DomainStatus domainStatus;

    private final String apiKey;
    private final String apiPassword;

    private final int retentionDays;

    public PubDomainProfile(HttpStatusCode httpStatusCode,
                            @JsonProperty("_links") HalLinks _links,
                            @JsonProperty("profileId") String profileId,
                            @JsonProperty("revision") String revision,
                            @JsonProperty("domainName") String domainName,
                            @JsonProperty("domainStatus") DomainStatus domainStatus,
                            @JsonProperty("apiKey") String apiKey,
                            @JsonProperty("apiPassword") String apiPassword,
                            @JsonProperty("retentionDays") int retentionDays) {

        super(httpStatusCode, _links);

        this.profileId = profileId;
        this.revision = revision;
        this.domainName = domainName;
        this.domainStatus = domainStatus;
        this.apiKey = apiKey;
        this.apiPassword = apiPassword;
        this.retentionDays = retentionDays;
    }

    public String getProfileId() {
        return profileId;
    }

    public String getRevision() {
        return revision;
    }

    public String getDomainName() {
        return domainName;
    }

    public DomainStatus getDomainStatus() {
        return domainStatus;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiPassword() {
        return apiPassword;
    }

    public int getRetentionDays() {
        return retentionDays;
    }
}
