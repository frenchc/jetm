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

import etm.contrib.console.HttpConsoleServer;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 *
 * BeanDefinitionParser that parses a JETM console configuration element.
 *
 * @version $Revision$
 * @author $Id$
 * @since 1.2.0
 */
public class ConsoleBeanDefinitionParser extends JetmBeanDefinitionParser {

  protected AbstractBeanDefinition parseInternal(Element aElement, ParserContext aParserContext) {
    String expanded = aElement.getAttribute("expanded");
    String listenPort = aElement.getAttribute("listen-port");
    String workerSize = aElement.getAttribute("worker-size");
    String monitorRef = aElement.getAttribute("runtime-ref");

    BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(HttpConsoleServer.class);
    if (monitorRef != null && monitorRef.length() > 0) {
      builder.addConstructorArgReference(monitorRef);
    } else {
      builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);
    }

    if (expanded != null && expanded.length() > 0) {
      builder.addPropertyValue("expanded", expanded);
    }
    if (listenPort != null && listenPort.length() > 0) {
      builder.addPropertyValue("listenPort", listenPort);
    }
    if (workerSize != null && workerSize.length() > 0) {
      builder.addPropertyValue("workerSize", workerSize);
    }

    builder.setInitMethodName("start");
    builder.setDestroyMethodName("stop");

    return builder.getBeanDefinition();
  }


}

