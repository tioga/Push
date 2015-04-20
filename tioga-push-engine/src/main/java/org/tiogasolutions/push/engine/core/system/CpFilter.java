/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.tiogasolutions.push.engine.core.system;

import java.io.*;
import javax.ws.rs.container.*;

import org.apache.commons.logging.*;

public class CpFilter implements ContainerResponseFilter {

  private static final Log log = LogFactory.getLog(CpFilter.class);

  public CpFilter() {
    log.info("Created");
  }

  @Override
  public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
    responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
    responseContext.getHeaders().add("X-UA-Compatible", "IE=Edge");
    responseContext.getHeaders().add("p3p", "CP=\"Push server does not have a P3P policy. Learn why here: https://www.TiogaSolutions.com/push/static/p3p.html\"");
  }
}