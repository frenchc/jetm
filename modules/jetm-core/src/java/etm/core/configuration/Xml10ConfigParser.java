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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * Config parser for jetm_config_1_0.dtd configurations.
 *
 * @version $Revision$
 * @author void.fm
 */
class Xml10ConfigParser extends XmlConfigParser {

  public EtmMonitorConfig parse(Document aDocument) {
    EtmMonitorConfig monitorConfig = new EtmMonitorConfig();

    Element documentElement = aDocument.getDocumentElement();
    String attribute = getAttribute(documentElement, "autostart");
    if ("true".equals(attribute)) {
      monitorConfig.setAutostart(true);
    }
    NodeList monitorTypes = documentElement.getElementsByTagName("monitor-type");
    if (monitorTypes.getLength() != 0) {
      Node node = monitorTypes.item(0);
      monitorConfig.setMonitorType(getNodeFirstChildTextValue(node));
    } else {
      NodeList monitorClasses = documentElement.getElementsByTagName("monitor-class");
      if (monitorClasses.getLength() != 0) {
        Node node = monitorClasses.item(0);
        monitorConfig.setMonitorClass(getNodeFirstChildTextValue(node));
      }
    }

    NodeList timerTypes = documentElement.getElementsByTagName("timer-type");
    if (timerTypes.getLength() != 0) {
      Node node = timerTypes.item(0);
      monitorConfig.setTimerType(getNodeFirstChildTextValue(node));
    } else {
      NodeList timerClasses = documentElement.getElementsByTagName("timer-class");
      if (timerClasses.getLength() != 0) {
        Node node = timerClasses.item(0);
        monitorConfig.setTimerClass(getNodeFirstChildTextValue(node));
      }
    }


    NodeList aggregatorChain = documentElement.getElementsByTagName("aggregator-chain");
    if (aggregatorChain.getLength() != 0) {
      Element element = ((Element) aggregatorChain.item(0));
      NodeList aggregatorRoot = element.getElementsByTagName("chain-root");
      if (aggregatorRoot.getLength() == 0) {
        throw new EtmConfigurationException("At least one aggregator-root element has to be specified");
      } else {
        EtmAggregatorConfig rootConfig = extractAggregatorConfig((Element) aggregatorRoot.item(0));
        monitorConfig.setAggregatorRoot(rootConfig);
      }

      NodeList aggregators = documentElement.getElementsByTagName("chain-element");
      for (int i = 0; i < aggregators.getLength(); i++) {
        Element aggregator = (Element) aggregators.item(i);
        EtmAggregatorConfig aggregatorConfig = extractAggregatorConfig(aggregator);
        monitorConfig.appendAggregator(aggregatorConfig);
      }
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

  private EtmPluginConfig extractPluginConfig(Element aPlugin) {
    EtmPluginConfig pluginConfig = new EtmPluginConfig();

    NodeList pluginClasses = aPlugin.getElementsByTagName("plugin-class");
    if (pluginClasses.getLength() != 0) {
      Node node = pluginClasses.item(0);
      pluginConfig.setPluginClass(getNodeFirstChildTextValue(node));
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

  private EtmAggregatorConfig extractAggregatorConfig(Element aAggregator) {
    EtmAggregatorConfig aggregatorConfig = new EtmAggregatorConfig();
    NodeList aggregatorClasses = aAggregator.getElementsByTagName("aggregator-class");
    if (aggregatorClasses.getLength() != 0) {
      Node node = aggregatorClasses.item(0);
      aggregatorConfig.setAggregatorClass(getNodeFirstChildTextValue(node));
    } else {
      throw new EtmConfigurationException("No valid aggregator class found");
    }

    NodeList properties = aAggregator.getElementsByTagName("property");
    for (int j = 0; j < properties.getLength(); j++) {
      Element property = (Element) properties.item(j);
      aggregatorConfig.addProperty(getAttribute(property, "name"), getNodeFirstChildTextValue(property));
    }
    return aggregatorConfig;
  }

  private String getAttribute(Element element, String attributeName) {
    String attribute = element.getAttribute(attributeName);
    if (attribute != null) {
      return attribute.trim();
    }

    return attribute;
  }

  private String getNodeFirstChildTextValue(Node aNode) {
    String nodeValue = aNode.getFirstChild().getNodeValue();
    if (nodeValue != null) {
      return nodeValue.trim();
    }
    return nodeValue;
  }
}
