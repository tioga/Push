/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.pushserver.common.accounts.actions;

import java.net.URL;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.pushserver.pub.internal.RequestErrors;
import org.tiogasolutions.pushserver.pub.internal.ValidationUtils;

public class ResetPasswordAction extends AccountAction {

  private final URL templateUrl;

  public ResetPasswordAction(URL templateUrl) {
    this.templateUrl = templateUrl;
  }

  public URL getTemplateUrl() {
    return templateUrl;
  }

  @Override
  public RequestErrors validate(RequestErrors errors) throws ApiException {
    ValidationUtils.requireValue(errors, templateUrl, "The template URL must be specified.");
    return errors;
  }
}
