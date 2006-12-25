package etm.core.configuration;

import etm.core.aggregation.Aggregator;
import etm.core.plugin.EtmPlugin;

import java.util.Map;
import java.util.HashMap;

/**
 *
 * Configuration for an EtmPlugin
 *
 * @version $Id$
 * @author void.fm
 *
 */
public class EtmPluginConfig {
  private Class pluginClass;
  private Map properties;

  public Class getPluginClass() {
    return pluginClass;
  }

  public Map getProperties() {
    return properties;
  }

  public void setPluginClass(String pluginClassName) {
    Class clazz;
    try {
      clazz = Class.forName(pluginClassName);
    } catch (ClassNotFoundException e) {
      throw new EtmConfigurationException("Plugin class " + pluginClassName + " not found.");
    }
    if (EtmPlugin.class.isAssignableFrom(clazz)) {
      pluginClass = clazz;
    } else {
      throw new EtmConfigurationException("Class " + pluginClassName + " is not a valid plugin implementation.");
    }
  }

  public void addProperty(String propertyName, String propertyValue) {
    if (properties == null) {
      properties = new HashMap();
    }
    properties.put(propertyName, propertyValue);
  }
}
