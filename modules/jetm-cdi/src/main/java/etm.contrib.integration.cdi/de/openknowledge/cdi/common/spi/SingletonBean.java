package etm.contrib.integration.cdi.de.openknowledge.cdi.common.spi;

import java.lang.annotation.Annotation;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.BeanManager;

/**
 * A bean that represents a singleton instance.
 *
 * @author Arne Limburg - open knowledge GmbH
 */
public class SingletonBean extends AbstractCdiBean<Object> {

  private Object instance;

  public SingletonBean(String name, Object singletonInstance, BeanManager beanManager, Annotation... additionalQualifiers) {
    super(name, singletonInstance.getClass(), beanManager, additionalQualifiers);
    instance = singletonInstance;
  }

  public SingletonBean(String name, Object singletonInstance, Class<? extends Annotation> scope,
                       BeanManager beanManager, Annotation... additionalQualifiers) {
    super(name, singletonInstance.getClass(), beanManager, additionalQualifiers);
    instance = singletonInstance;
    setScope(scope);
  }

  public Object create(CreationalContext<Object> creationalContext) {
    return instance;
  }

  public void destroy(Object instance, CreationalContext<Object> creationalContext) {
    // singleton instance is never destroyed
  }
}
