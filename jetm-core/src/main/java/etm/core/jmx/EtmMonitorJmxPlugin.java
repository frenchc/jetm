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

import etm.core.metadata.PluginMetaData;
import etm.core.monitor.EtmMonitorContext;
import etm.core.plugin.EtmPlugin;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * A plugin that exports the current ETM Monitor and all top level
 * performance results via JMX.
 *
 * @author void.fm
 * @version $Revision$
 * @since 1.2.0
 * 
 */
public class EtmMonitorJmxPlugin extends AbstractJmxRegistry implements EtmPlugin {

  protected static final String DESCRIPTION = "A plugin the exports the current EtmMonitor to JMX.";

  public void init(EtmMonitorContext aCtx) {
    etmMonitor = aCtx.getEtmMonitor();
  }

  /**
   * Returns the current JMX Plugin console metadata. The provided map of properties contains
   * <ul>
   * <li><i>jmxObjectName</i> - the JMX ObjectName used for registration</li>
   * <li><i>mbeanServerName</i> - the name of the JMX MBeanServer, may be null</li>
   * <li><i>overwrite</i> - whether an already existing MBean will be overwritten or not.</li>
   * </ul>
   *
   * @return The plugin metadata
   */
  public PluginMetaData getPluginMetaData() {
    Map properties = new HashMap();
    properties.put("monitorObjectName", monitorObjectName);
    properties.put("mbeanServerName", mbeanServerName);
    properties.put("measurementDomain", measurementDomain);
    properties.put("overwrite", String.valueOf(overwrite));

    return new PluginMetaData(EtmMonitorJmxPlugin.class, DESCRIPTION, properties);
  }

}
