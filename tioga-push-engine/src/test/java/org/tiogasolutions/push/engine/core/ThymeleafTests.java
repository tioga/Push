package org.tiogasolutions.push.engine.core;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tiogasolutions.push.engine.core.resources.RootResource;
import org.tiogasolutions.push.engine.core.resources.manage.account.ManageAccountModel;
import org.tiogasolutions.push.engine.core.resources.manage.client.DomainRequestsModel;
import org.tiogasolutions.push.engine.core.resources.manage.client.ManageDomainModel;
import org.tiogasolutions.push.engine.core.resources.manage.client.emails.EmailModel;
import org.tiogasolutions.push.engine.core.resources.manage.client.emails.EmailsModel;
import org.tiogasolutions.push.engine.core.view.Thymeleaf;
import org.tiogasolutions.push.engine.core.view.ThymeleafMessageBodyWriter;
import org.tiogasolutions.push.engine.core.view.ThymeleafViewFactory;
import org.tiogasolutions.push.kernel.accounts.Account;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.execution.ExecutionContext;
import org.tiogasolutions.push.kernel.requests.PushRequest;
import org.tiogasolutions.push.pub.common.CommonEmail;
import org.tiogasolutions.push.test.TestFactory;

import java.io.StringWriter;
import java.util.List;

import static org.testng.Assert.assertNotNull;

@Test
public class ThymeleafTests {

  private TestFactory testFactory = new TestFactory(4);
  private StringWriter writer;
  private ThymeleafMessageBodyWriter msgBodyWriter;

  @BeforeMethod
  public void beforeMethod() throws Exception {
    msgBodyWriter = new ThymeleafMessageBodyWriter() {
      @Override public String getBaseUri() { return "http://example.com/unit-tests/"; }
    };

    ExecutionContext executionContext = testFactory.getExecutionManager().newContext(null);
    DomainProfileEntity domain = testFactory.createDomain(testFactory.createAccount());
    executionContext.setDomain(domain);

    writer = new StringWriter();
  }

  @AfterMethod
  public void afterMethod() {
    testFactory.getExecutionManager().removeExecutionContext();
  }

  public void testWelcome() throws Exception {
    RootResource.WelcomeModel model = new RootResource.WelcomeModel(null, "This is a test", "some-username", "some-password");
    Thymeleaf leaf = new Thymeleaf(testFactory.createSession(), ThymeleafViewFactory.WELCOME, model);
    msgBodyWriter.writeTo(leaf, writer);
    String content = writer.toString();
    assertNotNull(content);
  }

  public void testManagePushRequests() throws Exception {

    Account account = testFactory.createAccount();
    DomainProfileEntity domain = testFactory.createDomain(account);
    List<PushRequest> requests = testFactory.createPushRequests(domain);
    DomainRequestsModel model = new DomainRequestsModel(account, domain, requests);

    Thymeleaf leaf = new Thymeleaf(testFactory.createSession(), ThymeleafViewFactory.MANAGE_API_REQUESTS, model);
    msgBodyWriter.writeTo(leaf, writer);
    String content = writer.toString();
    assertNotNull(content);
  }

  public void testManageDomain() throws Exception {
    DomainProfileEntity domainProfile = testFactory.getExecutionManager().context().getDomain();
    ManageDomainModel model = new ManageDomainModel(testFactory.getExecutionManager(), domainProfile, testFactory.getPluginManager(), "This was the last message.");
    Thymeleaf leaf = new Thymeleaf(testFactory.createSession(), ThymeleafViewFactory.MANAGE_API_CLIENT, model);
    msgBodyWriter.writeTo(leaf, writer);
    String content = writer.toString();
    assertNotNull(content);
  }

  public void testManageAccount() throws Exception {

    Account account = testFactory.createAccount();
    DomainProfileEntity domainProfile = testFactory.createDomain(account);
    ManageAccountModel model = new ManageAccountModel(testFactory.getPluginManager(), account, domainProfile);

    Thymeleaf leaf = new Thymeleaf(testFactory.createSession(), ThymeleafViewFactory.MANAGE_ACCOUNT, model);
    msgBodyWriter.writeTo(leaf, writer);
    String content = writer.toString();
    assertNotNull(content);
  }

  public void testManageApiEmails() throws Exception {

    Account account = testFactory.createAccount();
    DomainProfileEntity domain = testFactory.createDomain(account);
    List<PushRequest> requests = testFactory.createPushRequests_Emails(domain);
    EmailsModel model = new EmailsModel(account, domain, requests);

    Thymeleaf leaf = new Thymeleaf(testFactory.createSession(), ThymeleafViewFactory.MANAGE_API_EMAILS, model);
    msgBodyWriter.writeTo(leaf, writer);
    String content = writer.toString();
    assertNotNull(content);
  }

  public void testManageApiEmail() throws Exception {

    Account account = testFactory.createAccount();
    DomainProfileEntity domain = testFactory.createDomain(account);
    PushRequest request = testFactory.createPushRequests_Emails(domain).get(0);
    CommonEmail email = request.getCommonEmail();
    EmailModel model = new EmailModel(account, domain, request, email);

    Thymeleaf leaf = new Thymeleaf(testFactory.createSession(), ThymeleafViewFactory.MANAGE_API_EMAIL, model);
    msgBodyWriter.writeTo(leaf, writer);
    String content = writer.toString();
    assertNotNull(content);
  }
}
