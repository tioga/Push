package org.tiogasolutions.pushserver.engine.core.resources.manage.client.emails;

import org.tiogasolutions.pushserver.common.accounts.Account;
import org.tiogasolutions.pushserver.common.clients.Domain;
import org.tiogasolutions.pushserver.common.requests.PushRequest;

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
