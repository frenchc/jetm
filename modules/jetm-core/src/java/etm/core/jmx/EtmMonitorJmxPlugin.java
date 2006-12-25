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

package etm.core.jmx;

import etm.core.monitor.EtmMonitor;
import etm.core.plugin.EtmPlugin;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import java.util.ArrayList;

/**
 * A plugin that registers the EtmMonitor in JMX. Default name is
 * etm:service=PerformanceMonitor.
 * <p/>
 * By default the plugin will fail if the an mbean exists under the
 * given name. You may disable this by setting {@link #setOverwriteRegistered(boolean)}
 * to true.
 *
 * @author void.fm
 * @version $Revision$
 */
public class EtmMonitorJmxPlugin implements EtmPlugin {

  public static final String DEFAULT_ETM_MONITOR_OBJECT_NAME = "etm:service=PerformanceMonitor";

  private EtmMonitor monitor;
  private MBeanServer mbeanServer;

  private String etmMonitorObjectName = DEFAULT_ETM_MONITOR_OBJECT_NAME;
  // default mbeanservername is null
  private String mbeanServerName;
  private boolean overwriteRegistered = false;


  public void setEtmMonitor(EtmMonitor aEtmMonitor) {
    monitor = aEtmMonitor;
  }

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
   * {@link etm.core.jmx.EtmMonitorJmxPlugin#DEFAULT_ETM_MONITOR_OBJECT_NAME}
   *
   * @param aEtmMonitorObjectName The JMX Object name to be used for registration.
   */
  public void setEtmMonitorObjectName(String aEtmMonitorObjectName) {
    if (aEtmMonitorObjectName == null || aEtmMonitorObjectName.trim().length() == 0) {
      throw new IllegalArgumentException("ObjectName for EtmMonitor may not be null or empty ");
    }
    etmMonitorObjectName = aEtmMonitorObjectName;
  }

  /**
   * If a MBean is found under the given name the
   * existing instance will be overwritten. This behavior
   * is disabled by default.
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
        try {
          mbeanServer.registerMBean(new EtmMonitorMBean(monitor), objectName);
        } catch (InstanceAlreadyExistsException e) {
          if (overwriteRegistered) {
            mbeanServer.unregisterMBean(objectName);
            mbeanServer.registerMBean(new EtmMonitorMBean(monitor), objectName);
          } else {
            System.err.println("Error registering EtmMonitor MBean. An instance exists for name " + etmMonitorObjectName);
          }
        }
      } else {
        System.err.println("Unable to locate a valid MBean server. ");
      }
    } catch (Exception e) {
      //???
      e.printStackTrace();
    }
  }

  public void stop() {
    if (mbeanServer != null) {
      try {
        mbeanServer.unregisterMBean(new ObjectName(etmMonitorObjectName));
      } catch (Exception e) {
        // ???
        e.printStackTrace();
      }
    }
  }
}
