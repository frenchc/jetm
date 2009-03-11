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

import etm.core.monitor.EtmMonitor;
import etm.core.monitor.FlatMonitor;
import etm.core.monitor.NestedMonitor;
import etm.core.monitor.NullMonitor;
import etm.core.timer.DefaultTimer;
import etm.core.timer.ExecutionTimer;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent the configuration for an EtmMonitor instance.
 *
 * @author void.fm
 * @version $Revision$
 */
public class EtmMonitorConfig {
  private boolean autostart;
  private Class monitorClass = NestedMonitor.class;
  private Class timerClass;

  private EtmAggregatorConfig aggregatorRoot;
  private List etmAggregators;

  private List pluginConfig;


  public boolean isAutostart() {
    return autostart;
  }

  public void setAutostart(boolean aAutostart) {
    autostart = aAutostart;
  }

  /**
   * Returns the monitor class. Defaults to {@link NestedMonitor}.
   *
   * @return The monitor class.
   */
  public Class getMonitorClass() {
    return monitorClass;
  }

  /**
   * Returns the timer class. May return null.
   *
   * @return The timer class or null.
   */
  public Class getTimerClass() {
    return timerClass;
  }

  /**
   * Returns a list of {@link EtmAggregatorConfig} elements is descending
   * order (first chain element will be first).
   *
   * @return A list of elements or null.
   * @throws EtmConfigurationException Thrown in case there is a list of aggregators but no root aggregator.
   */

  public List getEtmAggregators() {
    if (etmAggregators != null && aggregatorRoot == null) {
      throw new EtmConfigurationException("Aggregator root not set.");
    }
    return etmAggregators;
  }

  /**
   * Returns the root aggregator config.
   *
   * @return The aggregator root config. May be null.
   */
  public EtmAggregatorConfig getAggregatorRoot() {
    return aggregatorRoot;
  }

  /**
   * Returns a list of EtmPluginConfig or null
   *
   * @return The plugin config.
   */
  public List getPluginConfig() {
    return pluginConfig;
  }

  /**
   * Sets the ExecutionMonitor type. Valid values are
   * <code>flat</code> ({@link FlatMonitor}), <code>nested</code> ({@link NestedMonitor})
   * and <code>null</code> ({@link NullMonitor}) or EtmMonitor classname.
   *
   * @param monitorType The type of the EtmMonitor.
   * @throws EtmConfigurationException Thrown to indicate that given monitor type is not supported.
   */

  public void setMonitorType(String monitorType) {
    if ("flat".equalsIgnoreCase(monitorType)) {
      monitorClass = FlatMonitor.class;
    } else if ("nested".equalsIgnoreCase(monitorType)) {
      monitorClass = NestedMonitor.class;
    } else if ("null".equalsIgnoreCase(monitorType)) {
      monitorClass = NullMonitor.class;
    } else {
      Class clazz;
      try {
        clazz = Class.forName(monitorType);
      } catch (ClassNotFoundException e) {
        throw new EtmConfigurationException("Unsupported monitor type or invalid monitor class " + monitorType + ".");
      }
      if (EtmMonitor.class.isAssignableFrom(clazz)) {
        monitorClass = clazz;
      } else {
        throw new EtmConfigurationException("Class " + monitorClass + " is not a valid EtmMonitor implementation.");
      }
    }
  }

  /**
   * Sets the timer type for the monitor. Supported values are
   * <code>default</code> ({@link DefaultTimer}), <code>sun</code> ({@link etm.core.timer.SunHighResTimer})
   * and <code>jdk50</code> ({@link etm.core.timer.Java15NanoTimer}) or valid Timer class name.
   *
   * @param timerType The timer type name.
   * @throws EtmConfigurationException Thrown to indicate that the given configuration is invalid or not supported
   *                                   for this runtime.
   */
  public void setTimerType(String timerType) {
    if ("default".equalsIgnoreCase(timerType)) {
      timerClass = DefaultTimer.class;
    } else if ("sun".equalsIgnoreCase(timerType)) {
      try {
        timerClass = Class.forName("etm.core.timer.SunHighResTimer");
      } catch (ClassNotFoundException e) {
        throw new EtmConfigurationException("Sun HigRes Timer not available.");
      }
    } else if ("jdk50".equalsIgnoreCase(timerType)) {
      try {
        timerClass = Class.forName("etm.core.timer.Java15NanoTimer");
      } catch (ClassNotFoundException e) {
        throw new EtmConfigurationException("Java 5.0 Nano Timer not available.");
      }
    } else if ("bestAvailable".equalsIgnoreCase(timerType)) {
      timerClass = EtmMonitorFactory.bestAvailableTimer().getClass();
    } else {
      Class clazz;
      try {
        clazz = Class.forName(timerType);
      } catch (ClassNotFoundException e) {
        throw new EtmConfigurationException("Unsupported timer type or invalid timer class " + timerClass + " for timer type " + timerType + ".");
      }
      if (ExecutionTimer.class.isAssignableFrom(clazz)) {
        this.timerClass = clazz;
      } else {
        throw new EtmConfigurationException("Class " + timerClass + " is not a valid ExecutionTimer implementation.");
      }
    }
  }

  /**
   * Appends a given config at the end of the currently existing
   * Aggregator configurations.
   *
   * @param aAggregatorConfig The AggregatorConfig to append.
   */
  public void appendAggregator(EtmAggregatorConfig aAggregatorConfig) {
    if (etmAggregators == null) {
      etmAggregators = new ArrayList();
    }
    etmAggregators.add(aAggregatorConfig);
  }

  /**
   * Sets the aggregator root config, which is the aggregator that is called
   * after a potentially exisiting list of aggregators has processed raw
   * performance results.
   *
   * @param aRootConfig The root config.
   */

  public void setAggregatorRoot(EtmAggregatorConfig aRootConfig) {
    aggregatorRoot = aRootConfig;
  }

  /**
   * Appends a new plugin config.
   *
   * @param aPluginConfig The plugin config.
   */
  public void addExtension(EtmPluginConfig aPluginConfig) {
    if (pluginConfig == null) {
      pluginConfig = new ArrayList();
    }
    pluginConfig.add(aPluginConfig);
  }
}
