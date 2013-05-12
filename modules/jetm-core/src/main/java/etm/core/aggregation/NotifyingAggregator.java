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

package etm.core.aggregation;

import etm.core.metadata.AggregatorMetaData;
import etm.core.monitor.EtmMonitorContext;
import etm.core.monitor.EtmPoint;
import etm.core.monitor.event.CollectEvent;
import etm.core.renderer.MeasurementRenderer;
import etm.core.util.Log;
import etm.core.util.LogAdapter;

/**
 * An aggregator that creates {@link etm.core.monitor.event.CollectEvent}s for every new
 * collected {@link etm.core.monitor.EtmPoint}. Always use in conjunction with a buffering
 * aggregator such as {@link etm.core.aggregation.BufferedThresholdAggregator} or
 * {@link etm.core.aggregation.BufferedTimedAggregator}. We recommend using the interval based
 * {@link etm.core.aggregation.BufferedTimedAggregator}.
 *
 * @author void.fm
 * @version $Revision$
 * @since 1.2.0
 */
public class NotifyingAggregator implements Aggregator {

  private static final LogAdapter LOG = Log.getLog(NotifyingAggregator.class);

  private Aggregator delegate;
  private EtmMonitorContext ctx;

  private boolean rootOnly = false;

  public NotifyingAggregator(Aggregator aDelegate) {
    delegate = aDelegate;
  }

  /**
   *
   * If rootOnly is enabled, only measurement roots will create
   * an event.
   *
   * @param aRootOnly True to notify on root collections only. Default is false.
   */
  public void setRootOnly(boolean aRootOnly) {
    rootOnly = aRootOnly;
  }

  public void setFilterPattern(String pattern) {
    LOG.warn("Filtering not supported yet.");
  }

  public void add(EtmPoint point) {
    delegate.add(point);
    if (!rootOnly || point.getParent() == null) {
      ctx.fireEvent(new CollectEvent(this, point));
    }
  }

  public void flush() {
    delegate.flush();
  }

  public void reset() {
    delegate.reset();
  }

  public void reset(String symbolicName) {
    delegate.reset(symbolicName);
  }

  public void render(MeasurementRenderer renderer) {
    delegate.render(renderer);
  }

  public AggregatorMetaData getMetaData() {
    return new AggregatorMetaData(NotifyingAggregator.class, "An aggregator that creates events for newly " +
      "collected performance results.", false, delegate.getMetaData());
  }

  public void init(EtmMonitorContext aCtx) {
    ctx = aCtx;
    delegate.init(aCtx);
  }

  public void start() {
    delegate.start();
  }

  public void stop() {
    delegate.stop();
  }
}
