package org.tiogasolutions.push.test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testng.annotations.BeforeClass;

public class AbstractSpringTest {

  @BeforeClass
  public void beforeClassAutowireTest() throws Exception {
    AnnotationConfigApplicationContext applicationContext;

    applicationContext = new AnnotationConfigApplicationContext();
    applicationContext.getEnvironment().setActiveProfiles("test");
    applicationContext.scan("org.tiogasolutions.push");
    applicationContext.refresh();

    // Inject our unit test with any beans.
    applicationContext.getBeanFactory().autowireBean(this);
  }
}
