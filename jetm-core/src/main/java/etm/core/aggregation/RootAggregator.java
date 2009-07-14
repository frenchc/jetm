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
import etm.core.monitor.event.PreMonitorResetEvent;
import etm.core.monitor.event.PreRootResetEvent;
import etm.core.monitor.event.RootCreateEvent;
import etm.core.monitor.event.RootResetEvent;
import etm.core.renderer.MeasurementRenderer;
import etm.core.util.collection.CollectionFactory;

import java.util.LinkedList;
import java.util.Map;

/**
 * An Aggregator that cumulates results, supports both nested and flat aggregation.
 *
 * @author void.fm
 * @version $Revision$
 * @since 1.2.0
 */
public class RootAggregator implements Aggregator {

  private static final String AGGREGATOR_DESCRIPTION = "An cummulating aggregator for flat and nested representation.";

  protected Map aggregates = CollectionFactory.getInstance().newConcurrentHashMapInstance();

  protected EtmMonitorContext ctx;


  public void reset() {
    ctx.fireEvent(new PreMonitorResetEvent(aggregates, this));
    aggregates.clear();
    ctx.fireEvent(new MonitorResetEvent(this));
  }

  public void reset(String symbolicName) {
    ExecutionAggregate aggregate = (ExecutionAggregate) aggregates.get(symbolicName);
    if (aggregate != null) {
      ctx.fireEvent(new PreRootResetEvent(aggregate, this));
      aggregate.reset();
      ctx.fireEvent(new RootResetEvent(symbolicName, this));
    }
  }

  public void render(MeasurementRenderer renderer) {
    renderer.render(aggregates);
  }

  public void flush() {
    // ignore
  }

  public AggregatorMetaData getMetaData() {
    return new AggregatorMetaData(RootAggregator.class, AGGREGATOR_DESCRIPTION, false);
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

  public void add(EtmPoint point) {
    // shortcut for parent == null;
    if (point.getParent() == null) {
      ExecutionAggregate aggregate = getAggregate(point.getName());
      aggregate.addTransaction(point);
      return;
    }

    // TODO check alternative strategy to improve performance
    // find tree root node
    LinkedList path = new LinkedList();
    path.add(point);

    EtmPoint rootNode = point.getParent();
    while (rootNode != null) {
      path.addFirst(rootNode);
      rootNode = rootNode.getParent();
    }

    rootNode = (EtmPoint) path.removeFirst();

    ExecutionAggregate aggregate = getAggregate(rootNode.getName());
    aggregate.appendPath(path);
  }

  protected ExecutionAggregate getAggregate(String aName) {
    ExecutionAggregate aggregate = (ExecutionAggregate) aggregates.get(aName);
    if (aggregate == null) {
      aggregate = new ExecutionAggregate(aName);
      aggregates.put(aName, aggregate);
      ctx.fireEvent(new RootCreateEvent(aggregate, this));
    }

    return aggregate;
  }

}
