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
import etm.core.util.Log;
import etm.core.util.LogAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Abtract base class for plugins that store collected details in a RRD database.
 *
 * @author void.fm
 * @version $Revision$
 * @since 1.2.0
 */
public abstract class AbstractRrdPlugin implements EtmPlugin, CollectionListener {

  private static final LogAdapter log = Log.getLog(AbstractRrdPlugin.class);


  protected EtmMonitorContext ctx;
  protected RrdDestination[] destinations;

  public void init(EtmMonitorContext aCtx) {
    ctx = aCtx;
  }

  public void start() {
    // validate whether we find required aggregator
    doValidateChain();
    RrdDestination[] tmp = getDestinations();

    List dest = new ArrayList();
    for (int i = 0; i < tmp.length; i++) {
      RrdDestination rrdDestination = tmp[i];
      try {
        rrdDestination.start();
        dest.add(rrdDestination);
        log.debug("Added RRD destination " + rrdDestination);
      } catch (Exception e) {
        log.warn("Error activation RRD destination " + rrdDestination, e);
      }
    }

    destinations = (RrdDestination[]) dest.toArray(new RrdDestination[dest.size()]);
  }


  public void stop() {
    RrdDestination[] saved = destinations;
    destinations = new RrdDestination[0];

    if (saved != null) {
      for (int i = 0; i < saved.length; i++) {
        saved[i].stop();
      }
    }
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
      metaData = metaData.getNestedMetaData();
    }

    throw new EtmException("Missing NotifyingAggregator. There has to be a " +
      "NotifyingAggregator in your aggregation chain. Rrd support disabled.");
  }

  protected abstract RrdDestination[] getDestinations();
}
