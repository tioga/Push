/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.pushserver.engine.core.system;

import org.tiogasolutions.pushserver.engine.core.deprecated.EmailToSmsPushV1;
import org.tiogasolutions.pushserver.engine.core.deprecated.NotificationPushV1;
import org.tiogasolutions.pushserver.engine.core.jaxrs.security.ApiAuthenticationFilter;
import org.tiogasolutions.pushserver.engine.core.jaxrs.security.MngtAuthenticationFilter;
import org.tiogasolutions.pushserver.engine.core.jaxrs.security.SessionFilter;
import org.tiogasolutions.pushserver.engine.core.resources.RootResource;
import org.tiogasolutions.pushserver.engine.core.view.LocalResourceMessageBodyWriter;
import org.tiogasolutions.pushserver.engine.core.view.ThymeleafMessageBodyWriter;
import org.tiogasolutions.pushserver.jackson.CpObjectMapper;
import org.tiogasolutions.pushserver.pub.common.PingPush;
import org.tiogasolutions.pushserver.pub.common.PushType;
import org.tiogasolutions.pushserver.pub.push.LqNotificationPush;
import org.tiogasolutions.pushserver.pub.push.XmppPush;
import org.apache.log4j.Level;
import org.tiogasolutions.apis.bitly.BitlyApis;
import org.tiogasolutions.app.logging.LogUtils;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;
import org.tiogasolutions.pushserver.common.system.*;

import javax.ws.rs.core.Application;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CpApplication extends Application {

  private static final ThreadLocal<ExecutionContext> executionContext = new ThreadLocal<>();
  public static boolean hasExecutionContext() {
    return executionContext.get() != null;
  }
  public static ExecutionContext getExecutionContext() {
    if (executionContext.get() == null) {
      executionContext.set(new ExecutionContext());
    }
    return executionContext.get();
  }
  public static void removeExecutionContext() {
    executionContext.remove();
  }

  private final Set<Class<?>> classes;
  private final Map<String, Object> properties;

  public CpApplication() throws Exception {
    // Make sure our logging is working before ANYTHING else.
    LogUtils logUtils = new LogUtils();
    logUtils.initConsoleAppender(Level.WARN, LogUtils.DEFAULT_PATTERN);

    Map<String, Object> properties = new HashMap<>();
    Set<Class<?>> classes = new HashSet<>();

    String bitlyAccessToken = "9f5ed9c08c695b4a017bfb432eea58876a5d40cb";

    CpObjectMapper objectMapper = new CpObjectMapper();
    TiogaJacksonTranslator translator = new TiogaJacksonTranslator(objectMapper);

    AppContext appContext = new AppContext(
      new SessionStore(TimeUnit.MINUTES.toMillis(60)),
      objectMapper,
      new CpCouchServer(),
      new BitlyApis(translator, bitlyAccessToken));
    properties.put(AppContext.class.getName(), appContext);

    classes.add(CpFilter.class);
    classes.add(SessionFilter.class);
    classes.add(ApiAuthenticationFilter.class);
    classes.add(MngtAuthenticationFilter.class);

    classes.add(ThymeleafMessageBodyWriter.class);
    classes.add(LocalResourceMessageBodyWriter.class);
    classes.add(RootResource.class);
    classes.add(CpReaderWriterProvider.class);
    classes.add(CpJaxRsExceptionMapper.class);

    // TODO - remove these once these are properly referenced by their plugins
    PingPush.PUSH_TYPE.getCode();
    NotificationPushV1.PUSH_TYPE.getCode();
    LqNotificationPush.PUSH_TYPE.getCode();
    EmailToSmsPushV1.PUSH_TYPE.getCode();
    new PushType(XmppPush.class, "im", "IM");

    this.classes = Collections.unmodifiableSet(classes);
    this.properties = Collections.unmodifiableMap(properties);

    checkForDuplicates();

    // Force initialization.
    PluginManager.getPlugins();

    CpJobs jobs = new CpJobs(appContext);
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    scheduler.scheduleAtFixedRate(jobs::pruneEvents, 0, 1, TimeUnit.HOURS);
    scheduler.scheduleAtFixedRate(jobs::cleanAndCompactDatabase, 0, 4, TimeUnit.HOURS);
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
