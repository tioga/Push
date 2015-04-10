package org.tiogasolutions.push.jackson;

import com.fasterxml.jackson.databind.*;
import java.util.*;
import org.tiogasolutions.dev.jackson.*;

public class CpObjectMapper extends TiogaJacksonObjectMapper {

  public CpObjectMapper() {
    super(Arrays.asList(
                new TiogaJacksonModule(),
                new CpJacksonModule()),
        Collections.<TiogaJacksonInjectable>emptyList());
  }

  protected CpObjectMapper(Collection<? extends Module> modules, Collection<? extends TiogaJacksonInjectable> injectables) {
    super(modules, injectables);
  }

  @Override
  public ObjectMapper copy() {
    _checkInvalidCopy(CpObjectMapper.class);
    return new CpObjectMapper(getModules(), getInjectables());
  }

}
