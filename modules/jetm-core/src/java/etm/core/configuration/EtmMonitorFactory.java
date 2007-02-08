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

import etm.core.aggregation.Aggregator;
import etm.core.aggregation.BufferedThresholdAggregator;
import etm.core.monitor.EtmMonitor;
import etm.core.plugin.EtmPlugin;
import etm.core.timer.DefaultTimer;
import etm.core.timer.ExecutionTimer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Factory to create an EtmMonitor based on configuration.
 *
 * @author void.fm
 * @version $Revision$
 */

public class  EtmMonitorFactory {

  private static final String[] jetmTimer = new String[]{
    "etm.core.timer.Java15NanoTimer",
    "etm.core.timer.SunHighResTimer",
    "etm.core.timer.DefaultTimer"
  };


  public static EtmMonitor createEtmMonitor(EtmMonitorConfig monitorConfig) throws Exception {
    Object obj;
    try {
      Constructor constructor = monitorConfig.getMonitorClass().getConstructor(new Class[]{ExecutionTimer.class, Aggregator.class});
      obj = constructor.newInstance(new Object[]{createTimer(monitorConfig), createAggregators(monitorConfig)});
    } catch (NoSuchMethodException e) {
      try {
        Constructor constructor = monitorConfig.getMonitorClass().getConstructor(new Class[]{Aggregator.class});
        obj = constructor.newInstance(new Object[]{createAggregators(monitorConfig)});

      } catch (NoSuchMethodException e1) {
        try {
          Constructor constructor = monitorConfig.getMonitorClass().getConstructor(new Class[]{ExecutionTimer.class});
          obj = constructor.newInstance(new Object[]{createTimer(monitorConfig)});

        } catch (NoSuchMethodException e2) {
          obj = monitorConfig.getMonitorClass().newInstance();
        }
      }
    }

    EtmMonitor etmMonitor = (EtmMonitor) obj;
    List pluginConfig = monitorConfig.getPluginConfig();
    if (pluginConfig != null) {
      addPlugins(etmMonitor, pluginConfig);
    }

    if (monitorConfig.isAutostart()) {
      etmMonitor.start();
      // todo maybe we should add a configuration property for
      // shutdown hook too
      Runtime.getRuntime().addShutdownHook(new ShutDownHook(etmMonitor));
    }
    return etmMonitor;
  }


  public static ExecutionTimer newTimer() {
    for (int i = 0; i < jetmTimer.length; i++) {
      try {
        return (ExecutionTimer) instantiateClass(jetmTimer[i]);
      } catch (Exception e) {
        System.err.println("Unable to instantiate execution timer '" + jetmTimer[i] + "'. Trying next. " + e);
      } catch (Throwable e) {
        // for our implementation we get a NoSuchMethodError for JDK's < 5.0
        // therefore ignore, unless it's ThreadDeath
        if (e instanceof ThreadDeath) {
          throw (ThreadDeath) e;
        }
      }
    }

    return new DefaultTimer();
  }

  private static ExecutionTimer createTimer(EtmMonitorConfig monitorConfig) throws IllegalAccessException, InstantiationException {
    if (monitorConfig.getTimerClass() != null) {
      return (ExecutionTimer) monitorConfig.getTimerClass().newInstance();
    } else {
      return newTimer();
    }
  }

  private static Aggregator createAggregators(EtmMonitorConfig monitorConfig) throws IllegalAccessException, InstantiationException, InvocationTargetException, ClassNotFoundException {
    if (monitorConfig.getAggregatorRoot() != null) {

      Aggregator current = (Aggregator) monitorConfig.getAggregatorRoot().getAggregatorClass().newInstance();
      Map properties = monitorConfig.getAggregatorRoot().getProperties();
      if (properties != null) {
        setProperties(current, properties);
      }

      List config = monitorConfig.getEtmAggregators();
      if (config != null) {
        for (int i = config.size() - 1; i >= 0; i--) {
          EtmAggregatorConfig etmAggregator = (EtmAggregatorConfig) config.get(i);
          try {
            Constructor constructor = etmAggregator.getAggregatorClass().getConstructor(new Class[]{Aggregator.class});
            current = (Aggregator) constructor.newInstance(new Object[]{current});
          } catch (NoSuchMethodException e) {
            throw new EtmConfigurationException("Nested aggregator does not have an constructor with type Aggregator.");
          }

          properties = etmAggregator.getProperties();
          if (properties != null) {
            setProperties(current, properties);
          }
        }
      } else {
        // always add buffering to aggregators if root aggregator does not buffer
        if (!current.getMetaData().isBuffering()) {
          current = new BufferedThresholdAggregator(current);
        }
      }
      return current;
    } else {
      return null;
    }
  }

  private static void addPlugins(EtmMonitor aEtmMonitor, List aPluginConfig) throws IllegalAccessException, InstantiationException, InvocationTargetException, ClassNotFoundException {
    for (int i = 0; i < aPluginConfig.size(); i++) {
      EtmPluginConfig etmPluginConfig = (EtmPluginConfig) aPluginConfig.get(i);
      Object obj = etmPluginConfig.getPluginClass().newInstance();
      if (etmPluginConfig.getProperties() != null) {
        setProperties(obj, etmPluginConfig.getProperties());
      }
      aEtmMonitor.addPlugin((EtmPlugin) obj);
    }

  }


  private static void setProperties(Object obj, Map properties) throws IllegalAccessException, InvocationTargetException, ClassNotFoundException {
    // todo just improve  ;)
    Method[] methods = obj.getClass().getMethods();
    for (int i = 0; i < methods.length; i++) {
      Method method = methods[i];
      String methodName = method.getName();
      if (methodName.startsWith("set") && methodName.length() >= 4) {
        String propertyName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
        if (properties.containsKey(propertyName) && method.getParameterTypes().length == 1) {
          Object value = properties.get(propertyName);

          Class clazz = method.getParameterTypes()[0];

          if (int.class.isAssignableFrom(clazz)) {
            method.invoke(obj, new Object[]{new Integer(Integer.parseInt((String) value))});
          } else if (long.class.isAssignableFrom(clazz)) {
            method.invoke(obj, new Object[]{new Long(Long.parseLong((String) value))});
          } else if (boolean.class.isAssignableFrom(clazz)) {
            if ("true".equals(value)) {
              method.invoke(obj, new Object[]{Boolean.TRUE});
            } else if ("false".equals(value)) {
              method.invoke(obj, new Object[]{Boolean.FALSE});
            }
          } else if (String.class.isAssignableFrom(clazz)) {
            method.invoke(obj, new Object[]{value});
          } else if (Class.class.isAssignableFrom(clazz)) {
            method.invoke(obj, new Object[]{Class.forName((String) value)});
          } else if (Map.class.isAssignableFrom(clazz)) {
            if (value instanceof Map) {
              method.invoke(obj, new Object[]{value});
            }
          } else if (List.class.isAssignableFrom(clazz)) {
            if (value instanceof List) {
              method.invoke(obj, new Object[]{value});
            }
          }
        }
      }
    }
  }

  private static Object instantiateClass(String className) throws Exception {
    Class clazz = Class.forName(className);
    return clazz.newInstance();
  }

  private static class ShutDownHook extends Thread {
    private final EtmMonitor etmMonitor;

    public ShutDownHook(EtmMonitor aEtmMonitor) {
      etmMonitor = aEtmMonitor;
    }

    public void run() {
      if (etmMonitor.isStarted()) {
        etmMonitor.stop();
      }
    }
  }
}

