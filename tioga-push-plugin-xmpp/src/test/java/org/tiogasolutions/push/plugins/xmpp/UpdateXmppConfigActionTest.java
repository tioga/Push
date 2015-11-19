/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.plugins.xmpp;

import org.tiogasolutions.push.kernel.accounts.Account;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.test.TestFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test
public class UpdateXmppConfigActionTest {

  private TestFactory testFactory;

  @BeforeClass
  public void beforeClass() throws Exception {
    testFactory = new TestFactory(1);
  }

  public void testUpdate() throws Exception {

    Account account = testFactory.createAccount();
    DomainProfileEntity domain = testFactory.createDomain(account);

    UpdateXmppConfigAction updateAction = new UpdateXmppConfigAction(domain,
        "mickey.mouse", "IamMickey",
        "talk.google.com", "5222", "gmail.com",
        "test@example.com", "override@example.com");

    XmppConfig config = new XmppConfig();
    config.apply(updateAction);

    assertEquals(config.getDomainId(), domain.getDomainId());

    assertEquals(config.getUsername(), "mickey.mouse");
    assertEquals(config.getPassword(), "IamMickey");

    assertEquals(config.getTestAddress(), "test@example.com");
    assertEquals(config.getRecipientOverride(), "override@example.com");
  }
}
