/*
 *
 * Copyright (c) void.fm
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

package etm.contrib.integration.jee.jsf;

import etm.core.metadata.PluginMetaData;
import etm.core.monitor.EtmMonitorContext;
import etm.core.plugin.EtmPlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * @author void.fm
 * @version $Revision: 372 $
 */
public class EtmJsfPlugin implements EtmPlugin {
  public static final String ROOT_ETM_POINT = "ETM__RootRequestPoint";

  protected static final String CONFIG_COMPONENT_MONITORING = "EtmJsfPlugin.componentMonitoring";
  private static final String DESCRIPTION = "Provides JSF component monitoring configuration";
  private boolean componentMonitoring;

  @Override
  public PluginMetaData getPluginMetaData() {
    Map<String, String> properties = new HashMap<String, String>();
    properties.put(CONFIG_COMPONENT_MONITORING, String.valueOf(isComponentMonitoring()));

    return new PluginMetaData(EtmJsfPlugin.class, DESCRIPTION, properties);
  }

  @Override
  public void init(EtmMonitorContext ctx) {

  }

  @Override
  public void start() {

  }

  @Override
  public void stop() {
  }


  public boolean isComponentMonitoring() {
    return componentMonitoring;
  }

  public void setComponentMonitoring(boolean enabled) {
    componentMonitoring = enabled;
  }
}
