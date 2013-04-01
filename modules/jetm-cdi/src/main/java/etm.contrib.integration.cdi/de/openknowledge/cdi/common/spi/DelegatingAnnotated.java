/*
 * Copyright open knowledge GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package etm.contrib.integration.cdi.de.openknowledge.cdi.common.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.spi.Annotated;

/**
 * An implementation of {@link Annotated} that delegates and the superclass of various implementations
 * of the Annotated* type hierarchy.
 *
 * @author Arne Limburg - open knowledge GmbH
 */
public class DelegatingAnnotated {

  private Annotated delegate;
  private Map<Class<? extends Annotation>, Annotation> additionalAnnotations;
  private Map<Class<? extends Annotation>, Annotation> removedAnnotations =
    new HashMap<Class<? extends Annotation>, Annotation>();

  public DelegatingAnnotated(Annotated annotated, Annotation... annotationsToAdd) {
    delegate = annotated;
    additionalAnnotations = new HashMap<Class<? extends Annotation>, Annotation>();
    for (Annotation annotation : annotationsToAdd) {
      addAnnotation(annotation);
    }
  }

  public Annotated getDelegate() {
    return delegate;
  }

  public Type getBaseType() {
    return delegate.getBaseType();
  }

  public Set<Type> getTypeClosure() {
    return delegate.getTypeClosure();
  }

  public Set<Annotation> getAnnotations() {
    Set<Annotation> annotations = new HashSet<Annotation>(delegate.getAnnotations());
    annotations.removeAll(removedAnnotations.values());
    annotations.addAll(additionalAnnotations.values());
    return annotations;
  }

  @SuppressWarnings("unchecked")
  public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
    if (additionalAnnotations.containsKey(annotationType)) {
      return (T) additionalAnnotations.get(annotationType);
    } else if (!this.removedAnnotations.containsKey(annotationType)) {
      return delegate.getAnnotation(annotationType);
    } else {
      return null;
    }
  }

  public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
    return additionalAnnotations.containsKey(annotationType)
      || (delegate.isAnnotationPresent(annotationType) && !removedAnnotations.containsKey(annotationType));
  }

  public void removeAnnotation(Class<? extends Annotation> annotationType) {
    Annotation annotation = delegate.getAnnotation(annotationType);
    if (annotation != null) {
      removedAnnotations.put(annotation.annotationType(), annotation);
    }
  }

  public void addAnnotation(Annotation annotation) {
    additionalAnnotations.put(annotation.annotationType(), annotation);
  }
}
