/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.common.accounts.actions;

import org.tiogasolutions.dev.common.StringUtils;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.push.pub.internal.RequestErrors;
import org.tiogasolutions.push.pub.internal.ValidationUtils;

public class UpdateAccountAction extends AccountAction {

  private final String emailAddress;

  private final String firstName;
  private final String lastName;

  public UpdateAccountAction(String firstName, String lastName, String emailAddress) {
    this.emailAddress = StringUtils.emptyToNull(emailAddress);
    this.firstName = StringUtils.emptyToNull(firstName);
    this.lastName = StringUtils.emptyToNull(lastName);
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  @Override
  public RequestErrors validate(RequestErrors errors) throws ApiException {
    ValidationUtils.requireValue(errors, firstName, "Your first name must be specified.");
    ValidationUtils.requireValue(errors, lastName, "Your last name must be specified.");
    ValidationUtils.requireValue(errors, emailAddress, "Your email address must be specified.");
    return errors;
  }
}
