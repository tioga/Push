package org.tiogasolutions.push.kernel.execution;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Component;
import org.tiogasolutions.couchace.core.api.CouchServer;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.push.kernel.config.CouchServersConfig;

import javax.ws.rs.core.UriInfo;

@Component
public class ExecutionManager implements BeanFactoryAware {

  private BeanFactory beanFactory;

  private final InheritableThreadLocal<ExecutionContext> threadLocal;

  public ExecutionManager() {
    threadLocal = new InheritableThreadLocal<>();
  }

  public void removeExecutionContext() {
    threadLocal.remove();
  }

  public ExecutionContext newContext(UriInfo uriInfo) {
    ExecutionContext context = new ExecutionContext(beanFactory, uriInfo);
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
    return beanFactory.getBean(CouchServersConfig.class);
  }

  public CouchServer getCouchServer() {
    return beanFactory.getBean(CouchServer.class);
  }

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    this.beanFactory = beanFactory;
  }
}
