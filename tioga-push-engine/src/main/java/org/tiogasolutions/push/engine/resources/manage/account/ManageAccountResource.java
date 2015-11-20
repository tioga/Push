/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.engine.resources.manage.account;

import org.tiogasolutions.dev.common.EqualsUtils;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.push.engine.jaxrs.security.MngtAuthentication;
import org.tiogasolutions.push.engine.view.Thymeleaf;
import org.tiogasolutions.push.engine.view.ThymeleafViewFactory;
import org.tiogasolutions.push.kernel.accounts.Account;
import org.tiogasolutions.push.kernel.accounts.AccountStore;
import org.tiogasolutions.push.kernel.accounts.DomainStore;
import org.tiogasolutions.push.kernel.accounts.actions.ChangePasswordAction;
import org.tiogasolutions.push.kernel.accounts.actions.UpdateAccountAction;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.execution.ExecutionContext;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.system.PluginManager;
import org.tiogasolutions.push.kernel.system.SessionStore;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.util.List;

@MngtAuthentication
public class ManageAccountResource {

  private final SessionStore sessionStore;
  private final PluginManager pluginManager;
  private final ExecutionManager executionManager;

  private final DomainStore domainStore;
  private final AccountStore accountStore;

  public ManageAccountResource(ExecutionManager executionManager, DomainStore domainStore, AccountStore accountStore, PluginManager pluginManager, SessionStore sessionStore) {
    this.sessionStore = sessionStore;
    this.pluginManager = pluginManager;
    this.executionManager = executionManager;
    this.domainStore = domainStore;
    this.accountStore = accountStore;
  }

  @GET
  public Thymeleaf viewAccount() throws IOException {
    ExecutionContext execContext = executionManager.context();
    Account account = execContext.getAccount();
    List<DomainProfileEntity> domains = domainStore.getDomains(account);

    ManageAccountModel model = new ManageAccountModel(executionManager, pluginManager, account, domains);
    return new Thymeleaf(execContext.getSession(), ThymeleafViewFactory.MANAGE_ACCOUNT, model);
  }

  @POST
  @Path("/update")
  public Response updateAccount(@FormParam("firstName") String firstName, @FormParam("lastName") String lastName, @FormParam("emailAddress") String newEmailAddress) throws Exception {

    Account account = executionManager.context().getAccount();

    String oldEmailAddress = account.getEmailAddress();
    if (EqualsUtils.objectsNotEqual(oldEmailAddress, newEmailAddress)) {
      // They are changing the emails address.
      if (accountStore.getByEmail(newEmailAddress) != null) {
        String msg = String.format("The email address %s is already in use.", newEmailAddress);
        throw ApiException.conflict(msg);
      }
    }

    UpdateAccountAction action = new UpdateAccountAction(firstName, lastName, newEmailAddress);
    account.apply(action);
    accountStore.update(account);

    if (EqualsUtils.objectsNotEqual(oldEmailAddress, newEmailAddress)) {
      // The email address has changed - we will need to update the session
      sessionStore.newSession(newEmailAddress);
    }

    executionManager.context().setLastMessage("You account details have been updated.");
    return Response.seeOther(new URI("manage/account")).build();
  }

  @POST
  @Path("/password")
  public Response changePassword(@FormParam("oldPassword") String oldPassword, @FormParam("newPassword") String newPassword, @FormParam("confirmed") String confirmed) throws Exception {
    Account account = executionManager.context().getAccount();

    ChangePasswordAction action = new ChangePasswordAction(oldPassword, newPassword, confirmed);
    account.apply(action);
    accountStore.update(account);

    executionManager.context().setLastMessage("You password has been updated.");
    return Response.seeOther(new URI("manage/account")).build();
  }
}
