/*
 *
 * Copyright (c) void.fm
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name void.fm nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific
 * prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package etm.contrib.integration.spring.configuration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import etm.core.util.Log;
import etm.core.util.LogAdapter;

/**
 * Base class for our Spring BeanDefinitionParsers. This class alters the behavior of
 * {@link AbstractBeanDefinitionParser} such that it automatically assign a id if none
 * is given.
 *
 * @author void.fm
 * @version $Revision$
 */
public abstract class JetmBeanDefinitionParser extends AbstractBeanDefinitionParser {

  private static final LogAdapter log = Log.getLog(JetmBeanDefinitionParser.class);
  
  protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext)
    throws BeanDefinitionStoreException {
    String id = super.resolveId(element, definition, parserContext);
    if (id == null || id.length() == 0) {
      return generateName(definition, parserContext);
    }
    return id;
  }

  protected String generateName(AbstractBeanDefinition definition, ParserContext parserContext) {
    Method method = getGenerateBeanNameMethod();
    try {
      Object[] parameters = getMethodParameters(definition, parserContext);
      Object beanName = method.invoke((Object) null, parameters);
      return beanName.toString();
    } catch (IllegalArgumentException e) {
      throw new IllegalStateException("Unable to invoke spring method via reflection.", e);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException("Unable to invoke spring method via reflection.", e);
    } catch (InvocationTargetException e) {
      throw new IllegalStateException("Unable to invoke spring method via reflection.", e);
    }
  }

  private Object[] getMethodParameters(AbstractBeanDefinition definition, ParserContext parserContext) {
    Object[] parameters = new Object[3];
    parameters[0] = definition;
    parameters[1] = parserContext.getRegistry();
    parameters[2] = new Boolean(parserContext.isNested());
    return parameters;
  }

  private Method getGenerateBeanNameMethod() {
    try {
      return getGenerateBeanNameMethodSinceSpring25();
    } catch (NoSuchMethodException e1) {
      try {
        return getGenerateBeanNameMethodSinceSpring11();
      } catch (NoSuchMethodException e2) {
        throw new IllegalStateException("BeanDefinitionReaderUtils.generateBeanName() method not found in neither Spring < 2.5 nor Spring >= 2.5 .", e2);
      }
    }
  }

  private Method getGenerateBeanNameMethodSinceSpring11() throws NoSuchMethodException {
    log.debug("Using BeanDefinitionReaderUtils.generateBeanName() method for spring < 2.5 and >= 1.1");
    return BeanDefinitionReaderUtils.class.getMethod("generateBeanName", new Class[] {AbstractBeanDefinition.class, BeanDefinitionRegistry.class, Boolean.TYPE});
  }

  private Method getGenerateBeanNameMethodSinceSpring25() throws NoSuchMethodException {
    log.debug("Using BeanDefinitionReaderUtils.generateBeanName() method for spring >= 2.5");
    return BeanDefinitionReaderUtils.class.getMethod("generateBeanName", new Class[] {BeanDefinition.class, BeanDefinitionRegistry.class, Boolean.TYPE});
  }

}
