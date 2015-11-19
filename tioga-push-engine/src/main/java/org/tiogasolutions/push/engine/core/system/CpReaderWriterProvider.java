/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */
package org.tiogasolutions.push.engine.core.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.tiogasolutions.lib.jaxrs.jackson.JacksonReaderWriterProvider;
import org.tiogasolutions.push.jackson.CpObjectMapper;
import org.tiogasolutions.push.pub.common.Push;
import org.tiogasolutions.push.pub.common.PushResponse;
import org.tiogasolutions.push.pub.common.UserAgent;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static java.util.Collections.singletonList;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CpReaderWriterProvider extends JacksonReaderWriterProvider {

  @Autowired
  public CpReaderWriterProvider(CpObjectMapper objectMapper) {
    super(objectMapper, singletonList(MediaType.APPLICATION_JSON_TYPE));
    addSupportedType(Push.class);
    addSupportedType(UserAgent.class);
    addSupportedType(PushResponse.class);
  }
}
