package org.tiogasolutions.push.engine.system;

import org.tiogasolutions.lib.jaxrs.providers.TiogaJaxRsExceptionMapper;
import org.tiogasolutions.push.pub.internal.PushExceptionInfo;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class PushJaxRsExceptionMapper extends TiogaJaxRsExceptionMapper {

    public PushJaxRsExceptionMapper() {
    }

    protected Response createResponse(int status, Throwable ex) {
        PushExceptionInfo exceptionInfo = new PushExceptionInfo(status, ex);
        return Response.status(status).entity(exceptionInfo).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

}
