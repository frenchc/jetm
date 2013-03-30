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

import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;
import etm.core.monitor.NestedMonitor;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Test to check possible concurrency issues during aggregation in nested monitors.
 *
 * @author void.fm
 * @version $Revision$
 */

public class ConcurrentNestedThresholdAggregationTest extends TestCase {


  protected EtmMonitor monitor;


  private int running;
  private final List allPoints = new ArrayList();
  private final Object lock = new Object();


  /**
   * Tests one etm point with multiple threads.
   *
   * @throws Exception Unexpected exception
   */
  public void testManyThreadsOnePoint() throws Exception {
    final int testSize = 100;
    final int iterations = 500;
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

    // todo
//
//    final ExecutionAggregate group1 = new ExecutionAggregate(testPointGroup1);
//
//    for (int i = 0; i < allPoints.size(); i++) {
//      TestMeasurementPoint point = (TestMeasurementPoint) allPoints.get(i);
//      if (point.getName().equals(testPointGroup1)) {
//        group1.addTransaction(point);
//      } else {
//        fail("Unknown group " + point.getName());
//      }
//    }

    // we should have 0 open entries
    // todo
    // assertEquals(testSize * iterations * 2, aggregator.getCounter());

//    monitor.render(new MeasurementRenderer() {
//      public void render(Map points) {
//        assertNotNull(points);
//        assertTrue(points.size() == 1);
//
//        ExecutionAggregate flush = (ExecutionAggregate) points.get(testPointGroup1);
//
//        assertNotNull(flush);
//        assertEquals(testPointGroup1, flush.getName());
//        assertEquals(group1.getMeasurements(), flush.getMeasurements());
//
//        assertEquals(group1.getTotal(), flush.getTotal(), 0.0);
//        assertEquals(group1.getMin(), flush.getMin(), 0.0);
//        assertEquals(group1.getMax(), flush.getMax(), 0.0);
//      }
//    });
  }


  /**
   * Tests many points with many threads.
   */

  public void testManyThreadsManyPoints() throws Exception {
    // still todo
  }

  protected void tearDown() throws Exception {
    monitor.stop();
    monitor.reset();
    monitor = null;
  }


  protected void setUp() throws Exception {
    monitor = new NestedMonitor();
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
      synchronized (allPoints) {
        running++;
      }
      try {

        while (runs > 0) {
          final EtmPoint point = monitor.createPoint(testPointName);

          final EtmPoint nested = monitor.createPoint("nested" + testPointName);
          Thread.sleep(1);

          nested.collect();
          Thread.sleep(1);

          point.collect();

          list.add(nested);
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
