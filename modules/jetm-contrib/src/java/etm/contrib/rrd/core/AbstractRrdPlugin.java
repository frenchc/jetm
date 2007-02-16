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
package etm.contrib.rrd.core;

import etm.core.aggregation.NotifyingAggregator;
import etm.core.metadata.AggregatorMetaData;
import etm.core.monitor.EtmException;
import etm.core.monitor.EtmMonitorContext;
import etm.core.monitor.event.CollectEvent;
import etm.core.monitor.event.CollectionListener;
import etm.core.plugin.EtmPlugin;

/**
 * Abtract base class for plugins that use a
 *
 * @author void.fm
 * @version $Revision$
 * @since 1.2.0
 */
public abstract class AbstractRrdPlugin implements EtmPlugin, CollectionListener {

  protected EtmMonitorContext ctx;
  protected RrdDestination[] destinations;

  public void init(EtmMonitorContext aCtx) {
    ctx = aCtx;
  }

  public void start() {
    // validate whether we find required aggregator
    doValidateChain();
  }


  public void stop() {
  }


  public void onCollect(CollectEvent event) {
    for (int i = 0; i < destinations.length; i++) {
      RrdDestination destination = destinations[i];
      if (destination.matches(event.getPoint())) {
        destination.write(event.getPoint());
      }
    }
  }


  protected void doValidateChain() {
    AggregatorMetaData metaData = ctx.getEtmMonitor().getMetaData().getAggregatorMetaData();
    while (metaData != null) {
      if (metaData.getImplementationClass().isAssignableFrom(NotifyingAggregator.class)) {
        return;
      }
    }

    throw new EtmException("Missing NotifyingAggregator. There has to be a NotifyingAggregator im your aggregation chain. Rrd support disabled.");
  }

}
