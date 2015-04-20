package org.tiogasolutions.push.engine.core.resources.manage.client.emails;

import org.tiogasolutions.push.common.accounts.Account;
import org.tiogasolutions.push.common.clients.Domain;
import org.tiogasolutions.push.common.requests.PushRequest;

import java.util.*;

public class EmailsModel {
  private final Account account;
  private final Domain domain;
  private final List<PushRequest> requests = new ArrayList<>();

  public EmailsModel(Account account, Domain domain, Collection<PushRequest> requests) {

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

  public Domain getDomain() {
    return domain;
  }

  public List<PushRequest> getRequests() {
    return requests;
  }
}
