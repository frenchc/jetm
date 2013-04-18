package etm.contrib.integration.cdi.de.openknowledge.cdi.common.spi;

import javax.enterprise.inject.spi.AnnotatedCallable;
import javax.enterprise.inject.spi.AnnotatedParameter;
import java.lang.annotation.Annotation;

/**
 * This implementation can be used to modify the scanned annotations of a CDI bean during annotation-processing
 * in a CDI extension (i.e. add annotations). See {@link DelegatingAnnotatedCallable} for a detailed example.
 *
 * @author Arne Limburg - open knowledge GmbH
 */
public class DelegatingAnnotatedParameter<T> extends DelegatingAnnotated implements AnnotatedParameter<T> {

  private AnnotatedCallable<T> declaringCallable;
  private AnnotatedParameter<T> delegate;

  public DelegatingAnnotatedParameter(AnnotatedCallable<T> declaringAnnotatedCallable,
                                      AnnotatedParameter<T> delegateParameter,
                                      Annotation... additionalAnnotations) {
    super(delegateParameter, additionalAnnotations);
    declaringCallable = declaringAnnotatedCallable;
    delegate = delegateParameter;
  }

  public int getPosition() {
    return delegate.getPosition();
  }

  public AnnotatedCallable<T> getDeclaringCallable() {
    return declaringCallable;
  }
}