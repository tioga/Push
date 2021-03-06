package org.tiogasolutions.push.engine.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.tiogasolutions.lib.jaxrs.providers.TiogaJaxRsExceptionMapper;
import org.tiogasolutions.notify.notifier.Notifier;

import javax.ws.rs.ext.Provider;

@Provider
public class PushExceptionMapper extends TiogaJaxRsExceptionMapper {

    private final Notifier notifier;

    @Autowired
    public PushExceptionMapper(Notifier notifier) {
        this.notifier = notifier;
    }

    @Override
    protected void log4xxException(String msg, Throwable throwable, int statusCode) {
        super.log4xxException(msg, throwable, statusCode);

//        notifier.begin()
//                .summary(msg)
//                .exception(throwable)
//                .trait("action", "Unhandled 4xx")
//                .trait("http-status-code", statusCode)
//                .trait("http-uri", cleanUrl(getUriInfo().getRequestUri()))
//                .send();
    }

    @Override
    protected void log5xxException(String msg, Throwable throwable, int statusCode) {
        super.log5xxException(msg, throwable, statusCode);

        notifier.begin()
                .summary(msg)
                .exception(throwable)
                .trait("action", "Unhandled 5xx")
                .trait("http-status-code", statusCode)
                .trait("http-uri", cleanUrl(getUriInfo().getRequestUri()))
                .send();
    }
}
