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
package etm.core.jmx;

import etm.core.aggregation.Aggregate;
import etm.core.aggregation.persistence.PersistentEtmState;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.event.AggregationListener;
import etm.core.monitor.event.AggregationStateListener;
import etm.core.monitor.event.AggregationStateLoadedEvent;
import etm.core.monitor.event.MonitorResetEvent;
import etm.core.monitor.event.RootCreateEvent;
import etm.core.monitor.event.RootResetEvent;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Simple base class that takes care of dynamic etm point registration.
 *
 * @author void.fm
 * @version $Revision$
 * @since 1.2.0
 */
public class AbstractJmxRegistry implements AggregationStateListener, AggregationListener {
  public static final String DEFAULT_ETM_MONITOR_OBJECT_NAME = "etm.monitor:service=PerformanceMonitor";
  public static final String DEFAULT_ETM_POINT_OBJECT_NAME_PREFIX = "etm.performance";

  protected String etmMonitorObjectName = DEFAULT_ETM_MONITOR_OBJECT_NAME;
  protected String etmMeasurementObjectNamePrefix = DEFAULT_ETM_POINT_OBJECT_NAME_PREFIX;
  // default mbeanservername is null
  protected String mbeanServerName;

  protected boolean overwriteRegistered = true;


  protected MBeanServer mbeanServer;
  protected EtmMonitor etmMonitor;

  protected boolean isStopping = false;

  /**
   * Sets the name of the MBeanServer to use. Default is <code>null</code>.
   *
   * @param aMbeanServerName The name.
   */
  public void setMbeanServerName(String aMbeanServerName) {
    mbeanServerName = aMbeanServerName;
  }

  /**
   * Sets the name to be used for Monitor registration. Default is
   * {@link EtmMonitorJmxPlugin#DEFAULT_ETM_MONITOR_OBJECT_NAME}
   *
   * @param aEtmMonitorObjectName The JMX Object name to be used for registration.
   */
  public void setEtmMonitorObjectName(String aEtmMonitorObjectName) {
    if (aEtmMonitorObjectName == null || aEtmMonitorObjectName.trim().length() == 0) {
      throw new IllegalArgumentException("ObjectName for EtmMonitor may not be null or empty ");
    }
    etmMonitorObjectName = aEtmMonitorObjectName;
  }

  public void setEtmMeasurementObjectNamePrefix(String aEtmMeasurementObjectNamePrefix) {
    etmMeasurementObjectNamePrefix = aEtmMeasurementObjectNamePrefix;
  }

  /**
   * If a MBean is found under the given name the
   * existing instance will be overwritten. This behavior
   * is enabled by default.
   *
   * @param flag True to overwrite, otherwhise false.
   */

  public void setOverwriteRegistered(boolean flag) {
    overwriteRegistered = flag;
  }

  public void start() {
    try {
      ArrayList mbeanServers = MBeanServerFactory.findMBeanServer(mbeanServerName);
      mbeanServer = (MBeanServer) mbeanServers.get(0);
      if (mbeanServer != null) {
        ObjectName objectName = new ObjectName(etmMonitorObjectName);
        registerMBean(new EtmMonitorMBean(etmMonitor), objectName);
      } else {
        System.err.println("Unable to locate a valid MBeanServer. ");
      }
    } catch (Exception e) {
      //???
      e.printStackTrace();
    }

    isStopping = false;
  }

  public void stop() {
    isStopping = true;

    if (mbeanServer != null) {
      try {
        deregisterPerformanceResults();
        mbeanServer.unregisterMBean(new ObjectName(etmMonitorObjectName));
      } catch (Exception e) {
        // ???
        e.printStackTrace();
      }
    }
  }


  public void onStateLoaded(AggregationStateLoadedEvent event) {
    if (isStopping) {
      return;
    }

    PersistentEtmState persistentEtmState = event.getState();
    Map aggregates = persistentEtmState.getAggregates();
    for (Iterator iterator = aggregates.values().iterator(); iterator.hasNext();) {
      Aggregate aggregate = (Aggregate) iterator.next();
      registerAggregate(aggregate);
    }
  }

  public void onRootCreate(RootCreateEvent event) {
    if (isStopping) {
      return;
    }

    Aggregate aggregate = event.getAggregate();
    registerAggregate(aggregate);
  }

  public void onStateReset(MonitorResetEvent event) {
    if (isStopping) {
      return;
    }
    try {
      deregisterPerformanceResults();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void onRootReset(RootResetEvent event) {
    // ignore
  }

  protected void registerMBean(Object object, ObjectName objectName) throws Exception {
    try {
      mbeanServer.registerMBean(object, objectName);
    } catch (InstanceAlreadyExistsException e) {
      if (overwriteRegistered) {
        mbeanServer.unregisterMBean(objectName);
        mbeanServer.registerMBean(object, objectName);
      } else {
        System.err.println("Error registering EtmMonitor MBean. An instance exists for name " + etmMonitorObjectName);
      }
    }
  }

  protected void registerAggregate(Aggregate aAggregate) {
    String name = calculateJmxName(aAggregate);

    Hashtable map = new Hashtable();
    map.put("type", "Measurement");
    map.put("name", name);
    try {
      ObjectName objectName = new ObjectName(etmMeasurementObjectNamePrefix, map);
      registerMBean(new EtmPointMBean(etmMonitor, aAggregate), objectName);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  protected void deregisterPerformanceResults() throws MalformedObjectNameException, InstanceNotFoundException, MBeanRegistrationException {
    ObjectName objectName = new ObjectName(etmMeasurementObjectNamePrefix+":*");
    Set set = mbeanServer.queryNames(objectName,  null);
    for (Iterator iterator = set.iterator(); iterator.hasNext();) {
      ObjectName o = (ObjectName) iterator.next();
      mbeanServer.unregisterMBean(o);
    }
  }

  protected String calculateJmxName(Aggregate aAggregate) {
    char[] chars = aAggregate.getName().toCharArray();

    for (int i = 0; i < chars.length; i++) {
      switch (chars[i]) {
        case':':
        case',':
        case';':
          chars[i] = '_';
        default:
          // don't do anything
      }
    }
    return new String(chars);
  }


}
