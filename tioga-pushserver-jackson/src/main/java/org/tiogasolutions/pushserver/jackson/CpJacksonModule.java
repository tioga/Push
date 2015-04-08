package org.tiogasolutions.pushserver.jackson;

import org.tiogasolutions.pushserver.pub.common.Push;
import org.tiogasolutions.pushserver.pub.common.PushType;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
* Created by jacobp on 8/4/2014.
*/
public class CpJacksonModule extends SimpleModule {
  public CpJacksonModule() {
    setMixInAnnotation(Push.class, PushMixin.class);

    addSerializer(PushType.class, new PushTypeSerializer());
    addDeserializer(PushType.class, new PushTypeDeserializer());
  }
}
