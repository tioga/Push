package org.tiogasolutions.push.engine.core.jaxrs.security;

import java.lang.annotation.*;
import javax.ws.rs.NameBinding;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@NameBinding
public @interface MngtAuthentication {
}
