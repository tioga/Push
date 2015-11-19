/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */
package org.tiogasolutions.push.kernel.actions;

import org.tiogasolutions.push.pub.internal.RequestErrors;
import org.tiogasolutions.push.pub.internal.ValidatableAction;
import org.tiogasolutions.push.pub.internal.ValidationUtils;

public class UpdateDomainAction implements ValidatableAction {

  private final String domainKey;
  private final String domainPassword;
  private final int retentionDays;

  public UpdateDomainAction(String domainKey, String domainPassword, int retentionDays) {
    this.domainKey = domainKey;
    this.domainPassword = domainPassword;
    this.retentionDays = (retentionDays < 0) ? 0 : retentionDays;
  }

  public String getDomainKey() {
    return domainKey;
  }

  public String getDomainPassword() {
    return domainPassword;
  }

  public int getRetentionDays() {
    return retentionDays;
  }

  @Override
  public RequestErrors validate(RequestErrors errors) {
    ValidationUtils.validateUserName(errors, domainKey, "domain's key");
    ValidationUtils.validatePassword(errors, domainPassword, "domain's password");
    return errors;
  }
}
