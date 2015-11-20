/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.plugins.ses;

import org.springframework.beans.factory.annotation.Autowired;
import org.tiogasolutions.push.kernel.accounts.Account;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.testng.annotations.Test;
import org.tiogasolutions.push.test.AbstractSpringTest;
import org.tiogasolutions.push.test.TestFixture;

import static org.testng.Assert.assertEquals;

@Test
public class UpdateSesEmailConfigActionTest extends AbstractSpringTest {

  @Autowired
  private TestFixture testFixture;

  public void testUpdate() throws Exception {

    Account account = testFixture.createAccount();
    DomainProfileEntity domain = testFixture.createDomain(account);

    UpdateSesEmailConfigAction updateAction = new UpdateSesEmailConfigAction(domain,
      "some-access-key-id", "some-secret-key", "email.us-west-2.amazonaws.com",
      "to@example.com", "from@example.com", "override@example.com");

    SesEmailConfig config = new SesEmailConfig();
    config.apply(updateAction);

    assertEquals(config.getDomainId(), domain.getDomainId());

    assertEquals(config.getAccessKeyId(), "some-access-key-id");
    assertEquals(config.getSecretKey(),   "some-secret-key");

    assertEquals(config.getTestToAddress(),     "to@example.com");
    assertEquals(config.getTestFromAddress(),   "from@example.com");
    assertEquals(config.getRecipientOverride(), "override@example.com");
  }
}
