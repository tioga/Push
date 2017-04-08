package org.tiogasolutions.push.engine.system;

import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.lib.hal.HalItem;
import org.tiogasolutions.lib.hal.HalLink;
import org.tiogasolutions.lib.hal.HalLinks;
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
import java.util.Map;

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
        Response.ResponseBuilder builder = Response.status(item.getHttpStatusCode().getCode()).entity(item);

        for (Map.Entry<String,HalLink> entry : item.get_links().entrySet()) {
            String rel = entry.getKey();
            HalLink link = entry.getValue();
            builder.link(link.getHref(), rel);
        }

        return builder;
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
}
