package org.tiogasolutions.push.engine.resources.manage.client.emails;

import org.tiogasolutions.push.kernel.accounts.Account;
import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.tiogasolutions.push.kernel.requests.PushRequest;

import java.util.*;

public class EmailsModel {
  private final Account account;
  private final DomainProfileEntity domain;
  private final List<PushRequest> requests = new ArrayList<>();

  public EmailsModel(Account account, DomainProfileEntity domain, Collection<PushRequest> requests) {

    this.account = account;
    this.domain = domain;

    Set<PushRequest> sortedSet = new TreeSet<>(requests);
    List<PushRequest> sortedList = new ArrayList<>(sortedSet);
    Collections.reverse(sortedList);
    this.requests.addAll(sortedList);
  }

  public Account getAccount() {
    return account;
  }

  public DomainProfileEntity getDomain() {
    return domain;
  }

  public List<PushRequest> getRequests() {
    return requests;
  }
}
