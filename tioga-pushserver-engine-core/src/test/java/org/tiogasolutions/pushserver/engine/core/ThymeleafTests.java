package org.tiogasolutions.pushserver.engine.core;

import org.tiogasolutions.pushserver.engine.core.resources.RootResource;
import org.tiogasolutions.pushserver.engine.core.resources.manage.account.ManageAccountModel;
import org.tiogasolutions.pushserver.engine.core.resources.manage.client.DomainRequestsModel;
import org.tiogasolutions.pushserver.engine.core.resources.manage.client.emails.EmailModel;
import org.tiogasolutions.pushserver.engine.core.resources.manage.client.emails.EmailsModel;
import org.tiogasolutions.pushserver.engine.core.resources.manage.client.notifications.DomainNotificationModel;
import org.tiogasolutions.pushserver.engine.core.view.Thymeleaf;
import org.tiogasolutions.pushserver.engine.core.view.ThymeleafMessageBodyWriter;
import org.tiogasolutions.pushserver.engine.core.view.ThymeleafViewFactory;
import org.tiogasolutions.pushserver.engine.core.resources.manage.client.ManageDomainModel;
import org.tiogasolutions.pushserver.common.accounts.Account;
import org.tiogasolutions.pushserver.common.clients.Domain;
import org.tiogasolutions.pushserver.common.plugins.PluginContext;
import org.tiogasolutions.pushserver.common.requests.PushRequest;
import org.tiogasolutions.pushserver.pub.push.EmailPush;
import org.tiogasolutions.pushserver.pub.push.LqNotificationPush;
import org.tiogasolutions.pushserver.test.TestFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertNotNull;

@Test
public class ThymeleafTests {

  private TestFactory testFactory;
  private StringWriter writer;
  private ThymeleafMessageBodyWriter msgBodyWriter;

  @BeforeClass
  public void beforeClass() throws Exception {
    testFactory = new TestFactory();
    msgBodyWriter = new ThymeleafMessageBodyWriter(null) {
      @Override public String getBaseUri() { return "http://example.com/unit-tests/"; }
    };
  }

  @BeforeMethod
  public void beforeMethod() {
    writer = new StringWriter();
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
    Domain domain = testFactory.createDomain(account);
    List<PushRequest> requests = testFactory.createPushRequests(domain);
    DomainRequestsModel model = new DomainRequestsModel(account, domain, requests);

    Thymeleaf leaf = new Thymeleaf(testFactory.createSession(), ThymeleafViewFactory.MANAGE_API_REQUESTS, model);
    msgBodyWriter.writeTo(leaf, writer);
    String content = writer.toString();
    assertNotNull(content);
  }

  public void testManageDomain() throws Exception {

    Account account = testFactory.createAccount();
    Domain domain = testFactory.createDomain(account);
    PluginContext pluginContext = testFactory.pluginContext(testFactory);
    ManageDomainModel model = new ManageDomainModel(pluginContext, account, domain, "This was the last message.");

    Thymeleaf leaf = new Thymeleaf(testFactory.createSession(), ThymeleafViewFactory.MANAGE_API_CLIENT, model);
    msgBodyWriter.writeTo(leaf, writer);
    String content = writer.toString();
    assertNotNull(content);
  }

  public void testManageAccount() throws Exception {

    Account account = testFactory.createAccount();
    Domain domain = testFactory.createDomain(account);
    PluginContext pluginContext = testFactory.pluginContext(testFactory);
    ManageAccountModel model = new ManageAccountModel(pluginContext, account, Arrays.asList(domain));

    Thymeleaf leaf = new Thymeleaf(testFactory.createSession(), ThymeleafViewFactory.MANAGE_ACCOUNT, model);
    msgBodyWriter.writeTo(leaf, writer);
    String content = writer.toString();
    assertNotNull(content);
  }

  public void testManageApiEmails() throws Exception {

    Account account = testFactory.createAccount();
    Domain domain = testFactory.createDomain(account);
    List<PushRequest> requests = testFactory.createPushRequests_Emails(domain);
    EmailsModel model = new EmailsModel(account, domain, requests);

    Thymeleaf leaf = new Thymeleaf(testFactory.createSession(), ThymeleafViewFactory.MANAGE_API_EMAILS, model);
    msgBodyWriter.writeTo(leaf, writer);
    String content = writer.toString();
    assertNotNull(content);
  }

  public void testManageApiEmail() throws Exception {

    Account account = testFactory.createAccount();
    Domain domain = testFactory.createDomain(account);
    PushRequest request = testFactory.createPushRequests_Emails(domain).get(0);
    EmailPush email = request.getEmailPush();
    EmailModel model = new EmailModel(account, domain, request, email);

    Thymeleaf leaf = new Thymeleaf(testFactory.createSession(), ThymeleafViewFactory.MANAGE_API_EMAIL, model);
    msgBodyWriter.writeTo(leaf, writer);
    String content = writer.toString();
    assertNotNull(content);
  }

  public void testManageApiNotification() throws Exception {

    Account account = testFactory.createAccount();
    Domain domain = testFactory.createDomain(account);
    PushRequest request = testFactory.createPushRequests_Notifications(domain).get(0);
    LqNotificationPush notification = request.getNotificationPush();
    DomainNotificationModel model = new DomainNotificationModel(account, domain, request, notification);

    Thymeleaf leaf = new Thymeleaf(testFactory.createSession(), ThymeleafViewFactory.MANAGE_API_NOTIFICATION, model);
    msgBodyWriter.writeTo(leaf, writer);
    String content = writer.toString();
    assertNotNull(content);
  }

  public void testManageApiNotifications() throws Exception {

    Account account = testFactory.createAccount();
    Domain domain = testFactory.createDomain(account);
    List<PushRequest> requests = testFactory.createPushRequests_Notifications(domain);
    DomainRequestsModel model = new DomainRequestsModel(account, domain, requests);

    Thymeleaf leaf = new Thymeleaf(testFactory.createSession(), ThymeleafViewFactory.MANAGE_API_NOTIFICATIONS, model);
    msgBodyWriter.writeTo(leaf, writer);
    String content = writer.toString();
    assertNotNull(content);
  }
}
