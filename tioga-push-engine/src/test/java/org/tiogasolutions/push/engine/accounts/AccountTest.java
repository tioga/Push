/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.engine.accounts;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;
import org.tiogasolutions.push.kernel.accounts.Account;
import org.tiogasolutions.push.kernel.accounts.AccountStore;
import org.tiogasolutions.push.kernel.accounts.actions.CreateAccountAction;
import org.tiogasolutions.push.test.AbstractSpringTest;
import org.tiogasolutions.push.test.TestFixture;

import static org.testng.Assert.assertNotNull;

@Test
public class AccountTest extends AbstractSpringTest {

  @Autowired
  private AccountStore accountStore;

  public void testCreate() {

    CreateAccountAction createAccountAction = new CreateAccountAction(
      TestFixture.westCoastTimeZone,
      "Test Parr <test@jacobparr.com>",
      "Unit", "Test",
      "testing123", "testing123"
    );

    Account account = new Account(createAccountAction);
    assertNotNull(account);

    accountStore.create(account);
  }
}
