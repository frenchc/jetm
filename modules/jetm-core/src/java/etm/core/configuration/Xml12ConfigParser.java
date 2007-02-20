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
package etm.core.configuration;

import etm.core.aggregation.BufferedThresholdAggregator;
import etm.core.aggregation.BufferedTimedAggregator;
import etm.core.aggregation.NotifyingAggregator;
import etm.core.aggregation.RootAggregator;
import etm.core.aggregation.persistence.PersistentRootAggregator;
import etm.core.jmx.EtmMonitorJmxPlugin;
import etm.core.util.Log;
import etm.core.util.LogAdapter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Config parser for jetm_config_1_2.dtd configurations.
 *
 * @author void.fm
 * @version $Revision$
 * @since 1.2.0
 */
class Xml12ConfigParser extends XmlConfigParser {
  private static final LogAdapter log = Log.getLog(Xml12ConfigParser.class);

  public EtmMonitorConfig parse(Document aDocument) {
    EtmMonitorConfig monitorConfig = new EtmMonitorConfig();

    Element documentElement = aDocument.getDocumentElement();
    String attribute = getAttribute(documentElement, "autostart");
    if ("true".equals(attribute)) {
      monitorConfig.setAutostart(true);
    }

    String type = getAttribute(documentElement, "type");
    if (type != null && type.length() > 0) {
      monitorConfig.setMonitorType(type);
    }

    String timer = getAttribute(documentElement, "timer");
    if (timer != null && timer.length() > 0) {
      monitorConfig.setTimerType(timer);
    }

    NodeList features = documentElement.getElementsByTagName("features");
    NodeList aggregatorChain = documentElement.getElementsByTagName("aggregator-chain");
    if (features.getLength() != 0) {
      parseFeatures(features, monitorConfig);
    } else if (aggregatorChain.getLength() != 0) {
      parseChain(aggregatorChain, monitorConfig);
    }

    NodeList extension = documentElement.getElementsByTagName("extension");
    if (extension.getLength() != 0) {
      NodeList plugins = ((Element) extension.item(0)).getElementsByTagName("plugin");
      for (int i = 0; i < plugins.getLength(); i++) {
        Element plugin = (Element) plugins.item(i);
        EtmPluginConfig pluginConfig = extractPluginConfig(plugin);
        monitorConfig.addExtension(pluginConfig);
      }
    }

    return monitorConfig;
  }

  protected void parseFeatures(NodeList features, EtmMonitorConfig aMonitorConfig) {
    Element featureConfig = ((Element) features.item(0));

    //(threshold-buffer|interval-buffer)?, notifications?, persistence?, jmx?, raw-data-log?
    NodeList thresholdBuffer = featureConfig.getElementsByTagName("threshold-buffer");
    NodeList intervalBuffer = featureConfig.getElementsByTagName("interval-buffer");
    NodeList notifications = featureConfig.getElementsByTagName("notifications");
    NodeList persistence = featureConfig.getElementsByTagName("persistence");
    NodeList jmxConfig = featureConfig.getElementsByTagName("jmx");
    NodeList rawDataConfig = featureConfig.getElementsByTagName("raw-data-log");

    if (persistence.getLength() != 0) {
      Element element = ((Element) persistence.item(0));

      EtmAggregatorConfig aggregatorConfig = new EtmAggregatorConfig();
      aggregatorConfig.setAggregatorClass(PersistentRootAggregator.class);

      // file based backend or custom backend??
      NodeList fileBackend = element.getElementsByTagName("file-backend");
      NodeList customBackend = element.getElementsByTagName("custom-backend");
      if (fileBackend.getLength() != 0) {
        Element fileBackendElement = ((Element) fileBackend.item(0));

        addPropertyByAttribute(fileBackendElement, aggregatorConfig, "filename", "backendProperties.filename");
        addPropertyByAttribute(fileBackendElement, aggregatorConfig, "path", "backendProperties.path");

      } else if (customBackend.getLength() != 0) {
        Element customBackendElement = ((Element) customBackend.item(0));

        addPropertyByAttribute(customBackendElement, aggregatorConfig, "class", "persistenceBackendClass");

        NodeList properties = customBackendElement.getElementsByTagName("property");
        for (int j = 0; j < properties.getLength(); j++) {
          Element property = (Element) properties.item(j);
          aggregatorConfig.addProperty("backendProperties." + getAttribute(property, "name"), getNodeFirstChildTextValue(property));
        }

      }

      aMonitorConfig.setAggregatorRoot(aggregatorConfig);
    } else {
      EtmAggregatorConfig aggregatorConfig = new EtmAggregatorConfig();
      aggregatorConfig.setAggregatorClass(RootAggregator.class);
      aMonitorConfig.setAggregatorRoot(aggregatorConfig);
    }

    if (thresholdBuffer.getLength() != 0) {
      Element element = ((Element) thresholdBuffer.item(0));
      EtmAggregatorConfig aggregatorConfig = new EtmAggregatorConfig();
      aggregatorConfig.setAggregatorClass(BufferedThresholdAggregator.class);

      addPropertyByAttribute(element, aggregatorConfig, "threshold", "threshold");

      aMonitorConfig.appendAggregator(aggregatorConfig);

    } else if (intervalBuffer.getLength() != 0) {
      Element element = ((Element) thresholdBuffer.item(0));
      EtmAggregatorConfig aggregatorConfig = new EtmAggregatorConfig();
      aggregatorConfig.setAggregatorClass(BufferedTimedAggregator.class);

      addPropertyByAttribute(element, aggregatorConfig, "interval", "aggregationInterval");

      aMonitorConfig.appendAggregator(aggregatorConfig);
    }

    if (notifications.getLength() != 0) {
      if (thresholdBuffer.getLength() == 0 && intervalBuffer.getLength() == 0) {
        log.warn("Missing buffering aggregator while notifications enabled. Adding BufferedThresholdAggregator.");
        EtmAggregatorConfig aggregatorConfig = new EtmAggregatorConfig();
        aggregatorConfig.setAggregatorClass(BufferedThresholdAggregator.class);
        aMonitorConfig.appendAggregator(aggregatorConfig);
      }

      Element element = ((Element) notifications.item(0));
      EtmAggregatorConfig aggregatorConfig = new EtmAggregatorConfig();
      aggregatorConfig.setAggregatorClass(NotifyingAggregator.class);

      addPropertyByAttribute(element, aggregatorConfig, "rootOnly", "rootOnly");
      addPropertyByAttribute(element, aggregatorConfig, "filter-pattern", "filterPattern");

      aMonitorConfig.appendAggregator(aggregatorConfig);
    }

    if (rawDataConfig.getLength() != 0) {
      Element element = ((Element) rawDataConfig.item(0));
      EtmAggregatorConfig aggregatorConfig = new EtmAggregatorConfig();
      String type = element.getAttribute("type");
      // dependency to contrib!??!

      if ("log4j".equals(type)) {
        aggregatorConfig.setAggregatorClass("etm.contrib.aggregation.log.Log4jAggregator");
      } else if ("jdk14".equals(type)) {
        aggregatorConfig.setAggregatorClass("etm.contrib.aggregation.log.Jdk14LogAggregator");
      } else if ("commons".equals(type)) {
        aggregatorConfig.setAggregatorClass("etm.contrib.aggregation.log.CommonsLoggingAggregator");
      }

      addPropertyByAttribute(element, aggregatorConfig, "category", "logName");
      addPropertyByAttribute(element, aggregatorConfig, "formatter-class", "formatterClass");
      addPropertyByAttribute(element, aggregatorConfig, "filter-pattern", "filterPattern");

      aMonitorConfig.appendAggregator(aggregatorConfig);
    }

    // plugins
    if (jmxConfig.getLength() != 0) {
      Element jmxConfigElement = ((Element) jmxConfig.item(0));
      EtmPluginConfig config = new EtmPluginConfig();
      config.setPluginClass(EtmMonitorJmxPlugin.class);

      addPropertyByAttribute(jmxConfigElement, config, "mbeanServerName", "mbeanServerName");
      addPropertyByAttribute(jmxConfigElement, config, "monitorObjectName", "monitorObjectName");
      addPropertyByAttribute(jmxConfigElement, config, "measurementDomain", "measurementDomain");
      addPropertyByAttribute(jmxConfigElement, config, "overwrite", "overwrite");

      aMonitorConfig.addExtension(config);
    }


  }


  protected void parseChain(NodeList aAggregatorChain, EtmMonitorConfig aMonitorConfig) {
    Element element = ((Element) aAggregatorChain.item(0));
    NodeList aggregatorRoot = element.getElementsByTagName("chain-root");
    if (aggregatorRoot.getLength() == 0) {
      EtmAggregatorConfig aggregatorConfig = new EtmAggregatorConfig();
      aggregatorConfig.setAggregatorClass(RootAggregator.class);
      aMonitorConfig.setAggregatorRoot(aggregatorConfig);
    } else {
      EtmAggregatorConfig rootConfig = extractAggregatorConfig((Element) aggregatorRoot.item(0));
      aMonitorConfig.setAggregatorRoot(rootConfig);
    }

    NodeList aggregators = element.getElementsByTagName("chain-element");
    for (int i = 0; i < aggregators.getLength(); i++) {
      Element aggregator = (Element) aggregators.item(i);
      EtmAggregatorConfig aggregatorConfig = extractAggregatorConfig(aggregator);
      aMonitorConfig.appendAggregator(aggregatorConfig);
    }
  }


  protected EtmPluginConfig extractPluginConfig(Element aPlugin) {
    EtmPluginConfig pluginConfig = new EtmPluginConfig();

    String pluginClass = aPlugin.getAttribute("class");
    if (pluginClass != null && pluginClass.length() > 0) {
      pluginConfig.setPluginClass(pluginClass);
    } else {
      throw new EtmConfigurationException("No valid plugin class found");
    }

    NodeList properties = aPlugin.getElementsByTagName("property");
    for (int j = 0; j < properties.getLength(); j++) {
      Element property = (Element) properties.item(j);
      pluginConfig.addProperty(getAttribute(property, "name"), getNodeFirstChildTextValue(property));
    }

    return pluginConfig;
  }

  protected EtmAggregatorConfig extractAggregatorConfig(Element aAggregator) {
    EtmAggregatorConfig aggregatorConfig = new EtmAggregatorConfig();
    String aggregatorClass = aAggregator.getAttribute("class");
    if (aggregatorClass != null && aggregatorClass.length() > 0) {
      aggregatorConfig.setAggregatorClass(aggregatorClass);
    } else {
      throw new EtmConfigurationException("No valid aggregator class in cahin element found");
    }

    NodeList properties = aAggregator.getElementsByTagName("property");
    for (int j = 0; j < properties.getLength(); j++) {
      Element property = (Element) properties.item(j);
      aggregatorConfig.addProperty(getAttribute(property, "name"), getNodeFirstChildTextValue(property));
    }
    return aggregatorConfig;
  }


}
