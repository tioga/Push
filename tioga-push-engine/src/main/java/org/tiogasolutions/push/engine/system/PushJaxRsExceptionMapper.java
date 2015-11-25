package org.tiogasolutions.push.engine.system;

import org.tiogasolutions.lib.jaxrs.providers.TiogaJaxRsExceptionMapper;

import javax.ws.rs.ext.Provider;

@Provider
public class PushJaxRsExceptionMapper extends TiogaJaxRsExceptionMapper {

  public PushJaxRsExceptionMapper() {
  }

}
