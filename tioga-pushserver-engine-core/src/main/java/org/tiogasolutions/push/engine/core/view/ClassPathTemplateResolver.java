package org.tiogasolutions.push.engine.core.view;

import org.thymeleaf.templateresolver.TemplateResolver;

public class ClassPathTemplateResolver extends TemplateResolver {
  public ClassPathTemplateResolver() {
    super();
    super.setResourceResolver(new ClassPathResourceResolver());
  }
}
