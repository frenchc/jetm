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

package etm.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import etm.core.aggregation.Aggregator;
import etm.core.aggregation.RootAggregator;
import etm.core.configuration.EtmMonitorFactory;
import etm.core.metadata.AggregatorMetaData;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmMonitorContext;
import etm.core.monitor.EtmPoint;
import etm.core.monitor.NestedMonitor;
import etm.core.renderer.MeasurementRenderer;
import etm.core.timer.ExecutionTimer;

/**
 * Test class for test point generation
 *
 * @author void.fm
 * @version $Revision$
 */

public class TestPointGenerator {

  ExecutionTimer timer = EtmMonitorFactory.bestAvailableTimer();
  CollectingAggregator aggregator = new CollectingAggregator(new RootAggregator());
  EtmMonitor monitor = new NestedMonitor(timer, aggregator);

  public TestPointGenerator() {
  }

  public TestPointGenerator(EtmMonitor aMonitor) {
    monitor = aMonitor;
  }

  public EtmPoint getEtmPoint() {
    monitor.start();

    try {
      EtmPoint point = monitor.createPoint("Testpoint");
      try {
        Thread.sleep((long) (10d * Math.random()));
      } catch (InterruptedException e) {
        // ignored
      } finally {
        point.collect();
      }

      return point;
    } finally {
      monitor.stop();
    }
  }

  public List getEtmPoints(int topLevel, int nestedSize) {
    monitor.start();

    try {
      for (int i = 0; i < topLevel; i++) {
        EtmPoint parent = monitor.createPoint("Parent::" + i);

        try {
          Thread.sleep((long) (10d * Math.random()));
          if (nestedSize > 1) {
            doNested(i, 0, nestedSize);
          }
        } catch (InterruptedException e) {
          // ignored
        } finally {
          parent.collect();
        }
      }

      //monitor.render(new SimpleTextRenderer());

      return aggregator.getPoints();
    } finally {
      monitor.stop();
    }

  }


  public Map getAggregates(int topLevel, int nestedSize) {

    for (int i = 0; i < topLevel; i++) {
      EtmPoint parent = monitor.createPoint("Testpoint" + i);

      try {
        Thread.sleep((long) (10d * Math.random()));
        if (nestedSize > 1) {
          doNested(i, 0, nestedSize);
        }
      } catch (InterruptedException e) {
        // ignored
      } finally {
        parent.collect();
      }

    }

    DummyRenderer renderer = new DummyRenderer();
    monitor.render(renderer);
    //monitor.render(new SimpleTextRenderer());
    return renderer.getPoints();
  }


  private void doNested(final int parentId, final int currentLevel, final int maxLevel) {
    int flatCounter = 0;


    doFlat(parentId, currentLevel, maxLevel, flatCounter);

    while (Math.random() < 0.2) {
      flatCounter++;
      doFlat(parentId, currentLevel, maxLevel, flatCounter);
    }

  }

  private void doFlat(final int parentId, final int currentLevel, final int maxLevel, final int flatCounter) {
    EtmPoint point = monitor.createPoint( "N" + parentId + "-L" + currentLevel + "-C" + flatCounter);

    try {
      Thread.sleep((long) (10d * Math.random()));

      if (currentLevel < maxLevel) {
        doNested(parentId, currentLevel + 1, maxLevel);
      }
    } catch (InterruptedException e) {
      // ignored
    } finally {
      point.collect();
    }
  }


  class DummyRenderer implements MeasurementRenderer {
    private Map points;

    public Map getPoints() {
      return points;
    }

    public void render(Map somePoints) {
      points = somePoints;
    }
  }


  class CollectingAggregator implements Aggregator {

    private List points = new ArrayList();
    private Aggregator delegate;

    public List getPoints() {
      return points;
    }

    public CollectingAggregator(Aggregator aDelegate) {
      delegate = aDelegate;
    }

    public void add(EtmPoint point) {
      points.add(point);
      delegate.add(point);
    }

    public void flush() {
    }

    public void reset() {
    }


    public void reset(String symbolicName) {

    }

    public void render(MeasurementRenderer renderer) {
      delegate.render(renderer);
    }

    public AggregatorMetaData getMetaData() {
      return new AggregatorMetaData(CollectingAggregator.class, "Helper aggregator", false, delegate.getMetaData());
    }

    public void start() {

    }

    public void stop() {

    }

    public void init(EtmMonitorContext ctx) {
      delegate.init(ctx);
    }
  }

}
