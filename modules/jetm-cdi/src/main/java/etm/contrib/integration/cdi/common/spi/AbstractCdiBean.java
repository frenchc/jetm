package etm.contrib.integration.cdi.common.spi;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.PassivationCapable;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Arne Limburg - open knowledge GmbH
 */
public abstract class AbstractCdiBean<T> implements PassivationCapable, Bean<T> {

  private String beanName;
  private Class<?> beanClass;
  private Set<Type> beanTypes;
  private Set<Annotation> qualifiers;
  private Set<Class<? extends Annotation>> stereotypes;
  private Class<? extends Annotation> scope  = Dependent.class;

  @SuppressWarnings("serial")
  public AbstractCdiBean(String name, Class<?> type, BeanManager beanManager, Annotation... additionalQualifiers) {
    beanName = name;
    beanClass = type;
    AnnotatedType<?> annotatedType = beanManager.createAnnotatedType(type);
    Set<Type> beanTypes = annotatedType.getTypeClosure();
    Set<Annotation> qualifiers = new HashSet<Annotation>();
    Set<Class<? extends Annotation>> stereotypes = new HashSet<Class<? extends Annotation>>();
    for (Annotation annotation : annotatedType.getAnnotations()) {
      if (beanManager.isQualifier(annotation.annotationType()) && !Named.class.equals(annotation.annotationType())) {
        qualifiers.add(annotation);
      }
      if (beanManager.isStereotype(annotation.annotationType())) {
        stereotypes.add(annotation.annotationType());
      }
    }
    for (Annotation qualifier : additionalQualifiers) {
      qualifiers.add(qualifier);
    }
    qualifiers.add(new AnnotationLiteral<Any>() {
    });
    if (qualifiers.size() == 1) {
      qualifiers.add(new AnnotationLiteral<Default>() {
      });
    }
    if (name != null) {
      qualifiers.add(new NamedLiteral(name));
    }
    this.beanTypes = Collections.unmodifiableSet(beanTypes);
    this.qualifiers = Collections.unmodifiableSet(qualifiers);
    this.stereotypes = Collections.unmodifiableSet(stereotypes);
  }

  public String getId() {
    return beanClass.getName() + "@" + System.identityHashCode(this);
  }

  public String getName() {
    return beanName;
  }

  public Class<?> getBeanClass() {
    return beanClass;
  }

  public Set<Type> getTypes() {
    return beanTypes;
  }

  public Set<Annotation> getQualifiers() {
    return qualifiers;
  }

  public Set<Class<? extends Annotation>> getStereotypes() {
    return stereotypes;
  }

  public Class<? extends Annotation> getScope() {
    return scope;
  }

  public Set<InjectionPoint> getInjectionPoints() {
    return Collections.emptySet();
  }

  public boolean isAlternative() {
    return false;
  }

  public boolean isNullable() {
    return true;
  }

  protected void setScope(Class<? extends Annotation> aScope) {
    scope = aScope;
  }
}

