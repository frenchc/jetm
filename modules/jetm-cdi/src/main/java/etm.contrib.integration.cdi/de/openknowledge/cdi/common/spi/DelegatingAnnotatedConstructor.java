package etm.contrib.integration.cdi.de.openknowledge.cdi.common.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedType;

/**
 * This implementation can be used to modify the scanned annotations of a CDI bean during annotation-processing
 * in a CDI extension (i.e. add annotations). See {@link DelegatingAnnotatedCallable} for a detailed example.
 *
 * @author Arne Limburg - open knowledge GmbH
 */
public class DelegatingAnnotatedConstructor<T> extends DelegatingAnnotatedCallable<T, Constructor<T>>
  implements AnnotatedConstructor<T> {

  public DelegatingAnnotatedConstructor(AnnotatedType<T> declaringType,
                                        AnnotatedConstructor<T> delegate,
                                        Annotation... additionalAnnotations) {
    super(declaringType, delegate, additionalAnnotations);
  }
}
