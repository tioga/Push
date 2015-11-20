/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.engine.system;

import ch.qos.logback.classic.Level;
import org.springframework.stereotype.Component;
import org.tiogasolutions.app.common.LogUtils;
import org.tiogasolutions.push.engine.jaxrs.security.ApiAuthenticationFilter;
import org.tiogasolutions.push.engine.jaxrs.security.MngtAuthenticationFilter;
import org.tiogasolutions.push.engine.jaxrs.security.PushRequestFilter;
import org.tiogasolutions.push.engine.resources.RootResource;
import org.tiogasolutions.push.engine.view.LocalResourceMessageBodyWriter;
import org.tiogasolutions.push.engine.view.ThymeleafMessageBodyWriter;
import org.tiogasolutions.push.pub.XmppPush;
import org.tiogasolutions.push.pub.common.PingPush;
import org.tiogasolutions.push.pub.common.PushType;

import javax.ws.rs.core.Application;
import java.util.*;

@Component
public class PushApplication extends Application {

  public static final int CURRENT_API_VERSION = 2;

//  private static final ThreadLocal<ExecutionContext> executionContext = new ThreadLocal<>();
//  public static boolean hasExecutionContext() {
//    return executionContext.get() != null;
//  }
//  public static ExecutionContext getExecutionContext() {
//    if (executionContext.get() == null) {
//      executionContext.set(new ExecutionContext());
//    }
//    return executionContext.get();
//  }
//  public static void removeExecutionContext() {
//    executionContext.remove();
//  }

  private final Set<Class<?>> classes;
  private final Map<String, Object> properties;

//  private final String mainDbName;
//  private final String domainDbName;

  public PushApplication() throws Exception {
//    this.mainDbName = "push-master";
//    this.domainDbName = "push-domain";

    // Make sure our logging is working before ANYTHING else.
    LogUtils.initLogback(Level.WARN);

    Map<String, Object> properties = new HashMap<>();
    Set<Class<?>> classes = new HashSet<>();

//    CpObjectMapper objectMapper = new CpObjectMapper();
//    TiogaJacksonTranslator translator = new TiogaJacksonTranslator(objectMapper);
//    CpCouchServer couchServer = new CpCouchServer();
//    AppContext appContext = new AppContext(
//      new SessionStore(TimeUnit.MINUTES.toMillis(60)),
//      objectMapper,
//      mainDbName,
//      new DomainDatabaseConfig(couchServer, domainDbName),
//      new BitlyApis(translator, bitlyAccessToken));
//
//    properties.put(AppContext.class.getName(), appContext);

//    classes.add(CpFilter.class);
    classes.add(PushRequestFilter.class);
    classes.add(ApiAuthenticationFilter.class);
    classes.add(MngtAuthenticationFilter.class);

    classes.add(ThymeleafMessageBodyWriter.class);
    classes.add(LocalResourceMessageBodyWriter.class);
    classes.add(RootResource.class);
    classes.add(CpReaderWriterProvider.class);
    classes.add(CpJaxRsExceptionMapper.class);

    // TODO - remove these once these are properly referenced by their plugins
    PingPush.PUSH_TYPE.getCode();
    new PushType(XmppPush.class, "im", "IM");

    this.classes = Collections.unmodifiableSet(classes);
    this.properties = Collections.unmodifiableMap(properties);

    checkForDuplicates();

//    // Force initialization.
//    pluginManager.getPlugins();

//    CpJobs jobs = new CpJobs(appContext);
//    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//    scheduler.scheduleAtFixedRate(jobs::pruneEvents, 0, 1, TimeUnit.HOURS);
//    scheduler.scheduleAtFixedRate(jobs::cleanAndCompactDatabase, 0, 4, TimeUnit.HOURS);
  }

  private void checkForDuplicates() {
    Set<Class> existing = new HashSet<>();

    for (Class type : classes) {
      if (type == null) continue;
      if (existing.contains(type)) {
        String msg = String.format("The class %s has already been registered.", type.getName());
        throw new IllegalArgumentException(msg);
      }
    }

    existing.clear();
  }

  @Override
  public Map<String, Object> getProperties() {
    return properties;
  }
  @Override
  public Set<Class<?>> getClasses() {
    return classes;
  }
}
