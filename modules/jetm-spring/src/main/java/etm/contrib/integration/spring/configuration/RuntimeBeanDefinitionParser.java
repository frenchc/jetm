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

import etm.contrib.aggregation.log.CommonsLoggingAggregator;
import etm.contrib.aggregation.log.Jdk14LogAggregator;
import etm.contrib.aggregation.log.Log4jAggregator;
import etm.core.aggregation.BufferedThresholdAggregator;
import etm.core.aggregation.BufferedTimedAggregator;
import etm.core.aggregation.NotifyingAggregator;
import etm.core.aggregation.RootAggregator;
import etm.core.aggregation.persistence.FileSystemPersistenceBackend;
import etm.core.aggregation.persistence.PersistentRootAggregator;
import etm.core.configuration.EtmMonitorFactory;
import etm.core.jmx.EtmMonitorJmxPlugin;
import etm.core.timer.DefaultTimer;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * BeanDefinitionParser that parses a JETM runtime element.
 *
 * @author $Id$
 * @version $Revision$
 * @since 1.2.0
 */
public class RuntimeBeanDefinitionParser extends JetmBeanDefinitionParser {

  protected AbstractBeanDefinition parseInternal(Element aElement, ParserContext aParserContext) {
    String type = aElement.getAttribute("type");
    String timer = aElement.getAttribute("timer");

    Element features = DomUtils.getChildElementByTagName(aElement, "features");
    Element aggregatorChain = DomUtils.getChildElementByTagName(aElement, "aggregator-chain");
    Element extension = DomUtils.getChildElementByTagName(aElement, "extension");

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

    if (features != null) {
      buildChainFromFeatures(builder, features);
    } else if (aggregatorChain != null) {
      buildChainFromChain(builder, aggregatorChain);
    }

    if (extension != null) {
      if (features != null) {
        addExtensions(builder, extension, DomUtils.getChildElementByTagName(features, "jmx"));
      } else {
        addExtensions(builder, extension, null);
      }
    }

    builder.setInitMethodName("start");
    builder.setDestroyMethodName("stop");
    return builder.getBeanDefinition();
  }

  private void addExtensions(BeanDefinitionBuilder aBuilder, Element aExtension, Element jmx) {
    List pluginConfigs = DomUtils.getChildElementsByTagName(aExtension, "plugin");

    if (!pluginConfigs.isEmpty()) {
      List plugins = new ArrayList();
      for (Object pluginConfig : pluginConfigs) {

        Element element = (Element) pluginConfig;
        String clazz = element.getAttribute("class");

        BeanDefinitionBuilder builder;
        try {

          builder = BeanDefinitionBuilder.rootBeanDefinition(Class.forName(clazz));
        } catch (ClassNotFoundException e) {
          throw new FatalBeanException("Unable to locate plugin class " + clazz, e);
        }

        List properties = DomUtils.getChildElementsByTagName(element, "property");
        if (!properties.isEmpty()) {
          for (Object property : properties) {
            Element aProperty = (Element) property;
            addProperty(builder, aProperty);
          }
        }
        plugins.add(builder.getBeanDefinition());

      }
      ManagedList list = new ManagedList(plugins.size());
      list.addAll(plugins);

      if (jmx != null) {
        String monitorObjectName = jmx.getAttribute("monitorObjectName");
        String mbeanServerName = jmx.getAttribute("mbeanServerName");
        String measurementDomain = jmx.getAttribute("measurementDomain");
        String overwrite = jmx.getAttribute("overwrite");

        BeanDefinitionBuilder jmxBuilder =
          BeanDefinitionBuilder.rootBeanDefinition(EtmMonitorJmxPlugin.class);
        if (monitorObjectName != null && monitorObjectName.length() > 0) {
          jmxBuilder.addPropertyValue("monitorObjectName", monitorObjectName);
        }
        if (mbeanServerName != null && mbeanServerName.length() > 0) {
          jmxBuilder.addPropertyValue("mbeanServerName", mbeanServerName);

        }
        if (measurementDomain != null && measurementDomain.length() > 0) {
          jmxBuilder.addPropertyValue("measurementDomain", measurementDomain);

        }
        if (overwrite != null && overwrite.length() > 0) {
          jmxBuilder.addPropertyValue("overwrite", overwrite);

        }
        list.add(jmxBuilder.getBeanDefinition());
      }


      aBuilder.addPropertyValue("plugins", list);

    }
  }

  private void buildChainFromChain(BeanDefinitionBuilder aBuilder, Element aAggregatorChain) {
    List chainElements = DomUtils.getChildElementsByTagName(aAggregatorChain, "chain-element");
    Element chainRoot = DomUtils.getChildElementByTagName(aAggregatorChain, "chain-root");
    BeanDefinitionBuilder chainBuilder;

    Class rootClazz;
    if (chainRoot != null) {
      String rootClassName = chainRoot.getAttribute("class");
      try {
        rootClazz = Class.forName(rootClassName);
      } catch (ClassNotFoundException e) {
        throw new FatalBeanException("Unable to locate chain root class " + rootClassName, e);
      }
    } else {
      rootClazz = RootAggregator.class;
    }

    chainBuilder = BeanDefinitionBuilder.rootBeanDefinition(rootClazz);

    if (!chainElements.isEmpty()) {
      for (int i = chainElements.size() - 1; i >= 0; i--) {
        Element chainElement = (Element) chainElements.get(i);
        String chainClassName = chainElement.getAttribute("class");

        BeanDefinitionBuilder nestedBuilder;
        try {
          nestedBuilder = BeanDefinitionBuilder.rootBeanDefinition(Class.forName(chainClassName));
        } catch (ClassNotFoundException e) {
          throw new FatalBeanException("Unable to locate chain element class " + chainClassName, e);
        }

        List propertyElements = DomUtils.getChildElementsByTagName(chainElement, "property");
        if (!propertyElements.isEmpty()) {
          for (Object propertyElement : propertyElements) {
            Element property = (Element) propertyElement;
            addProperty(nestedBuilder, property);
          }
        }

        nestedBuilder.addConstructorArg(chainBuilder.getBeanDefinition());
        chainBuilder = nestedBuilder;

      }
    }

    // register our chain
    aBuilder.addConstructorArg(chainBuilder.getBeanDefinition());
  }

  private void buildChainFromFeatures(BeanDefinitionBuilder runtimeBuilder, Element aElement) {
    Element thresholdBufferElement = DomUtils.getChildElementByTagName(aElement, "threshold-buffer");
    Element intervalBuffer = DomUtils.getChildElementByTagName(aElement, "interval-buffer");

    Element notifications = DomUtils.getChildElementByTagName(aElement, "notifications");
    Element rawDataLog = DomUtils.getChildElementByTagName(aElement, "raw-data-log");
    Element persistence = DomUtils.getChildElementByTagName(aElement, "persistence");

    BeanDefinitionBuilder notificationBuilder = null;
    BeanDefinitionBuilder bufferBuilder;
    BeanDefinitionBuilder rawDataBuilder = null;
    BeanDefinitionBuilder aggregationRootBuilder;

    if (notifications != null) {
      notificationBuilder = BeanDefinitionBuilder.rootBeanDefinition(NotifyingAggregator.class);
      String rootOnly = notifications.getAttribute("rootOnly");
      String filterPattern = notifications.getAttribute("filter-pattern");

      if ("true".equals(rootOnly)) {
        notificationBuilder.addPropertyValue("rootOnly", "true");
      }

      if (filterPattern != null && filterPattern.length() > 0) {
        notificationBuilder.addPropertyValue("filterPattern", filterPattern);
      }

    }

    if (persistence != null) {
      aggregationRootBuilder = BeanDefinitionBuilder.rootBeanDefinition(PersistentRootAggregator.class);

      Element fileBackend = DomUtils.getChildElementByTagName(persistence, "file-backend");
      Element genericBackend = DomUtils.getChildElementByTagName(persistence, "custom-backend");

      BeanDefinitionBuilder backendBuilder;

      if (fileBackend != null) {
        backendBuilder = BeanDefinitionBuilder.rootBeanDefinition(FileSystemPersistenceBackend.class);


        String file = fileBackend.getAttribute("filename");
        String path = fileBackend.getAttribute("path");
        if (file != null && file.length() > 0) {
          backendBuilder.addPropertyValue("filename", file);
        }
        if (path != null && path.length() > 0) {
          backendBuilder.addPropertyValue("path", path);
        }

      } else if (genericBackend != null) {
        String className = genericBackend.getAttribute("class");
        try {
          backendBuilder = BeanDefinitionBuilder.rootBeanDefinition(Class.forName(className));
        } catch (ClassNotFoundException e) {
          throw new FatalBeanException("Unable to locate persistence backend class " + className, e);
        }
        List properties = DomUtils.getChildElementsByTagName(genericBackend, "property");
        for (Object property : properties) {
          Element element = (Element) property;
          addProperty(backendBuilder, element);
        }
      } else {
        backendBuilder = BeanDefinitionBuilder.rootBeanDefinition(FileSystemPersistenceBackend.class);
      }

      aggregationRootBuilder.addPropertyValue("persistenceBackend", backendBuilder.getBeanDefinition());
    } else {
      aggregationRootBuilder = BeanDefinitionBuilder.rootBeanDefinition(RootAggregator.class);
    }

    if (rawDataLog != null) {
      String logType = rawDataLog.getAttribute("type");
      String logCategory = rawDataLog.getAttribute("category");
      String logFormaterClass = rawDataLog.getAttribute("formatter-class");
      String filterPattern = rawDataLog.getAttribute("filter-pattern");


      if ("log4j".equals(logType)) {
        rawDataBuilder = BeanDefinitionBuilder.rootBeanDefinition(Log4jAggregator.class);
      } else if ("commons".equals(logType)) {
        rawDataBuilder = BeanDefinitionBuilder.rootBeanDefinition(CommonsLoggingAggregator.class);
      } else if ("jdk14".equals(logType)) {
        rawDataBuilder = BeanDefinitionBuilder.rootBeanDefinition(Jdk14LogAggregator.class);
      } else {
        throw new BeanDefinitionStoreException("Raw logging type '" + logType + "' not supported");
      }

      if (logCategory != null && logCategory.length() > 0) {
        rawDataBuilder.addPropertyValue("logName", logCategory);
      }

      if (filterPattern != null && filterPattern.length() > 0) {
        rawDataBuilder.addPropertyValue("filterPattern", filterPattern);
      }

      if (logFormaterClass != null && logFormaterClass.length() > 0) {
        RootBeanDefinition definition = new RootBeanDefinition();
        definition.setBeanClassName(logFormaterClass);
        rawDataBuilder.addPropertyValue("formatter", definition);
      }
    }


    if (thresholdBufferElement != null) {
      bufferBuilder = BeanDefinitionBuilder.rootBeanDefinition(BufferedThresholdAggregator.class);
      String threshold = thresholdBufferElement.getAttribute("threshold");

      if (threshold != null && threshold.length() > 0) {
        bufferBuilder.addPropertyValue("threshold", threshold);
      }

    } else if (intervalBuffer != null) {
      bufferBuilder = BeanDefinitionBuilder.rootBeanDefinition(BufferedTimedAggregator.class);
      String interval = intervalBuffer.getAttribute("threshold");

      if (interval != null && interval.length() > 0) {
        bufferBuilder.addPropertyValue("interval", interval);
      }
    } else {
      bufferBuilder = BeanDefinitionBuilder.rootBeanDefinition(BufferedThresholdAggregator.class);
    }

    // now build up chain - reverse 
    BeanDefinitionBuilder chainBuilder = aggregationRootBuilder;

    if (rawDataBuilder != null) {
      rawDataBuilder.addConstructorArg(chainBuilder.getBeanDefinition());
      chainBuilder = rawDataBuilder;
    }

    if (notificationBuilder != null) {
      notificationBuilder.addConstructorArg(chainBuilder.getBeanDefinition());
      chainBuilder = notificationBuilder;

    }
    bufferBuilder.addConstructorArg(chainBuilder.getBeanDefinition());
    chainBuilder = bufferBuilder;

    runtimeBuilder.addConstructorArg(chainBuilder.getBeanDefinition());

  }

  private void addProperty(BeanDefinitionBuilder builder, Element aElement) {
    String name = aElement.getAttribute("name");
    String ref = aElement.getAttribute("ref");
    if (ref != null && ref.length() > 0) {
      builder.addPropertyReference(name, ref);
    } else {
      builder.addPropertyValue(name, DomUtils.getTextValue(aElement));
    }
  }

  private void addTimerDefinition(String aTimer, BeanDefinitionBuilder builder) {
    if ("jdk15".equals(aTimer)) {
      try {
        Class clazz = Class.forName("etm.core.timer.Java15NanoTimer");
        builder.addConstructorArg(clazz.newInstance());
      } catch (Exception e) {
        throw new FatalBeanException("Java15NanoTimer is not available for this platform. Please try 'sun' or " +
          "'default' instead.", e);
      }
    } else if ("sun".equals(aTimer)) {
       try {
        Class clazz = Class.forName("etm.core.timer.SunHighResTimer. Please try 'jdk15' or 'default' instead.");
        builder.addConstructorArg(clazz.newInstance());
      } catch (Exception e) {
        throw new FatalBeanException("SunHighResTimer is not available for this platform.", e);
      }
    } else if ("default".equals(aTimer)) {
      builder.addConstructorArg(new DefaultTimer());
    } else if ("bestAvailable".equals(aTimer)) {
      builder.addConstructorArg(EtmMonitorFactory.bestAvailableTimer());
    } else {
      RootBeanDefinition timerBeanDefinition = new RootBeanDefinition();
      timerBeanDefinition.setBeanClassName(aTimer);
      builder.addConstructorArg(timerBeanDefinition);
    }
  }
}