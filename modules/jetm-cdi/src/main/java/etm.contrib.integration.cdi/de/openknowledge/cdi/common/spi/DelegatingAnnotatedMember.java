package etm.contrib.integration.cdi.de.openknowledge.cdi.common.spi;

import javax.enterprise.inject.spi.AnnotatedMember;
import javax.enterprise.inject.spi.AnnotatedType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Member;

/**
 * An implementation of {@link AnnotatedMember} that delegates and the superclass of various implementations
 * of the Annotated* type hierarchy.
 *
 * @author Arne Limburg - open knowledge GmbH
 */
public class DelegatingAnnotatedMember<T, M extends Member> extends DelegatingAnnotated implements AnnotatedMember<T> {

  private AnnotatedType<T> declaringType;
  private AnnotatedMember<T> delegate;

  public DelegatingAnnotatedMember(AnnotatedType<T> declaringAnnotatedType,
                                   AnnotatedMember<T> delegateMember,
                                   Annotation... additionalAnnotations) {
    super(delegateMember, additionalAnnotations);
    declaringType = declaringAnnotatedType;
    delegate = delegateMember;
  }

  @SuppressWarnings("unchecked")
  public M getJavaMember() {
    return (M) delegate.getJavaMember();
  }

  public boolean isStatic() {
    return delegate.isStatic();
  }

  public AnnotatedType<T> getDeclaringType() {
    return declaringType;
  }
}
