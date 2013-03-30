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

package etm.contrib.console;

import etm.core.metadata.PluginMetaData;
import etm.core.monitor.EtmMonitorContext;
import etm.core.plugin.EtmPlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * A EtmPlugin that enables our HTTP console.
 *
 * @author void.fm
 * @version $Revision$
 */
public class HttpConsoleServerPlugin extends HttpConsoleServer implements EtmPlugin {
  private static final String DESCRIPTION = "A HTTP Console providing aggregated results.";

  public HttpConsoleServerPlugin() {
    // workaround for required constructor
    super(null);
  }

  public void init(EtmMonitorContext ctx) {
    etmMonitor = ctx.getEtmMonitor();
  }

  /**
   * Returns the current HTTP console metadata. The provided map of properties contains
   * <ul>
   * <li><i>listenPort</i> - the port the console is listen at</li>
   * <li><i>workerSize</i> - the number of workers answering requests</li>
   * <li><i>expanded</i> - whether the console defaults to expanded view or not</li>
   * </ul>
   *
   * @return The plugin metadata
   */
  public PluginMetaData getPluginMetaData() {

    Map properties = new HashMap();
    properties.put("listenPort", String.valueOf(getListenPort()));
    properties.put("workerSize", String.valueOf(getWorkerSize()));
    properties.put("expanded", String.valueOf(isExpanded()));

    return new PluginMetaData(HttpConsoleServerPlugin.class, DESCRIPTION, properties);
  }
}
