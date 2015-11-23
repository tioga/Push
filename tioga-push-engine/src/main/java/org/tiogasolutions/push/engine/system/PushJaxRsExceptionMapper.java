package org.tiogasolutions.push.engine.system;

import org.apache.log4j.Logger;
import org.tiogasolutions.lib.jaxrs.TiogaJaxRsExceptionMapper;

public class PushJaxRsExceptionMapper extends TiogaJaxRsExceptionMapper {

  public PushJaxRsExceptionMapper() {
    super(true);
  }

  @Override
  protected void logInfo(String msg, Throwable ex) {
    Logger.getLogger(PushJaxRsExceptionMapper.class).info(msg, ex);
  }

  @Override
  protected void logError(String msg, Throwable ex) {
    Logger.getLogger(PushJaxRsExceptionMapper.class).error(msg, ex);
  }
}
