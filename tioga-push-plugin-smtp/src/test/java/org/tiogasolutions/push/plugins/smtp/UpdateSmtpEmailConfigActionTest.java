/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.plugins.smtp;

import org.tiogasolutions.push.kernel.accounts.Account;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.config.SmtpAuthType;
import org.tiogasolutions.push.test.TestFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test
public class UpdateSmtpEmailConfigActionTest {

  private TestFactory testFactory;

  @BeforeClass
  public void beforeClass() throws Exception {
    testFactory = new TestFactory(1);
  }

  public void testUpdate() throws Exception {

    Account account = testFactory.createAccount();
    DomainProfileEntity domain = testFactory.createDomain(account);

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
