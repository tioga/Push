package org.tiogasolutions.push.engine.core.system;

import org.apache.log4j.Logger;
import org.tiogasolutions.lib.jaxrs.TiogaJaxRsExceptionMapper;

public class CpJaxRsExceptionMapper extends TiogaJaxRsExceptionMapper {

  public CpJaxRsExceptionMapper() {
    super(true);
  }

  @Override
  protected void logInfo(String msg, Throwable ex) {
    Logger.getLogger(CpJaxRsExceptionMapper.class).info(msg, ex);
  }

  @Override
  protected void logError(String msg, Throwable ex) {
    Logger.getLogger(CpJaxRsExceptionMapper.class).error(msg, ex);
  }
}
