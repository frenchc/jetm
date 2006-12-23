/*
 *
 * Copyright (c) 2004, 2005, 2006 void.fm
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

import etm.core.monitor.EtmMonitor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Xml based configurator. Requires a xml configuration which is valid
 * to <code>jetm_config_1_0.dtd</code>.
 * <p/>
 * Be aware that you need to start and stop the EtmMonitor before
 * using it. See {@link etm.core.monitor.EtmMonitor} lifecycle.
 *
 * @author void.fm
 * @version $Id: XmlEtmConfigurator.java,v 1.9 2006/12/11 15:10:32 french_c Exp $
 */
public class XmlEtmConfigurator {
  public static final String PUBLIC_DTD_ID = "-// void.fm //DTD JETM Config 1.0//EN";
  public static final String JETM_CONFIG_1_0_DTD_NAME = "jetm_config_1_0.dtd";


  private XmlEtmConfigurator() {
  }

  /**
   * Configures the EtmManager using the given string which represents
   * a valid XmlEtmConfigurator configuration.
   *
   * @param config The xml configuration string.
   * @throws EtmConfigurationException Thrown in case an error occures.
   */
  public static void configure(String config) {
    InputStream inStream = null;
    try {
      inStream = new ByteArrayInputStream(config.getBytes());
      Document doc = parse(inStream);
      loadConfig(doc);
    } catch (EtmConfigurationException e) {
      throw e;
    } catch (Exception e) {
      throw new EtmConfigurationException(e);
    } finally {
      if (inStream != null) {
        try {
          inStream.close();
        } catch (IOException e) {
          // ignored
        }
      }
    }
  }

  /**
   * Configures the EtmManager using the given URL.
   *
   * @param configLocation The location of the file which may be remote or locally.
   * @throws EtmConfigurationException Thrown in case an error occures.
   */
  public static void configure(URL configLocation) {
    InputStream inStream = null;

    try {
      inStream = configLocation.openStream();
      Document doc = parse(inStream);
      loadConfig(doc);
    } catch (EtmConfigurationException e) {
      throw e;
    } catch (Exception e) {
      throw new EtmConfigurationException(e);
    } finally {
      if (inStream != null) {
        try {
          inStream.close();
        } catch (IOException e) {
          // ignored
        }
      }
    }
  }

  /**
   * Configures the EtmManager using the given inputStream.
   * The stream will not be closed after usage.
   *
   * @param in The inputStream to be used.
   * @throws EtmConfigurationException Thrown in case an error occures.
   */
  public static void configure(InputStream in) {
    try {
      Document doc = parse(in);
      loadConfig(doc);
    } catch (EtmConfigurationException e) {
      throw e;
    } catch (Exception e) {
      throw new EtmConfigurationException(e);
    }
  }

  /**
   * Configures the EtmManager using the given file. Delegates to
   * {@link #configure(java.net.URL)} only.
   *
   * @param file The config filefile to be used.
   * @throws EtmConfigurationException Thrown in case an error occures.
   */
  public static void configure(File file) {
    try {
      configure(file.toURL());
    } catch (MalformedURLException e) {
      throw new EtmConfigurationException(e);
    }
  }

  private static Document parse(InputStream inStream) throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder = factory.newDocumentBuilder();
    documentBuilder.setEntityResolver(new EntityResolver() {
      public InputSource resolveEntity(String string, String string1) throws SAXException {
        if (PUBLIC_DTD_ID.equals(string)) {
          return new InputSource(Thread.currentThread().getContextClassLoader().getResourceAsStream(JETM_CONFIG_1_0_DTD_NAME));
        }
        throw new SAXException("Unsupported entity " + string);
      }
    });

    return documentBuilder.parse(inStream);

  }

  private static void loadConfig(Document document) throws Exception {
    EtmMonitorConfig monitorConfig = new EtmMonitorConfig();

    Element documentElement = document.getDocumentElement();
    String attribute = documentElement.getAttribute("autostart");
    if ("true".equals(attribute)) {
      monitorConfig.setAutostart(true);
    }
    NodeList monitorTypes = documentElement.getElementsByTagName("monitor-type");
    if (monitorTypes.getLength() != 0) {
      Node node = monitorTypes.item(0);
      monitorConfig.setMonitorType(node.getFirstChild().getNodeValue());
    } else {
      NodeList monitorClasses = documentElement.getElementsByTagName("monitor-class");
      if (monitorClasses.getLength() != 0) {
        Node node = monitorClasses.item(0);
        monitorConfig.setMonitorClass(node.getFirstChild().getNodeValue());
      }
    }

    NodeList timerTypes = documentElement.getElementsByTagName("timer-type");
    if (timerTypes.getLength() != 0) {
      Node node = timerTypes.item(0);
      monitorConfig.setTimerType(node.getFirstChild().getNodeValue());
    } else {
      NodeList timerClasses = documentElement.getElementsByTagName("timer-class");
      if (timerClasses.getLength() != 0) {
        Node node = timerClasses.item(0);
        monitorConfig.setTimerClass(node.getFirstChild().getNodeValue());
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
    EtmMonitor etmMonitor = EtmMonitorFactory.createEtmMonitor(monitorConfig);
    EtmManager.configure(etmMonitor);
  }

  private static EtmPluginConfig extractPluginConfig(Element aPlugin) {
    EtmPluginConfig pluginConfig = new EtmPluginConfig();

    NodeList pluginClasses = aPlugin.getElementsByTagName("plugin-class");
    if (pluginClasses.getLength() != 0) {
      Node node = pluginClasses.item(0);
      pluginConfig.setPluginClass(node.getFirstChild().getNodeValue());
    } else {
      throw new EtmConfigurationException("No valid plugin class found");
    }

    NodeList properties = aPlugin.getElementsByTagName("property");
    for (int j = 0; j < properties.getLength(); j++) {
      Element property = (Element) properties.item(j);
      pluginConfig.addProperty(property.getAttribute("name"), property.getFirstChild().getNodeValue());
    }

    return pluginConfig;
  }

  private static EtmAggregatorConfig extractAggregatorConfig(Element aAggregator) {
    EtmAggregatorConfig aggregatorConfig = new EtmAggregatorConfig();
    NodeList aggregatorClasses = aAggregator.getElementsByTagName("aggregator-class");
    if (aggregatorClasses.getLength() != 0) {
      Node node = aggregatorClasses.item(0);
      aggregatorConfig.setAggregatorClass(node.getFirstChild().getNodeValue());
    } else {
      throw new EtmConfigurationException("No valid aggregator class found");
    }

    NodeList properties = aAggregator.getElementsByTagName("property");
    for (int j = 0; j < properties.getLength(); j++) {
      Element property = (Element) properties.item(j);
      aggregatorConfig.addProperty(property.getAttribute("name"), property.getFirstChild().getNodeValue());
    }
    return aggregatorConfig;
  }


}
