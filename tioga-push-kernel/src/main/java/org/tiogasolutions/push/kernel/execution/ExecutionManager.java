package org.tiogasolutions.push.kernel.execution;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Component;
import org.tiogasolutions.couchace.core.api.CouchServer;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.push.kernel.accounts.DomainStore;
import org.tiogasolutions.push.kernel.config.CouchServersConfig;
import org.tiogasolutions.push.kernel.plugins.PushProcessor;
import org.tiogasolutions.push.kernel.system.PluginManager;

import javax.ws.rs.core.UriInfo;

@Component
public class ExecutionManager implements BeanFactoryAware {

    private BeanFactory beanFactory;
    private final InheritableThreadLocal<ExecutionContext> threadLocal;

    public ExecutionManager() {
        this.threadLocal = new InheritableThreadLocal<>();
    }

    public void removeExecutionContext() {
        threadLocal.remove();
    }

    public ExecutionContext newContext(UriInfo uriInfo) {
        ExecutionContext context = new ExecutionContext(uriInfo, getPushProcessor(), getDomainStore());
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
    public ExecutionContext getContext() {
        ExecutionContext context = threadLocal.get();
        if (context == null) {
            throw ApiException.internalServerError("There is no current execution getContext for this thread.");
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

    public PushProcessor getPushProcessor() {
        return beanFactory.getBean(PushProcessor.class);
    }

    public DomainStore getDomainStore() {
        return beanFactory.getBean(DomainStore.class);
    }

    public PluginManager getPluginManager() {
        return beanFactory.getBean(PluginManager.class);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
