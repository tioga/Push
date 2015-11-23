/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */
package org.tiogasolutions.push.engine.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tiogasolutions.lib.jaxrs.jackson.JacksonReaderWriterProvider;
import org.tiogasolutions.push.jackson.PushObjectMapper;
import org.tiogasolutions.push.pub.common.Push;
import org.tiogasolutions.push.pub.common.PushResponse;
import org.tiogasolutions.push.pub.common.UserAgent;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import static java.util.Collections.singletonList;

@Provider
@Component
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PushReaderWriterProvider extends JacksonReaderWriterProvider {

  @Autowired
  @SuppressWarnings("unchecked")
  public PushReaderWriterProvider(PushObjectMapper objectMapper) {
    super(objectMapper, singletonList(MediaType.APPLICATION_JSON_TYPE));
    addSupportedType(Push.class);
    addSupportedType(UserAgent.class);
    addSupportedType(PushResponse.class);
  }
}
