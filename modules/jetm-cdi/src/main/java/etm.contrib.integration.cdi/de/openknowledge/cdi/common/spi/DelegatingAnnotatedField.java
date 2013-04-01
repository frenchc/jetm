package etm.contrib.integration.cdi.de.openknowledge.cdi.common.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedType;

/**
 * This implementation can be used to modify the scanned annotations of a CDI bean during annotation-processing
 * in a CDI extension (i.e. add annotations). See {@link DelegatingAnnotatedType} for a detailed example.
 *
 * @author Arne Limburg - open knowledge GmbH
 */
public class DelegatingAnnotatedField<T> extends DelegatingAnnotatedMember<T, Field> implements AnnotatedField<T> {

  public DelegatingAnnotatedField(AnnotatedType<T> declaringType,
                                  AnnotatedField<T> delegate,
                                  Annotation... additionalAnnotations) {
    super(declaringType, delegate, additionalAnnotations);
  }
}
