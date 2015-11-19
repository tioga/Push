/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.engine.core.accounts;

import org.tiogasolutions.push.kernel.accounts.Account;
import org.tiogasolutions.push.kernel.accounts.AccountStore;
import org.tiogasolutions.push.kernel.accounts.actions.CreateAccountAction;
import org.tiogasolutions.push.test.TestFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

@Test
public class AccountTest {

  private TestFactory testFactory;
  private AccountStore accountStore;

  @BeforeClass
  public void beforeClass() throws Exception {
    testFactory = new TestFactory(4);
    accountStore = testFactory.getAccountStore();
  }

  public void testCreate() {

    CreateAccountAction createAccountAction = new CreateAccountAction(
      TestFactory.westCoastTimeZone,
      "Test Parr <test@jacobparr.com>",
      "Unit", "Test",
      "testing123", "testing123"
    );

    Account account = new Account(createAccountAction);
    assertNotNull(account);

    accountStore.create(account);
  }
}
