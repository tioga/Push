package org.tiogasolutions.push.kernel.config;

public class CouchServersConfig {

  private String masterUrl;
  private String masterUserName;
  private String masterPassword;
  private String masterDatabaseName;

  private String domainUrl;
  private String domainUserName;
  private String domainPassword;
  private String domainDatabasePrefix;

  public CouchServersConfig() {
  }

  public String getMasterUrl() {
    return masterUrl;
  }

  public void setMasterUrl(String masterUrl) {
    this.masterUrl = masterUrl;
  }

  public String getMasterUserName() {
    return masterUserName;
  }

  public void setMasterUserName(String masterUserName) {
    this.masterUserName = masterUserName;
  }

  public String getMasterPassword() {
    return masterPassword;
  }

  public void setMasterPassword(String masterPassword) {
    this.masterPassword = masterPassword;
  }

  public String getMasterDatabaseName() {
    return masterDatabaseName;
  }

  public void setMasterDatabaseName(String masterDatabaseName) {
    this.masterDatabaseName = masterDatabaseName;
  }

  public String getDomainUrl() {
    return domainUrl;
  }

  public void setDomainUrl(String domainUrl) {
    this.domainUrl = domainUrl;
  }

  public String getDomainUserName() {
    return domainUserName;
  }

  public void setDomainUserName(String domainUserName) {
    this.domainUserName = domainUserName;
  }

  public String getDomainPassword() {
    return domainPassword;
  }

  public void setDomainPassword(String domainPassword) {
    this.domainPassword = domainPassword;
  }

  public String getDomainDatabasePrefix() {
    return domainDatabasePrefix;
  }

  public void setDomainDatabasePrefix(String domainDatabasePrefix) {
    this.domainDatabasePrefix = domainDatabasePrefix;
  }
}
