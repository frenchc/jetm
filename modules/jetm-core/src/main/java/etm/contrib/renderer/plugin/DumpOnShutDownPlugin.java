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

package etm.contrib.renderer.plugin;

import etm.core.aggregation.Aggregate;
import etm.core.metadata.EtmMonitorMetaData;
import etm.core.metadata.PluginMetaData;
import etm.core.monitor.EtmMonitorContext;
import etm.core.monitor.event.AggregationFinishedEvent;
import etm.core.monitor.event.AggregationListener;
import etm.core.monitor.event.MonitorResetEvent;
import etm.core.monitor.event.PreMonitorResetEvent;
import etm.core.monitor.event.PreRootResetEvent;
import etm.core.monitor.event.RootCreateEvent;
import etm.core.monitor.event.RootResetEvent;
import etm.core.plugin.EtmPlugin;
import etm.core.renderer.SimpleTextRenderer;

import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class for Plugins that dump the current aggregated
 * results during shutdown.
 *
 * @author void.fm
 * @version $Revision$
 */
public abstract class DumpOnShutDownPlugin implements EtmPlugin, AggregationListener {
  private static final String DEFAULT_LOG_NAME = "etm-dump";

  protected String logName = DEFAULT_LOG_NAME;
  protected EtmMonitorContext ctx;
  protected String lineSeparator = System.getProperty("line.separator");


  private String description;


  protected DumpOnShutDownPlugin(String aDescription) {
    description = aDescription;
  }

  public void setLogName(String aLogName) {
    logName = aLogName;
  }

  public void init(EtmMonitorContext aCtx) {
    ctx = aCtx;
  }

  /**
   * Returns the current Dump On Shutdown metadata. The provided map of properties contains
   * <ul>
   * <li><i>logName</i> - the JMX ObjectName used for registration</li>
   * </ul>
   *
   * @return The plugin metadata
   */

  public PluginMetaData getPluginMetaData() {
    Map<String, String> properties = new HashMap<>();
    properties.put("logName", logName);

    return new PluginMetaData(getClass(), description, properties);
  }


  public void onRootCreate(RootCreateEvent event) {
  }

  public void preRootReset(final PreRootResetEvent event) {
    StringWriter writer = new StringWriter();
    EtmMonitorMetaData etmMonitorMetaData = ctx.getEtmMonitor().getMetaData();

    Aggregate aggregate = event.getAggregate();

    writer.write("Dumping performance results '");
    writer.write(aggregate.getName());
    writer.write("' for period ");
    writer.write(etmMonitorMetaData.getLastResetTime().toString());
    writer.write(" - ");
    writer.write(new Date().toString());
    writer.write(lineSeparator);

    SimpleTextRenderer textRenderer = new SimpleTextRenderer(writer);
    Map<String, Aggregate> map = new HashMap<>();
    map.put(aggregate.getName(), aggregate);
    textRenderer.render(map);
    logResetDetail(writer.toString());
  }

  public void onRootReset(RootResetEvent event) {
  }

  public void preStateReset(PreMonitorResetEvent event) {
    StringWriter writer = new StringWriter();
    EtmMonitorMetaData etmMonitorMetaData = ctx.getEtmMonitor().getMetaData();
    writer.write("Dumping performance results for period ");
    writer.write(etmMonitorMetaData.getLastResetTime().toString());
    writer.write(" - ");
    writer.write(new Date().toString());
    writer.write(lineSeparator);

    SimpleTextRenderer textRenderer = new SimpleTextRenderer(writer);
    textRenderer.render(event.getAggregates());
    logResetDetail(writer.toString());
  }

  public void onStateReset(MonitorResetEvent event) {
    // ignored
  }

  public void onAggregationFinished(AggregationFinishedEvent event) {
    // ignored
  }

  /**
   * Logs aggregated statistics before reset.
   *
   * @param information The information that will be resetted.
   */

  protected abstract void logResetDetail(String information);
}
