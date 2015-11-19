package org.tiogasolutions.push.kernel;

import javax.ws.rs.core.UriInfo;

public class KernelUtils {

  public static String getContextRoot(UriInfo uriInfo) {
    if (uriInfo == null) return "/";
    String path = uriInfo.getBaseUri().toASCIIString();
    path = path.substring(0, path.length()-1);
    return path.trim();
  }
}
