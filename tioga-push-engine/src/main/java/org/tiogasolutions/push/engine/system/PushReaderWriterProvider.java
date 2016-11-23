/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */
package org.tiogasolutions.push.engine.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.tiogasolutions.lib.jaxrs.providers.TiogaReaderWriterProvider;
import org.tiogasolutions.push.jackson.PushObjectMapper;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

@Provider
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PushReaderWriterProvider extends TiogaReaderWriterProvider {

  @Autowired
  public PushReaderWriterProvider(PushObjectMapper objectMapper) {
    super(objectMapper);
  }
}
