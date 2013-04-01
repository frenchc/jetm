package etm.contrib.integration.cdi.de.openknowledge.cdi.common.spi;

import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * This implementation can be used to modify the scanned annotations of a CDI bean during annotation-processing
 * in a CDI extension (i.e. add annotations). See {@link DelegatingAnnotatedCallable} for a detailed example.
 *
 * @author Arne Limburg - open knowledge GmbH
 */
public class DelegatingAnnotatedMethod<T> extends DelegatingAnnotatedCallable<T, Method> implements AnnotatedMethod<T> {

  public DelegatingAnnotatedMethod(AnnotatedType<T> declaringType,
                                   AnnotatedMethod<T> delegate,
                                   Annotation... additionalAnnotations) {
    super(declaringType, delegate, additionalAnnotations);
  }
}
