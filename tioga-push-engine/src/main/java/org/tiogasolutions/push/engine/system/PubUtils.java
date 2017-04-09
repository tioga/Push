package org.tiogasolutions.push.engine.system;

import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.dev.domain.query.ListQueryResult;
import org.tiogasolutions.dev.domain.query.QueryResult;
import org.tiogasolutions.lib.hal.*;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.system.PluginManager;
import org.tiogasolutions.push.plugins.ses.SesEmailConfig;
import org.tiogasolutions.push.plugins.ses.SesEmailPlugin;
import org.tiogasolutions.push.plugins.smtp.SmtpEmailConfig;
import org.tiogasolutions.push.plugins.smtp.SmtpEmailPlugin;
import org.tiogasolutions.push.plugins.twilio.TwilioConfig;
import org.tiogasolutions.push.plugins.twilio.TwilioPlugin;
import org.tiogasolutions.push.plugins.xmpp.XmppConfig;
import org.tiogasolutions.push.plugins.xmpp.XmppPlugin;
import org.tiogasolutions.push.pub.domain.*;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.tiogasolutions.push.kernel.Paths.$admin;
import static org.tiogasolutions.push.kernel.Paths.$api_v3;
import static org.tiogasolutions.push.kernel.Paths.$domains;

public class PubUtils {

    private final UriInfo uriInfo;

    public PubUtils(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    public PubUtils(ContainerRequestContext requestContext) {
        this.uriInfo = requestContext.getUriInfo();
    }

    public UriInfo getUriInfo() {
        return uriInfo;
    }

    public Response.ResponseBuilder toResponse(HalItem item) {

        int statusCode = item.getHttpStatusCode().getCode();

        Response.ResponseBuilder builder = Response
                .status(statusCode)
                .entity(item);

        if (statusCode == HttpStatusCode.CREATED.getCode()) {
            HalLink link = item.get_links().getLink("self");
            builder.location( link.getHref() );
        }

        for (Map.Entry<String,HalLink> entry : item.get_links().entrySet()) {
            String rel = entry.getKey();
            HalLink link = entry.getValue();
            builder.link(link.getHref(), rel);
        }

        return builder;
    }

    public HalItem fromDomainProfileEntities(HttpStatusCode statusCode, List<DomainProfileEntity> domainProfileEntities) {

        HalLinks links = HalLinks.builder()
                .add("self", newAdminDomainsLink())
                .build();

        List<HalItem> items = new ArrayList<>();
        for (DomainProfileEntity domainProfileEntity : domainProfileEntities) {
            PubDomainProfile pubDomainProfile = fromDomainProfileEntity(null, domainProfileEntity, true);
            items.add(pubDomainProfile);
        }

        QueryResult<HalItem> itemResults = ListQueryResult.newComplete(HalItem.class, items);

        return new HalItemWrapper<>(itemResults, statusCode, links);
    }

    public PubDomainProfile fromDomainProfileEntity(HttpStatusCode statusCode, DomainProfileEntity domainProfile, boolean admin) {

        HalLinksBuilder builder = HalLinks.builder();
        if (admin) {
            builder.add("self", newDomainLink(true, domainProfile.getDomainKey()));
            builder.add("domains", newAdminDomainsLink());
            builder.add("client", newDomainLink(false, domainProfile.getDomainKey()));
        } else {
            builder.add("self", newDomainLink(false, domainProfile.getDomainKey()));
        }
        HalLinks links = builder.build();

        return new PubDomainProfile(
                statusCode,
                links,
                domainProfile.getDomainId(),
                domainProfile.getRevision(),
                domainProfile.getDomainKey(),
                DomainStatus.ACTIVE,
                domainProfile.getDomainKey(),
                domainProfile.getDomainPassword(),
                domainProfile.getRetentionDays());
    }

    public PubConfig toConfig(HttpStatusCode statusCode, DomainProfileEntity domainProfile, PluginManager pluginManager) {

        SesEmailConfig sesEmailConfig = pluginManager.getPlugin(SesEmailPlugin.class).getConfig(domainProfile);
        PubSesConfig sesSettings = (sesEmailConfig == null) ? null : new PubSesConfig(
                sesEmailConfig.getAccessKeyId(),
                sesEmailConfig.getSecretKey(),
                sesEmailConfig.getEndpoint(),

                sesEmailConfig.getTestToAddress(),
                sesEmailConfig.getTestFromAddress(),
                sesEmailConfig.getRecipientOverride()
        );

        SmtpEmailConfig smtpEmailConfig = pluginManager.getPlugin(SmtpEmailPlugin.class).getConfig(domainProfile);
        PubSmtpConfig smtpSettings = (smtpEmailConfig == null) ? null : new PubSmtpConfig(
                smtpEmailConfig.getUsername(),
                smtpEmailConfig.getPassword(),

                smtpEmailConfig.getAuthType(),
                smtpEmailConfig.getPort(),
                smtpEmailConfig.getServerName(),

                smtpEmailConfig.getTestToAddress(),
                smtpEmailConfig.getTestFromAddress(),
                smtpEmailConfig.getRecipientOverride()
        );

        TwilioConfig twilioConfig = pluginManager.getPlugin(TwilioPlugin.class).getConfig(domainProfile);
        PubTwilioConfig twilioSettings = (twilioConfig == null) ? null : new PubTwilioConfig(
                twilioConfig.getAccountSid(),
                twilioConfig.getAuthToken(),
                twilioConfig.getTestFromNumber(),
                twilioConfig.getTestToNumber()
        );

        XmppConfig xmppConfig = pluginManager.getPlugin(XmppPlugin.class).getConfig(domainProfile);
        PubXmppConfig xmppSettings = (xmppConfig == null) ? null : new PubXmppConfig(
                xmppConfig.getUsername(),
                xmppConfig.getPassword(),
                xmppConfig.getHost(),
                xmppConfig.getPort(),
                xmppConfig.getServiceName(),
                xmppConfig.getTestToAddress(),
                xmppConfig.getRecipientOverride()
        );

        return new PubConfig(
                statusCode,
                HalLinks.empty(),

                domainProfile.getDomainKey(),
                domainProfile.getDomainPassword(),
                domainProfile.getRetentionDays(),

                sesSettings,
                smtpSettings,
                twilioSettings,
                xmppSettings);
    }

    public HalLink newAdminDomainsLink() {
        return HalLink.create(uriInfo.getBaseUriBuilder()
                .path($api_v3)
                .path($admin)
                .path($domains)
                .build());
    }

    private HalLink newDomainLink(boolean admin, String domainName) {
        if (admin == false) {
            return HalLink.create(uriInfo.getBaseUriBuilder().path($api_v3).build());
        } else {
            return HalLink.create(uriInfo.getBaseUriBuilder()
                    .path($api_v3)
                    .path($admin)
                    .path($domains)
                    .path(domainName)
                    .build());
        }
    }

    public HalLink newAdminLink() {
        return HalLink.create(uriInfo.getBaseUriBuilder()
                .path($api_v3)
                .path($admin)
                .build());
    }
}
