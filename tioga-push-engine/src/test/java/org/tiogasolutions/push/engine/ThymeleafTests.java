package org.tiogasolutions.push.engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tiogasolutions.push.engine.resources.RootResource;
import org.tiogasolutions.push.engine.resources.manage.account.ManageAccountModel;
import org.tiogasolutions.push.engine.resources.manage.client.DomainRequestsModel;
import org.tiogasolutions.push.engine.resources.manage.client.ManageDomainModel;
import org.tiogasolutions.push.engine.resources.manage.client.emails.EmailModel;
import org.tiogasolutions.push.engine.resources.manage.client.emails.EmailsModel;
import org.tiogasolutions.push.engine.view.Thymeleaf;
import org.tiogasolutions.push.engine.view.ThymeleafMessageBodyWriter;
import org.tiogasolutions.push.engine.view.ThymeleafViewFactory;
import org.tiogasolutions.push.kernel.accounts.Account;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.execution.ExecutionContext;
import org.tiogasolutions.push.kernel.execution.ExecutionManager;
import org.tiogasolutions.push.kernel.requests.PushRequest;
import org.tiogasolutions.push.kernel.system.PluginManager;
import org.tiogasolutions.push.pub.common.CommonEmail;
import org.tiogasolutions.push.test.AbstractSpringTest;
import org.tiogasolutions.push.test.TestFixture;

import java.io.StringWriter;
import java.util.List;

import static org.testng.Assert.assertNotNull;

@Test
public class ThymeleafTests extends AbstractSpringTest {

  @Autowired
  private TestFixture testFactory;

  @Autowired
  private ExecutionManager executionManager;

  @Autowired
  private PluginManager pluginManager;

  private StringWriter writer;
  private ThymeleafMessageBodyWriter msgBodyWriter;

  @BeforeMethod
  public void beforeMethod() throws Exception {
    msgBodyWriter = new ThymeleafMessageBodyWriter() {
      @Override public String getBaseUri() { return "http://example.com/unit-tests/"; }
    };

    ExecutionContext executionContext = executionManager.newContext(null);
    DomainProfileEntity domain = testFactory.createDomain(testFactory.createAccount());
    executionContext.setDomain(domain);

    writer = new StringWriter();
  }

  @AfterMethod
  public void afterMethod() {
    executionManager.removeExecutionContext();
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
    DomainProfileEntity domainProfile = executionManager.context().getDomain();
    ManageDomainModel model = new ManageDomainModel(executionManager, domainProfile, pluginManager, "This was the last message.");
    Thymeleaf leaf = new Thymeleaf(testFactory.createSession(), ThymeleafViewFactory.MANAGE_API_CLIENT, model);
    msgBodyWriter.writeTo(leaf, writer);
    String content = writer.toString();
    assertNotNull(content);
  }

  public void testManageAccount() throws Exception {

    Account account = testFactory.createAccount();
    DomainProfileEntity domainProfile = testFactory.createDomain(account);
    ManageAccountModel model = new ManageAccountModel(executionManager, pluginManager, account, domainProfile);

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
