/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.engine.core.resources.manage.account;

import org.tiogasolutions.push.common.accounts.actions.ChangePasswordAction;
import org.tiogasolutions.push.common.system.ExecutionContext;
import org.tiogasolutions.push.engine.core.jaxrs.security.MngtAuthentication;
import org.tiogasolutions.push.engine.core.system.CpApplication;
import org.tiogasolutions.push.engine.core.view.Thymeleaf;
import org.tiogasolutions.push.engine.core.view.ThymeleafViewFactory;
import org.tiogasolutions.push.common.accounts.Account;
import org.tiogasolutions.push.common.accounts.actions.UpdateAccountAction;
import org.tiogasolutions.push.common.clients.Domain;
import org.tiogasolutions.dev.common.EqualsUtils;
import org.tiogasolutions.dev.common.exceptions.ApiException;

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

  private final Account account;

  private final ExecutionContext execContext = CpApplication.getExecutionContext();

  public ManageAccountResource(Account account) {
    this.account = account;
  }

  @GET
  public Thymeleaf viewAccount() throws IOException {
    ExecutionContext execContext = CpApplication.getExecutionContext();
    Account account = execContext.getAccount();
    List<Domain> domains = execContext.getDomainStore().getDomains(account);

    ManageAccountModel model = new ManageAccountModel(execContext, account, domains);
    return new Thymeleaf(execContext.getSession(), ThymeleafViewFactory.MANAGE_ACCOUNT, model);
  }

  @POST
  @Path("/update")
  public Response updateAccount(@FormParam("firstName") String firstName, @FormParam("lastName") String lastName, @FormParam("emailAddress") String newEmailAddress) throws Exception {

    String oldEmailAddress = account.getEmailAddress();
    if (EqualsUtils.objectsNotEqual(oldEmailAddress, newEmailAddress)) {
      // They are changing the emails address.
      if (execContext.getAccountStore().getByEmail(newEmailAddress) != null) {
        String msg = String.format("The email address %s is already in use.", newEmailAddress);
        throw ApiException.conflict(msg);
      }
    }

    UpdateAccountAction action = new UpdateAccountAction(firstName, lastName, newEmailAddress);
    account.apply(action);
    execContext.getAccountStore().update(account);

    if (EqualsUtils.objectsNotEqual(oldEmailAddress, newEmailAddress)) {
      // The email address has changed - we will need to update the session
      execContext.getSessionStore().newSession(newEmailAddress);
    }

    execContext.setLastMessage("You account details have been updated.");
    return Response.seeOther(new URI("manage/account")).build();
  }

  @POST
  @Path("/password")
  public Response changePassword(@FormParam("oldPassword") String oldPassword, @FormParam("newPassword") String newPassword, @FormParam("confirmed") String confirmed) throws Exception {

    ChangePasswordAction action = new ChangePasswordAction(oldPassword, newPassword, confirmed);
    account.apply(action);
    execContext.getAccountStore().update(account);

    execContext.setLastMessage("You password has been updated.");
    return Response.seeOther(new URI("manage/account")).build();
  }
}
