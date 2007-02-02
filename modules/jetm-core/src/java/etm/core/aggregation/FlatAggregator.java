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

package etm.core.aggregation;

import etm.core.metadata.AggregatorMetaData;
import etm.core.monitor.EtmMonitorContext;
import etm.core.monitor.EtmPoint;
import etm.core.monitor.event.MonitorResetEvent;
import etm.core.monitor.event.RootCreateEvent;
import etm.core.monitor.event.RootResetEvent;
import etm.core.renderer.MeasurementRenderer;

import java.util.HashMap;
import java.util.Map;

/**
 * The FlatAggregator creates a flat mesurement result representation. Nesting elements
 * will be ignored.
 *
 * @author void.fm
 * @version $Revision$
 * @deprecated Use {@link etm.core.aggregation.RootAggregator} instead. This aggregator will be removed with JETM 2.0.0
 */

public class FlatAggregator implements Aggregator {

  protected Map aggregates = new HashMap();
  protected EtmMonitorContext ctx;

  public FlatAggregator() {
  }

  public void reset() {
    aggregates.clear();
    ctx.fireEvent(new MonitorResetEvent(this));
  }


  public void reset(String symbolicName) {
    ExecutionAggregate aggregate = (ExecutionAggregate) aggregates.get(symbolicName);
    if (aggregate != null) {
      aggregate.reset();
      ctx.fireEvent(new RootResetEvent(symbolicName, this));
    }
  }

  public void render(MeasurementRenderer renderer) {
    //todo fix concurrency issue
    // right now we block forever if rendering takes forever
    renderer.render(aggregates);
  }


  public void add(EtmPoint point) {
    ExecutionAggregate aggregate = getAggregate(point.getName());
    aggregate.addTransaction(point);
  }

  public void flush() {
    // ignore
  }

  public AggregatorMetaData getMetaData() {
    return new AggregatorMetaData(FlatAggregator.class, "An cummulating aggregator for flat representation.", false);
  }

  public void start() {
    // do nothing
  }

  public void stop() {
    // do nothing
  }

  public void init(EtmMonitorContext aCtx) {
    ctx = aCtx;
  }

  protected ExecutionAggregate getAggregate(final String aName) {
    ExecutionAggregate aggregate = (ExecutionAggregate) aggregates.get(aName);
    if (aggregate == null) {
      aggregate = new ExecutionAggregate(aName);
      aggregates.put(aName, aggregate);
      ctx.fireEvent(new RootCreateEvent(aggregate, this));
    }

    return aggregate;
  }

}
