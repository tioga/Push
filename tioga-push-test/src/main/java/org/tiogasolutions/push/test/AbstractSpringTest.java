package org.tiogasolutions.push.test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testng.annotations.BeforeMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AbstractSpringTest {

  private final Class<?>[] configurationClasses;

  public AbstractSpringTest(Class<?>...configurationClasses) {
    List<Class<?>> classes = new ArrayList<>();
    Collections.addAll(classes, configurationClasses);
    classes.add(SpringTestConfig.class);

    this.configurationClasses = classes.toArray(new Class<?>[classes.size()]);
  }

  @BeforeMethod
  public void beforeMethodAutowireTest() throws Exception {
    AnnotationConfigApplicationContext applicationContext;

    applicationContext = new AnnotationConfigApplicationContext();
    applicationContext.getEnvironment().setActiveProfiles("test");
    applicationContext.scan("org.tiogasolutions.push");
    applicationContext.register(configurationClasses);
    applicationContext.refresh();

    // Inject our unit test with any beans.
    applicationContext.getBeanFactory().autowireBean(this);
  }
}
