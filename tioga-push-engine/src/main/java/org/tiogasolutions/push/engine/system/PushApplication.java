/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.engine.system;

import org.springframework.stereotype.Component;
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

  private final Set<Class<?>> classes = new HashSet<>();
  private final Map<String, Object> properties = new HashMap<>();

  public PushApplication() throws Exception {

    classes.add(PushRequestFilter.class);
    classes.add(ApiAuthenticationFilter.class);
    classes.add(MngtAuthenticationFilter.class);

    classes.add(ThymeleafMessageBodyWriter.class);
    classes.add(LocalResourceMessageBodyWriter.class);
    classes.add(RootResource.class);

    // TODO - remove these once these are properly referenced by their plugins
    PingPush.PUSH_TYPE.getCode();
    new PushType(XmppPush.class, "im", "IM");

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
