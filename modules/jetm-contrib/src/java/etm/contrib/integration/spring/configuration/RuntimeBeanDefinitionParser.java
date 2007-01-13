/*
 *
 * Copyright (c) 2004, 2005, 2006, 2007 void.fm
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

import etm.core.timer.DefaultTimer;
import etm.core.timer.Java15NanoTimer;
import etm.core.timer.SunHighResTimer;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @version $Revision$
 * @author $Id$
 * @since 1.2.0
 */
public class RuntimeBeanDefinitionParser extends AbstractBeanDefinitionParser {

  protected AbstractBeanDefinition parseInternal(Element aElement, ParserContext aParserContext) {
    String type = aElement.getAttribute("type");
    String timer = aElement.getAttribute("timer");

    NodeList features = aElement.getElementsByTagName("features");
    NodeList aggregatorChain = aElement.getElementsByTagName("aggregator-chain");

    NodeList extension = aElement.getElementsByTagName("extensions");

    if (type == null || type.length() == 0) {
      type = "nested";
    }

    BeanDefinitionBuilder builder;
    if ("nested".equals(type)) {
      builder = BeanDefinitionBuilder.rootBeanDefinition(etm.core.monitor.NestedMonitor.class);
    } else if ("flat".equals(type)) {
      builder = BeanDefinitionBuilder.rootBeanDefinition(etm.core.monitor.FlatMonitor.class);
    } else {
      try {
        builder = BeanDefinitionBuilder.rootBeanDefinition(Class.forName(type));
      } catch (ClassNotFoundException e) {
        throw new FatalBeanException("Unable to locate monitor class " + type, e);
      }
    } 
    if (timer != null && timer.length() > 0) {
      addTimerDefinition(timer, builder);
    }

    builder.setInitMethodName("start");
    builder.setDestroyMethodName("stop");
    return builder.getBeanDefinition();

  }

  private void addTimerDefinition(String aTimer, BeanDefinitionBuilder builder) {
    if ("jdk50".equals(aTimer)) {
      builder.addConstructorArg(new Java15NanoTimer());
    } else if ("sun".equals(aTimer)) {
      builder.addConstructorArg(new SunHighResTimer());
    } else if ("default".equals(aTimer)) {
      builder.addConstructorArg(new DefaultTimer());
    } else {
      RootBeanDefinition timerBeanDefinition = new RootBeanDefinition();
      timerBeanDefinition.setBeanClassName(aTimer);
      builder.addConstructorArg(timerBeanDefinition);
    }
  }
}