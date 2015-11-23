package org.tiogasolutions.push.jackson;

import org.tiogasolutions.push.pub.common.Push;
import org.tiogasolutions.push.pub.common.PushType;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
* Created by jacobp on 8/4/2014.
*/
public class PushJacksonModule extends SimpleModule {
  public PushJacksonModule() {
    setMixInAnnotation(Push.class, PushMixin.class);

    addSerializer(PushType.class, new PushTypeSerializer());
    addDeserializer(PushType.class, new PushTypeDeserializer());
  }
}
