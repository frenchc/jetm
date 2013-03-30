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

package etm.contrib.aggregation.log;

import etm.core.aggregation.Aggregator;
import etm.core.aggregation.RootAggregator;
import etm.core.metadata.AggregatorMetaData;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmMonitorContext;
import etm.core.monitor.EtmPoint;
import etm.core.monitor.NullMonitor;
import etm.core.monitor.event.EtmMonitorEvent;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * Tests basic behavior for logging aggregators.
 *
 * @author void.fm
 * @version $Revision$
 */
public class AbstractLogAggregatorTest extends TestCase {

  /**
   * Tests the log aggregator filtering mechanism. If filtering works
   * the overall implementation works.
   */
  public void testFilter() {
    EtmMonitor monitor = new NullMonitor();
    TestLogAggregator aggregator = new TestLogAggregator(new RootAggregator());

    aggregator.setFilterPattern("Action.+;ClassName;");

    aggregator.add(monitor.createPoint("Action Foo"));
    aggregator.add(monitor.createPoint("Action Bar"));
    aggregator.add(monitor.createPoint("AnotherPoint"));
    aggregator.add(monitor.createPoint("ClassName"));
    aggregator.add(monitor.createPoint("Action Bar"));

    assertEquals(4, aggregator.points.size());
  }


  /**
   * Tests the log aggregator filtering mechanism. If filtering works
   * the overall implementation works.
   */
  public void testWithoutFilter() {
    EtmMonitor monitor = new NullMonitor();
    TestLogAggregator aggregator = new TestLogAggregator(new RootAggregator());

    aggregator.add(monitor.createPoint("Action Foo"));
    aggregator.add(monitor.createPoint("Action Bar"));
    aggregator.add(monitor.createPoint("AnotherPoint"));
    aggregator.add(monitor.createPoint("ClassName"));
    aggregator.add(monitor.createPoint("Action Bar"));

    assertEquals(5, aggregator.points.size());
  }

  class TestLogAggregator extends AbstractLogAggregator {
    private List points = new ArrayList();

    public TestLogAggregator(Aggregator aAggregator) {
      super(aAggregator);
      init(new EtmMonitorContext() {

        public EtmMonitor getEtmMonitor() {
          throw new UnsupportedOperationException();
        }

        public Timer getScheduler() {
          throw new UnsupportedOperationException();
        }

        public void fireEvent(EtmMonitorEvent event) {
          //ignore
        }
      });
    }

    protected void logMeasurement(EtmPoint aPoint) {
      points.add(aPoint);
    }

    public AggregatorMetaData getMetaData() {
      throw new UnsupportedOperationException();
    }
  }
}
