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


package test.etm.core.monitor;

import etm.core.aggregation.Aggregate;
import etm.core.aggregation.ExecutionAggregate;
import etm.core.aggregation.RootAggregator;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;
import etm.core.monitor.NestedMonitor;
import etm.core.renderer.MeasurementRenderer;
import etm.core.timer.DefaultTimer;
import junit.framework.TestCase;
import test.etm.core.TestAggregator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Concurrency tests for flat monitor.
 *
 * @author void.fm
 * @version $Revision$
 */
public class ConcurrentNestedMonitorTest extends TestCase {


  protected EtmMonitor monitor;
  protected TestAggregator aggregator;

  private Object lock = new Object();
  private int running;
  private List allPoints = new ArrayList();

  /**
   * tests two points with multiple threads.
   */
  public void testThreadedTwoPoint() throws Exception {
    assertNotNull(monitor);

    final int testSize = 50;
    final int iterations = 100;
    final String testPointGroup1 = "group1";
    final String testPointGroup2 = "group2";

    Runner[] runnerGroup1 = new Runner[testSize];
    Runner[] runnerGroup2 = new Runner[testSize];


    for (int i = 0; i < testSize; i++) {
      runnerGroup1[i] = new Runner(testPointGroup1, iterations);
      runnerGroup2[i] = new Runner(testPointGroup2, iterations);
    }

    for (int i = 0; i < testSize; i++) {
      runnerGroup1[i].start();
      runnerGroup2[i].start();
    }

    do {
      synchronized (lock) {
        lock.wait();
      }
    } while (running > 0);


    final ExecutionAggregate group1 = new ExecutionAggregate(testPointGroup1);
    final ExecutionAggregate group2 = new ExecutionAggregate(testPointGroup2);

    final ExecutionAggregate nested1 = new ExecutionAggregate("nested" + testPointGroup1);
    final ExecutionAggregate nested2 = new ExecutionAggregate("nested" + testPointGroup2);

    for (int i = 0; i < allPoints.size(); i++) {
      EtmPoint point = (EtmPoint) allPoints.get(i);
      if (point.getName().equals(testPointGroup1)) {
        group1.addTransaction(point);
      } else if (point.getName().equals(testPointGroup2)) {
        group2.addTransaction(point);
      } else if (point.getName().equals("nested" + testPointGroup1)) {
        nested1.addTransaction(point);
      } else if (point.getName().equals("nested" + testPointGroup2)) {
        nested2.addTransaction(point);
      } else {
        fail("Unknown point " + point.getName());
      }
    }


    assertEquals(4 * testSize * iterations, aggregator.getCounter());

    monitor.render(new MeasurementRenderer() {
      public void render(Map points) {
        assertNotNull(points);
        assertTrue(points.size() == 2);

        // check group 1
        ExecutionAggregate aggregate = (ExecutionAggregate) points.get(testPointGroup1);

        assertNotNull(aggregate);
        assertEquals(testPointGroup1, aggregate.getName());
        assertEquals(testSize * iterations, aggregate.getMeasurements());

        assertEquals(group1.getTotal(), aggregate.getTotal(), 0.0);
        assertEquals(group1.getMin(), aggregate.getMin(), 0.0);
        assertEquals(group1.getMax(), aggregate.getMax(), 0.0);

        assertTrue(aggregate.hasChilds());

        Map childs1 = aggregate.getChilds();
        Aggregate aggregateChild1 = (Aggregate) childs1.get("nested" + testPointGroup1);

        assertNotNull(aggregateChild1);
        assertEquals(nested1.getName(), aggregateChild1.getName());
        assertEquals(testSize * iterations, aggregateChild1.getMeasurements());

        assertEquals(nested1.getTotal(), aggregateChild1.getTotal(), 0.0);
        assertEquals(nested1.getMin(), aggregateChild1.getMin(), 0.0);
        assertEquals(nested1.getMax(), aggregateChild1.getMax(), 0.0);

        // check group 2
        ExecutionAggregate aggregate2 = (ExecutionAggregate) points.get(testPointGroup2);

        assertNotNull(aggregate2);
        assertEquals(testPointGroup2, aggregate2.getName());
        assertEquals(testSize * iterations, aggregate2.getMeasurements());

        assertEquals(group2.getTotal(), aggregate2.getTotal(), 0.0);
        assertEquals(group2.getMin(), aggregate2.getMin(), 0.0);
        assertEquals(group2.getMax(), aggregate2.getMax(), 0.0);

        assertTrue(aggregate2.hasChilds());

        Map childs2 = aggregate2.getChilds();
        Aggregate aggregateChild2 = (Aggregate) childs2.get("nested" + testPointGroup2);

        assertNotNull(aggregateChild2);
        assertEquals(nested2.getName(), aggregateChild2.getName());
        assertEquals(testSize * iterations, aggregateChild2.getMeasurements());

        assertEquals(nested2.getTotal(), aggregateChild2.getTotal(), 0.0);
        assertEquals(nested2.getMin(), aggregateChild2.getMin(), 0.0);
        assertEquals(nested2.getMax(), aggregateChild2.getMax(), 0.0);
      }
    });

  }


  protected void tearDown() throws Exception {
    monitor.stop();
    monitor.reset();
    monitor = null;
  }


  protected void setUp() throws Exception {
    aggregator = new TestAggregator(new RootAggregator());
    monitor = new NestedMonitor(new DefaultTimer(), aggregator);
    monitor.start();
  }


  class Runner extends Thread {

    List list = new ArrayList();

    private String testPointName;
    private int runs;


    public Runner(String aTestPointName, int aRuns) {
      testPointName = aTestPointName;
      runs = aRuns;
    }

    public void run() {
      running++;
      try {

        while (runs > 0) {
          final EtmPoint point = monitor.createPoint(testPointName);
          Thread.sleep(1);

          final EtmPoint nested = monitor.createPoint("nested" + testPointName);
          Thread.sleep(1);
          nested.collect();

          point.collect();

          list.add(nested);
          list.add(point);
          runs--;
        }
      } catch (InterruptedException e) {

      }

      synchronized (allPoints) {
        allPoints.addAll(list);
      }
      running--;
      synchronized (lock) {
        lock.notifyAll();
      }
    }

  }

}
