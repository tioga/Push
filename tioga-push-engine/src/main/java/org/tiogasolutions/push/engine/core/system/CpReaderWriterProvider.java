/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */
package org.tiogasolutions.push.engine.core.system;

import org.tiogasolutions.push.engine.core.resources.api.ApiResourceV1;
import org.tiogasolutions.push.common.system.AppContext;
import org.tiogasolutions.push.pub.common.Push;
import org.tiogasolutions.push.pub.common.PushResponse;
import org.tiogasolutions.push.pub.common.UserAgent;
import org.tiogasolutions.lib.jaxrs.jackson.JacksonReaderWriterProvider;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Collections;

import static java.util.Collections.*;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CpReaderWriterProvider extends JacksonReaderWriterProvider {

  public CpReaderWriterProvider(@Context Application application) {
    super(AppContext.from(application).getObjectMapper(), singletonList(MediaType.APPLICATION_JSON_TYPE));
    addSupportedType(Push.class);
    addSupportedType(UserAgent.class);
    addSupportedType(PushResponse.class);
    addSupportedType(ApiResourceV1.PushResponseV1.class);
  }
}
