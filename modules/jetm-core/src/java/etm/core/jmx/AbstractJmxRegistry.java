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
import etm.core.monitor.event.PreMonitorResetEvent;
import etm.core.monitor.event.PreRootResetEvent;
import etm.core.monitor.event.RootCreateEvent;
import etm.core.monitor.event.RootResetEvent;
import etm.core.util.Log;
import etm.core.util.LogAdapter;

import javax.management.InstanceNotFoundException;
import javax.management.JMException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * JMX base class. Registers an EtmMonitor on startup and performance
 * measurements mbeans on demand.
 * <p/>
 * By default the monitor will be available at
 * {@link AbstractJmxRegistry#DEFAULT_ETM_MONITOR_OBJECT_NAME} and register
 * performance details in the domain {@link etm.core.jmx.AbstractJmxRegistry#DEFAULT_ETM_POINT_DOMAIN}.
 * You may disable this behavior by setting {@link #setOverwrite(boolean)} to true.
 *
 * @author void.fm
 * @version $Revision$
 * @since 1.2.0
 */
public class AbstractJmxRegistry extends JmxSupport implements AggregationStateListener, AggregationListener {

  private static final LogAdapter log = Log.getLog(AbstractJmxRegistry.class);


  public static final String DEFAULT_ETM_MONITOR_OBJECT_NAME = "etm.monitor:service=PerformanceMonitor";
  public static final String DEFAULT_ETM_POINT_DOMAIN = "etm.performance";

  protected String monitorObjectName = DEFAULT_ETM_MONITOR_OBJECT_NAME;
  protected String measurementDomain = DEFAULT_ETM_POINT_DOMAIN;
  // default mbeanservername is null
  protected String mbeanServerName;
  protected boolean overwrite = false;

  protected MBeanServer mbeanServer;
  protected EtmMonitor etmMonitor;

  // flag to prevent registration during shutdown
  private boolean isStopping = true;

  protected EtmMonitorMBean monitorMBean;


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
   * {@link AbstractJmxRegistry#DEFAULT_ETM_MONITOR_OBJECT_NAME}
   *
   * @param aMonitorObjectName The JMX Object name to be used for registration.
   */
  public void setMonitorObjectName(String aMonitorObjectName) {
    if (aMonitorObjectName == null || aMonitorObjectName.trim().length() == 0) {
      throw new IllegalArgumentException("ObjectName for EtmMonitor may not be null or empty ");
    }
    monitorObjectName = aMonitorObjectName;
  }


  /**
   * Sets the prefix for measurement results. Default is
   * {@link etm.core.jmx.AbstractJmxRegistry#DEFAULT_ETM_POINT_DOMAIN}
   *
   * @param aMeasurementDomain The prefix.
   */

  public void setMeasurementDomain(String aMeasurementDomain) {
    measurementDomain = aMeasurementDomain;
  }

  /**
   * If a MBean is found under the given name the
   * existing instance will be overwritten. This behavior
   * is enabled by default.
   *
   * @param flag True to overwrite, otherwhise false.
   */

  public void setOverwrite(boolean flag) {
    overwrite = flag;
  }

  public void start() {
    if (mbeanServer == null) {
      try {
        ArrayList mbeanServers = MBeanServerFactory.findMBeanServer(mbeanServerName);
        if (mbeanServers.size() > 0) {
          mbeanServer = (MBeanServer) mbeanServers.get(0);
          ObjectName objectName = new ObjectName(monitorObjectName);

          monitorMBean = new EtmMonitorMBean(etmMonitor, measurementDomain);

          registerMBean(mbeanServer, objectName, monitorMBean, overwrite);

          isStopping = false;

        } else {
          log.warn("Unable to locate a valid MBeanServer. Disable JMX support.");
        }
      } catch (Exception e) {
        log.error("Error while registering EtmMonitorMBean ", e);
      }
    }

  }

  public void stop() {
    isStopping = true;

    if (mbeanServer != null) {
      try {
        deregisterPerformanceResults();
        mbeanServer.unregisterMBean(new ObjectName(monitorObjectName));
      } catch (Exception e) {
        log.warn("Error while unregistering EtmMonitorMBean ", e);
      }
    }
  }

  // callback methods

  public void onStateLoaded(AggregationStateLoadedEvent event) {
    if (isStopping) {
      return;
    }

    PersistentEtmState persistentEtmState = event.getState();
    Map aggregates = persistentEtmState.getAggregates();
    for (Iterator iterator = aggregates.values().iterator(); iterator.hasNext();) {
      Aggregate aggregate = (Aggregate) iterator.next();
      try {
        registerMBean(mbeanServer, calculateObjectName(measurementDomain, aggregate), new EtmPointMBean(etmMonitor, aggregate), overwrite);
      } catch (JMException e) {
        log.warn("Unable to register EtmPoint " + aggregate.getName(), e);
      }
    }
  }

  public void onRootCreate(RootCreateEvent event) {
    if (isStopping) {
      return;
    }

    Aggregate aggregate = event.getAggregate();
    try {
      registerMBean(mbeanServer, calculateObjectName(measurementDomain, aggregate), new EtmPointMBean(etmMonitor, aggregate), overwrite);
    } catch (JMException e) {
      log.warn("Unable to register EtmPoint " + aggregate.getName(), e);
    }
  }

  public void onStateReset(MonitorResetEvent event) {
    if (isStopping) {
      return;
    }
    try {
      deregisterPerformanceResults();
    } catch (Exception e) {
      log.warn("Error while deregistering all performance results ", e);
    }
  }

  public void onRootReset(RootResetEvent event) {
    // ignore
  }

  public void preRootReset(PreRootResetEvent event) {
    // ignore
  }

  public void preStateReset(PreMonitorResetEvent event) {
    // ignore
  }


  protected void deregisterPerformanceResults() throws MalformedObjectNameException, InstanceNotFoundException, MBeanRegistrationException {
    ObjectName objectName = new ObjectName(measurementDomain + ":*");
    Set set = mbeanServer.queryNames(objectName, null);
    for (Iterator iterator = set.iterator(); iterator.hasNext();) {
      ObjectName o = (ObjectName) iterator.next();
      mbeanServer.unregisterMBean(o);
    }
  }

}
