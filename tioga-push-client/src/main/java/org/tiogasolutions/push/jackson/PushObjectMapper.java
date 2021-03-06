package org.tiogasolutions.push.jackson;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.tiogasolutions.dev.jackson.TiogaJacksonInjectable;
import org.tiogasolutions.dev.jackson.TiogaJacksonModule;
import org.tiogasolutions.dev.jackson.TiogaJacksonObjectMapper;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class PushObjectMapper extends TiogaJacksonObjectMapper {

  public PushObjectMapper() {
    super(Arrays.asList(new TiogaJacksonModule(),
                        new PushJacksonModule()),
        Collections.<TiogaJacksonInjectable>emptyList());
  }

  protected PushObjectMapper(Collection<? extends Module> modules, Collection<? extends TiogaJacksonInjectable> injectables) {
    super(modules, injectables);
  }

  @Override
  public ObjectMapper copy() {
    _checkInvalidCopy(PushObjectMapper.class);
    return new PushObjectMapper(getModules(), getInjectables());
  }

}
