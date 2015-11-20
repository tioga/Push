/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.plugins.xmpp;

import org.springframework.beans.factory.annotation.Autowired;
import org.tiogasolutions.push.kernel.accounts.Account;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.testng.annotations.Test;
import org.tiogasolutions.push.test.AbstractSpringTest;
import org.tiogasolutions.push.test.TestFixture;

import static org.testng.Assert.assertEquals;

@Test
public class UpdateXmppConfigActionTest extends AbstractSpringTest {

  @Autowired
  TestFixture testFixture;

  public UpdateXmppConfigActionTest() {
    super(XmppSpringTestConfig.class);
  }

  public void testUpdate() throws Exception {

    Account account = testFixture.createAccount();
    DomainProfileEntity domain = testFixture.createDomain(account);

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
