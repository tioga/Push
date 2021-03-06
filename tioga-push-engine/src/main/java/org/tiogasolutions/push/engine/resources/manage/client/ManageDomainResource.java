/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */
package org.tiogasolutions.push.engine.resources.manage.client;

import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.net.InetMediaType;
import org.tiogasolutions.push.engine.jaxrs.security.MngtAuthentication;
import org.tiogasolutions.push.engine.resources.manage.client.emails.ManageEmailsResource;
import org.tiogasolutions.push.engine.view.Thymeleaf;
import org.tiogasolutions.push.engine.view.ThymeleafViewFactory;
import org.tiogasolutions.push.kernel.accounts.Account;
import org.tiogasolutions.push.kernel.accounts.AccountStore;
import org.tiogasolutions.push.kernel.accounts.DomainStore;
import org.tiogasolutions.push.kernel.actions.UpdateDomainAction;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.plugins.Plugin;
import org.tiogasolutions.push.kernel.requests.PushRequest;
import org.tiogasolutions.push.kernel.requests.PushRequestStore;
import org.tiogasolutions.push.kernel.requests.QueryResult;
import org.tiogasolutions.push.kernel.system.PluginManager;
import org.tiogasolutions.push.pub.common.PushType;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@MngtAuthentication
public class ManageDomainResource {

    private final DomainProfileEntity domainProfile;
    private final PluginManager pluginManager;
    private final ExecutionManager executionManager;
    private final DomainStore domainStore;
    private final AccountStore accountStore;
    private final PushRequestStore pushRequestStore;

    public ManageDomainResource(ExecutionManager executionManager, DomainStore domainStore, AccountStore accountStore, PushRequestStore pushRequestStore, PluginManager pluginManager, String domainKey) {
        this.pluginManager = pluginManager;
        this.executionManager = executionManager;
        this.domainStore = domainStore;
        this.accountStore = accountStore;
        this.pushRequestStore = pushRequestStore;

        this.domainProfile = domainStore.getByDomainName(domainKey);

        if (domainProfile == null) {
            throw ApiException.notFound(domainKey);
        }

        this.executionManager.getContext().setDomain(domainProfile);
    }

    private Account getAccount() {
        return executionManager.getContext().getAccount();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Thymeleaf viewDomain() throws Exception {
        String lastMessage = executionManager.getContext().getSession().getLastMessage();
        executionManager.getContext().setLastMessage(null);
        accountStore.update(getAccount());

        ManageDomainModel model = new ManageDomainModel(executionManager, domainProfile, pluginManager, lastMessage);
        return new Thymeleaf(executionManager.getContext().getSession(), ThymeleafViewFactory.MANAGE_API_CLIENT, model);
    }

    @GET
    @Path("/icon/{pushType}")
    @Produces(InetMediaType.IMAGE_PNG_VALUE)
    public Response getEnabledIcon(@PathParam("pushType") PushType pushType) throws Exception {

        Plugin plugin = pluginManager.getPlugin(pushType);
        byte[] bytes = plugin.getIcon(domainProfile);

        return Response.ok(bytes, InetMediaType.IMAGE_PNG_VALUE).build();
    }

    @GET
    @Path("/requests")
    @Produces(MediaType.TEXT_HTML)
    public Thymeleaf viewEvents() throws Exception {
        QueryResult<PushRequest> queryResult = pushRequestStore.getByClient(domainProfile, 500);
        List<PushRequest> requests = new ArrayList<>(queryResult.getEntityList());

        Collections.sort(requests);
        Collections.reverse(requests);

        DomainRequestsModel model = new DomainRequestsModel(getAccount(), domainProfile, requests);
        return new Thymeleaf(executionManager.getContext().getSession(), ThymeleafViewFactory.MANAGE_API_REQUESTS, model);
    }

    @POST
    @Path("/requests/delete-all")
    public Response deleteEvents() throws Exception {
        QueryResult<PushRequest> queryResult = pushRequestStore.getByClient(domainProfile, 500);
        List<PushRequest> requests = new ArrayList<>(queryResult.getEntityList());

        for (PushRequest request : requests) {
            pushRequestStore.delete(request);
        }

        executionManager.getContext().setLastMessage("All API Requests deleted");
        domainStore.update(domainProfile);

        URI uri = executionManager.getContext().getUriInfo().getBaseUriBuilder().path("manage").path("domain").path(domainProfile.getDomainKey()).path("requests").build();
        return Response.seeOther(uri).build();
    }

    @Path("/emails")
    public ManageEmailsResource getManageEmailsResource() throws Exception {
        return new ManageEmailsResource(executionManager, pushRequestStore, pluginManager, getAccount(), domainProfile);
    }

    @POST
    public Response updateClient(@FormParam("domainKey") String domainKey, @FormParam("domainPassword") String domainPassword, @FormParam("retentionDays") int retentionDays) throws Exception {
        if (domainProfile.getDomainKey().equals(domainKey) == false && domainStore.getByDomainName(domainKey) != null) {
            // The specified name is not the same as the current value but it is already in use by another account.
            String msg = String.format("The client name %s already exists.", domainKey);
            throw ApiException.badRequest(msg);
        }

        UpdateDomainAction action = new UpdateDomainAction(domainKey, domainPassword, retentionDays);
        executionManager.getContext().setLastMessage("Domain configuration changed.");

        domainProfile.apply(action);
        domainStore.update(domainProfile);

        URI uri = executionManager.getContext().getUriInfo().getBaseUriBuilder().path("manage").path("domain").path(domainProfile.getDomainKey()).build();
        return Response.seeOther(uri).build();
    }

    @POST
    @Path("/delete")
    public Response deleteClient() throws Exception {
        domainStore.delete(domainProfile);
        return Response.seeOther(new URI("manage/account")).build();
    }

    @Path("/{pushType}")
    public ManagePluginApi getManagePluginApi(@PathParam("pushType") PushType pushType) throws Exception {
        return new ManagePluginApi(executionManager, pluginManager, domainProfile, pushType);
    }
}
