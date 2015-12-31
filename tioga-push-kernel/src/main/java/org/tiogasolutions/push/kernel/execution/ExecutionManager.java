package org.tiogasolutions.push.kernel.execution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tiogasolutions.couchace.core.api.CouchServer;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.push.kernel.config.CouchServersConfig;

import javax.ws.rs.core.UriInfo;

@Component
public class ExecutionManager {

  private final InheritableThreadLocal<ExecutionContext> threadLocal;

  private final CouchServer couchServer;
  private final CouchServersConfig couchServersConfig;

  @Autowired
  public ExecutionManager(CouchServersConfig couchServersConfig, CouchServer couchServer) {
    this.couchServer = couchServer;
    this.couchServersConfig = couchServersConfig;
    this.threadLocal = new InheritableThreadLocal<>();
  }

  public void removeExecutionContext() {
    threadLocal.remove();
  }

  public ExecutionContext newContext(UriInfo uriInfo) {
    ExecutionContext context = new ExecutionContext(uriInfo);
    assignContext(context);
    return context;
  }

  public void assignContext(ExecutionContext context) {
    threadLocal.set(context);
  }

  public boolean hasContext() {
    ExecutionContext executionContext = threadLocal.get();
    return executionContext != null;
  }

  // TODO - why is this not getContext()?
  public ExecutionContext context() {
    ExecutionContext context = threadLocal.get();
    if (context == null) {
      throw ApiException.internalServerError("There is no current execution context for this thread.");
    } else {
      return context;
    }
  }

  public CouchServersConfig getCouchServersConfig() {
    return couchServersConfig;
  }

  public CouchServer getCouchServer() {
    return couchServer;
  }
}
