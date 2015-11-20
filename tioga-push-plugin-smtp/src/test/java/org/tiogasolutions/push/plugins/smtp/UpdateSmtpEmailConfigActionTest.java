/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.plugins.smtp;

import org.springframework.beans.factory.annotation.Autowired;
import org.tiogasolutions.push.kernel.accounts.Account;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.config.SmtpAuthType;
import org.testng.annotations.Test;
import org.tiogasolutions.push.test.AbstractSpringTest;
import org.tiogasolutions.push.test.TestFixture;

import static org.testng.Assert.assertEquals;

@Test
public class UpdateSmtpEmailConfigActionTest extends AbstractSpringTest {

  @Autowired
  TestFixture testFixture;

  public UpdateSmtpEmailConfigActionTest() {
    super(SmtpSpringTestConfig.class);
  }

  public void testUpdate() throws Exception {

    Account account = testFixture.createAccount();
    DomainProfileEntity domain = testFixture.createDomain(account);

    UpdateSmtpEmailConfigAction updateAction = new UpdateSmtpEmailConfigAction(
      domain,
      "mickey.mouse", "IamMickey",
      SmtpAuthType.ssl, "google.com", "99",
      "to@example.com", "from@example.com", "override@example.com");


    SmtpEmailConfig config = new SmtpEmailConfig();
    config.apply(updateAction);

    assertEquals(config.getDomainId(), domain.getDomainId());

    assertEquals(config.getUserName(), "mickey.mouse");
    assertEquals(config.getPassword(), "IamMickey");

    assertEquals(config.getPortNumber(),  "99");
    assertEquals(config.getAuthType(),    SmtpAuthType.ssl);
    assertEquals(config.getServerName(),  "google.com");

    assertEquals(config.getTestToAddress(),     "to@example.com");
    assertEquals(config.getTestFromAddress(),   "from@example.com");
    assertEquals(config.getRecipientOverride(), "override@example.com");
  }
}
