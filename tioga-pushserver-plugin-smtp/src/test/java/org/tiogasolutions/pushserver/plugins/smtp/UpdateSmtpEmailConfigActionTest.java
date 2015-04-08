/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.pushserver.plugins.smtp;

import org.tiogasolutions.pushserver.common.accounts.Account;
import org.tiogasolutions.pushserver.common.clients.Domain;
import org.tiogasolutions.pushserver.common.config.SmtpAuthType;
import org.tiogasolutions.pushserver.test.TestFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test
public class UpdateSmtpEmailConfigActionTest {

  private TestFactory testFactory;

  @BeforeClass
  public void beforeClass() throws Exception {
    testFactory = TestFactory.get();
  }

  public void testUpdate() throws Exception {

    Account account = testFactory.createAccount();
    Domain domain = testFactory.createDomain(account);

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
