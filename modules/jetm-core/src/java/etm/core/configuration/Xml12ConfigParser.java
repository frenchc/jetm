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

import etm.core.aggregation.RootAggregator;
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

    NodeList aggregatorChain = documentElement.getElementsByTagName("aggregator-chain");
    if (aggregatorChain.getLength() != 0) {
      Element element = ((Element) aggregatorChain.item(0));
      NodeList aggregatorRoot = element.getElementsByTagName("chain-root");
      if (aggregatorRoot.getLength() == 0) {
        EtmAggregatorConfig aggregatorConfig = new EtmAggregatorConfig();
        aggregatorConfig.setAggregatorClass(RootAggregator.class);
        monitorConfig.setAggregatorRoot(aggregatorConfig);
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
