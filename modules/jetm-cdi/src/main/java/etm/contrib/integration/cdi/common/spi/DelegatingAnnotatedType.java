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

package etm.contrib.integration.cdi.common.spi;

import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 * This implementation can be used to modify the scanned annotations of a CDI bean during annotation-processing
 * in a CDI extension (i.e. add annotations):
 * Example:
 * <pre>
 * public class MyCdiExtension implements Extension {
 *   public <T> void processAnnotatedType(ProcessAnnotatedType<T> event) {
 *     Annotation myCustomAnnotation = new AnnotationLiteral<MyCustomAnnotation>() { };
 *     AnnotatedType<T> delegatingType = new DelegatingAnnotatedType<T>(event.getAnnotatedType(), myCustomAnnotation);
 *     event.setAnnotatedType(delegatingType);
 *   }
 * }
 * </pre>
 * <p/>
 * If you want to modify annotations of fields, constructors or methods,
 * you may implement your own {@link AnnotatedType}, inherit from {@link DelegatingAnnotatedType}
 * and override {@link DelegatingAnnotatedType#processAnnotatedField(AnnotatedField)},
 * {@link DelegatingAnnotatedType#processAnnotatedConstructor(AnnotatedConstructor)} or
 * {@link DelegatingAnnotatedType#processAnnotatedMethod(AnnotatedMethod)} to return an instance of
 * {@link DelegatingAnnotatedField}, {@link DelegatingAnnotatedConstructor} or {@link DelegatingAnnotatedMethod}
 * accordingly.
 * Example:
 * <pre>
 * public class MyAnnotatedType<T> extends DelegatingAnnotatedType<T> {
 *
 *   private Annotation[] additionalAnnotations;
 *
 *   public MyAnnotatedType(AnnotatedType<T> typeDelegate, Annotation... additionalAnnotations) {
 *     super(typeDelegate, additionalAnnotations);
 *     this.additionalAnnotations = additionalAnnotations;
 *   }
 *
 *   protected AnnotatedConstructor<T> processAnnotatedConstructor(AnnotatedConstructor<T> constructor) {
 *     return new DelegatingAnnotatedConstructor<T>(this, constructor, this.additionalAnnotations);
 *   }
 * }
 * </pre>
 *
 * @author Arne Limburg - open knowledge GmbH
 */
public class DelegatingAnnotatedType<T> extends DelegatingAnnotated implements AnnotatedType<T> {

  private final Set<AnnotatedMethod<? super T>> methods;
  private final Set<AnnotatedConstructor<T>> constructors;
  private final Set<AnnotatedField<? super T>> fields;

  public DelegatingAnnotatedType(AnnotatedType<T> delegateType, Annotation... additionalAnnotations) {
    super(delegateType, additionalAnnotations);

    constructors = new HashSet<AnnotatedConstructor<T>>();
    for (AnnotatedConstructor<T> constructor : delegateType.getConstructors()) {
      constructors.add(processAnnotatedConstructor(constructor));
    }


    methods = new HashSet<AnnotatedMethod<? super T>>();
    for (AnnotatedMethod<? super T> method : delegateType.getMethods()) {
      methods.add(processAnnotatedMethod(method));
    }

    fields = new HashSet<AnnotatedField<? super T>>();
    for (AnnotatedField<? super T> field : delegateType.getFields()) {
      fields.add(processAnnotatedField(field));
    }
  }

  public Class<T> getJavaClass() {
    return getDelegate().getJavaClass();
  }

  @Override
  public AnnotatedType<T> getDelegate() {
    return (AnnotatedType<T>) super.getDelegate();
  }

  public Set<AnnotatedConstructor<T>> getConstructors() {
    return constructors;
  }


  public Set<AnnotatedMethod<? super T>> getMethods() {
    return methods;
  }

  public Set<AnnotatedField<? super T>> getFields() {
    return fields;
  }

  protected AnnotatedField<? super T> processAnnotatedField(AnnotatedField<? super T> field) {
    return field;
  }

  protected AnnotatedConstructor<T> processAnnotatedConstructor(AnnotatedConstructor<T> constructor) {
    return constructor;
  }

  protected AnnotatedMethod<? super T> processAnnotatedMethod(AnnotatedMethod<? super T> method) {
    return method;
  }
}