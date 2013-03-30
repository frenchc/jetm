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

import etm.core.aggregation.Aggregate;
import etm.core.aggregation.ExecutionAggregate;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;
import etm.core.renderer.MeasurementRenderer;
import junit.framework.TestCase;
import etm.core.TestHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Tests for flat concurrency testing.
 *
 * @author void.fm
 * @version $Revision$
 */
public abstract class CommonConcurrentFlatAggregationTests extends TestCase {
  protected EtmMonitor monitor;
  private final Object lock = new Object();
  private final List allPoints = new ArrayList();
  private int running;

  /**
   * Tests one etm point with multiple threads.
   *
   * @throws Exception Unexcpeted Exception
   */
  public void testManyThreadsOnePoint() throws Exception {
    int testSize = 100;
    int iterations = 500;
    final String testPointGroup1 = "group1";


    Runner[] runnerGroup1 = new Runner[testSize];
    for (int i = 0; i < testSize; i++) {
      runnerGroup1[i] = new Runner(testPointGroup1, iterations);
    }
    for (int i = 0; i < testSize; i++) {
      runnerGroup1[i].start();
    }

    do {
      synchronized (lock) {
        lock.wait();
      }
    } while (running > 0);


    final ExecutionAggregate group1 = new ExecutionAggregate(testPointGroup1);

    for (int i = 0; i < allPoints.size(); i++) {
      EtmPoint point = (EtmPoint) allPoints.get(i);
      if (point.getName().equals(testPointGroup1)) {
        group1.addTransaction(point);
      } else {
        fail("Unknown group " + point.getName());
      }
    }

    monitor.render(new MeasurementRenderer() {
      public void render(Map points) {

      }
    });


    final int expectedExecutions = testSize * iterations;

    monitor.render(new MeasurementRenderer() {
      public void render(Map points) {

        assertEquals(expectedExecutions, new TestHelper().countExecutions(points));

        assertNotNull(points);
        assertTrue(points.size() == 1);

        Aggregate aggregate = (Aggregate) points.get(testPointGroup1);

        assertNotNull(aggregate);
        assertEquals(testPointGroup1, aggregate.getName());
        assertEquals(group1.getMeasurements(), aggregate.getMeasurements());

        assertEquals(group1.getTotal(), aggregate.getTotal(), 0.0001);
        assertEquals(group1.getMin(), aggregate.getMin(), 0.0001);
        assertEquals(group1.getMax(), aggregate.getMax(), 0.0001);
      }
    });
  }

  /**
   * Tests many points with many threads.
   *
   * @throws Exception Unexpected exception.
   */

  public void testManyThreadsManyPoints() throws Exception {
    final int pointSize = 20;
    int threadSize = 50;
    int iterations = 500;
    String testPrefix = "group";


    Runner[][] runners = new Runner[pointSize][threadSize];

    for (int j = 0; j < pointSize; j++) {
      for (int i = 0; i < threadSize; i++) {
        runners[j][i] = new Runner(testPrefix + j, iterations);
      }
    }
    for (int j = 0; j < pointSize; j++) {
      for (int i = 0; i < threadSize; i++) {
        runners[j][i].start();
      }
    }

    do {
      synchronized (lock) {
        lock.wait();
      }
    } while (running > 0);


    final Map aggregates = new HashMap();

    for (int i = 0; i < allPoints.size(); i++) {
      EtmPoint point = (EtmPoint) allPoints.get(i);

      ExecutionAggregate aggregate = (ExecutionAggregate) aggregates.get(point.getName());
      if (aggregate == null) {
        aggregate = new ExecutionAggregate(point.getName());
        aggregates.put(point.getName(), aggregate);
      }
      aggregate.addTransaction(point);

    }


    assertEquals(aggregates.size(), pointSize);

    final int expectedExecutions = pointSize * threadSize * iterations;

    monitor.render(new MeasurementRenderer() {
      public void render(Map points) {
        assertNotNull(points);
        assertTrue(points.size() == pointSize);

        assertEquals(expectedExecutions, new TestHelper().countExecutions(points));

        for (Iterator iterator = points.keySet().iterator(); iterator.hasNext();) {
          String s = (String) iterator.next();
          Aggregate renderAggregate = (Aggregate) points.get(s);
          Aggregate actualAggregate = (Aggregate) aggregates.get(s);

          assertNotNull(actualAggregate);

          assertEquals(actualAggregate.getName(), renderAggregate.getName());
          assertEquals(actualAggregate.getMeasurements(), renderAggregate.getMeasurements());

          assertEquals(actualAggregate.getTotal(), renderAggregate.getTotal(), 0.00001);
          assertEquals(actualAggregate.getMin(), renderAggregate.getMin(), 0.00001);
          assertEquals(actualAggregate.getMax(), renderAggregate.getMax(), 0.00001);
        }

      }
    });
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
      synchronized (allPoints) {
        running++;
      }
      try {

        while (runs > 0) {
          final EtmPoint point = monitor.createPoint(testPointName);
          Thread.sleep(10);
          point.collect();

          list.add(point);
          runs--;
        }
      } catch (InterruptedException e) {
        // ignored
      }

      synchronized (allPoints) {
        allPoints.addAll(list);
        running--;
      }

      synchronized (lock) {
        lock.notifyAll();
      }
    }

  }
}
