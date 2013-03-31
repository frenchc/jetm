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

import etm.contrib.aop.aopalliance.EtmMethodCallInterceptor;
import etm.core.monitor.EtmMonitor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * BeanDefinitionParser that parses JETM monitoring configuration element. Uses EtmMethodCallInterceptor
 * for bean-pattern based monitoring. Currently signature based monitoring is not supported.
 *
 * @author $Id$
 * @version $Revision$
 * @since 1.2.0
 */
public class MonitoringBeanDefinitionParser extends JetmBeanDefinitionParser {

  // todo cleanup and generify

  protected AbstractBeanDefinition parseInternal(Element aElement, ParserContext aParserContext) {
    String monitorRef = aElement.getAttribute("runtime-ref");

    Set registeredProxies = new HashSet();

    List signaturePattern = DomUtils.getChildElementsByTagName(aElement, "signature-pattern");
    if (signaturePattern.size() > 0) {
      throw new UnsupportedOperationException("Signature patterns currently not supported.");
    }

    List beanPattern = DomUtils.getChildElementsByTagName(aElement, "bean-pattern");
    for (int i = 0; i < beanPattern.size(); i++) {
      Element currentBeanPattern = (Element) beanPattern.get(i);
      String registeredProxy = registerBeanPattern(aParserContext, currentBeanPattern, monitorRef);
      registeredProxies.add(registeredProxy);
    }



    BeanDefinitionBuilder proxyWrapper = BeanDefinitionBuilder.rootBeanDefinition(MonitoringInfo.class);
    proxyWrapper.addPropertyValue("proxyNames", registeredProxies);

    return proxyWrapper.getBeanDefinition();
  }

  private String registerBeanPattern(ParserContext aParserContext, Element aCurrentBeanPattern, String monitorRef) {
    BeanDefinitionRegistry definitionRegistry = aParserContext.getRegistry();

    String group = aCurrentBeanPattern.getAttribute("group");


    String interceptorName = null;

    if (group != null && group.length() > 0) {
      // use a named interceptor, locate definition for it
      String[] names = definitionRegistry.getBeanDefinitionNames();
      for (int i = 0; i < names.length; i++) {
        BeanDefinition definition = definitionRegistry.getBeanDefinition(names[i]);
        if ("etm.contrib.integration.etm.contrib.integration.spring.configuration.MonitoringBeanDefinitionParser$NamedEtmMethodCallInterceptor".equals(definition.getBeanClassName())) {
          MutablePropertyValues propertyValues = definition.getPropertyValues();
          PropertyValue propertyValue = propertyValues.getPropertyValue("name");
          if (propertyValue.getValue().equals(group)) {
            interceptorName = names[i];
            break;
          }
        }
      }
      // interceptor not found, we register one
      if (interceptorName == null) {
        BeanDefinitionBuilder interceptorBuilder = BeanDefinitionBuilder.rootBeanDefinition(NamedEtmMethodCallInterceptor.class);
        interceptorBuilder.addPropertyValue("name", group);
        if (monitorRef != null && monitorRef.length() > 0) {
          interceptorBuilder.addConstructorArgReference(monitorRef);
        } else {
          interceptorBuilder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);
        }
        AbstractBeanDefinition definition = interceptorBuilder.getBeanDefinition();
        interceptorName = generateName(definition, aParserContext);
        definitionRegistry.registerBeanDefinition(interceptorName, definition);
      }

    } else {
      // use standard interceptor, locate definition for it
      String[] names = definitionRegistry.getBeanDefinitionNames();
      for (int i = 0; i < names.length; i++) {
        BeanDefinition definition = definitionRegistry.getBeanDefinition(names[i]);
        if ("etm.contrib.aop.aopalliance.EtmMethodCallInterceptor".equals(definition.getBeanClassName())) {
          interceptorName = names[i];
          break;
        }
      }
      // interceptor not found, we register one
      if (interceptorName == null) {
        BeanDefinitionBuilder interceptorBuilder = BeanDefinitionBuilder.rootBeanDefinition(EtmMethodCallInterceptor.class);
        if (monitorRef != null && monitorRef.length() > 0) {
          interceptorBuilder.addConstructorArgReference(monitorRef);
        } else {
          interceptorBuilder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);
        }
        AbstractBeanDefinition definition = interceptorBuilder.getBeanDefinition();
        interceptorName = generateName(definition, aParserContext);
        definitionRegistry.registerBeanDefinition(interceptorName, definition);
      }
    }

    // locate beanDefinition
    ProxyHolder proxyDefinition = locateBeanNameProxy(aParserContext, interceptorName);
    MutablePropertyValues propertyValues = proxyDefinition.getDefinition().getPropertyValues();

    String pattern = DomUtils.getTextValue(aCurrentBeanPattern).trim();

    PropertyValue currentBeanNames = propertyValues.getPropertyValue("beanNames");
    if (currentBeanNames == null) {
      propertyValues.addPropertyValue("beanNames", pattern);
    } else {
      propertyValues.removePropertyValue("beanNames");
      propertyValues.addPropertyValue("beanNames", currentBeanNames.getValue() + "," + pattern);
    }

    return proxyDefinition.getName();
  }


  private ProxyHolder locateBeanNameProxy(ParserContext aParserContext, String aInterceptorName) {
    BeanDefinitionRegistry definitionRegistry = aParserContext.getRegistry();
    String[] names = definitionRegistry.getBeanDefinitionNames();

    for (int i = 0; i < names.length; i++) {
      BeanDefinition definition = definitionRegistry.getBeanDefinition(names[i]);
      if ("org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator".equals(definition.getBeanClassName())) {
        MutablePropertyValues propertyValues = definition.getPropertyValues();
        PropertyValue propertyValue = propertyValues.getPropertyValue("interceptorNames");
        if (propertyValue.getValue().equals(aInterceptorName)) {
          return new ProxyHolder(names[i], (AbstractBeanDefinition) definition);
        }
      }
    }

    // we did not have one so far.
    // register it
    BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(BeanNameAutoProxyCreator.class);
    builder.addPropertyValue("interceptorNames", aInterceptorName);
    AbstractBeanDefinition newDefinition = builder.getBeanDefinition();

    String beanName = generateName(newDefinition, aParserContext);
    definitionRegistry.registerBeanDefinition(beanName, newDefinition);

    return new ProxyHolder(beanName, newDefinition);
  }

  /**
   *
   * A EtmMethodCallInterceptor that uses one name for all measurement points. 
   *
   */
  public static class NamedEtmMethodCallInterceptor extends EtmMethodCallInterceptor {

    private String name;

    public NamedEtmMethodCallInterceptor(EtmMonitor aEtmMonitor) {
      super(aEtmMonitor);
    }

    public void setName(String aName) {
      name = aName;
    }

    protected String calculateName(MethodInvocation aMethodInvocation) {
      return name;
    }
  }

  /**
   *
   * A helper class holding currently known proxy names.
   *
   */
  public static class MonitoringInfo {
    private Set proxyNames;


    public Set getProxyNames() {
      return proxyNames;
    }

    public void setProxyNames(Set aProxyNames) {
      proxyNames = aProxyNames;
    }
  }

  /**
   *
   * Bean definition wrapper.
   *
   */
  class ProxyHolder {
    private String name;
    private AbstractBeanDefinition definition;


    public ProxyHolder(String aName, AbstractBeanDefinition aDefinition) {
      name = aName;
      definition = aDefinition;
    }


    public String getName() {
      return name;
    }

    public AbstractBeanDefinition getDefinition() {
      return definition;
    }
  }

}
