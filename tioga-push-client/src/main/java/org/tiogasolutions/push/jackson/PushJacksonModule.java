package org.tiogasolutions.push.jackson;

import org.tiogasolutions.push.pub.common.Push;
import org.tiogasolutions.push.pub.common.PushType;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class PushJacksonModule extends SimpleModule {

  public PushJacksonModule() {
    setMixInAnnotation(Push.class, PushMixin.class);

    addSerializer(PushType.class, new PushTypeSerializer());
    addDeserializer(PushType.class, new PushTypeDeserializer());
  }

}
