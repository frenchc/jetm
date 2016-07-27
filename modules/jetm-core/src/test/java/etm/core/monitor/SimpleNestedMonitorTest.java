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


package etm.core.monitor;

import etm.core.aggregation.Aggregate;
import etm.core.aggregation.ExecutionAggregate;
import etm.core.renderer.MeasurementRenderer;
import etm.core.TestHelper;

import java.util.Map;

/**
 * @author void.fm
 * @version $Revision$
 */
public class SimpleNestedMonitorTest extends CommonMonitorTests {


  /**
   * Test different nested levels
   * @throws Exception Unexpected exception
   */
  public void testNestedLevels() throws Exception {


    final EtmPoint point = monitor.createPoint("test");
    Thread.sleep(10);

    final EtmPoint point10 = monitor.createPoint("test-nest10");
    Thread.sleep(10);

    // inner measurements
    final EtmPoint point20 = monitor.createPoint("test-nest20");
    Thread.sleep(10);
    point20.collect();

    final EtmPoint point21 = monitor.createPoint("test-nest21");
    Thread.sleep(10);
    point21.collect();

    point10.collect();

    final EtmPoint point11 = monitor.createPoint("test-nest11");
    Thread.sleep(10);
    point11.collect();

    final EtmPoint point12 = monitor.createPoint("test-nest12");
    Thread.sleep(10);
    point12.collect();

    point.collect();


    final EtmPoint pointNew = monitor.createPoint("test");
    Thread.sleep(10);

    final EtmPoint pointNew10 = monitor.createPoint("test-nest10");
    Thread.sleep(10);

    pointNew10.collect();
    pointNew.collect();


    monitor.render(new MeasurementRenderer() {
      public void render(Map points) {
        assertNotNull(points);
        assertEquals(1, points.size());

        assertEquals(8, new TestHelper().countExecutions(points));

        // analyze point one
        ExecutionAggregate aggregate = (ExecutionAggregate) points.get("test");

        assertNotNull(aggregate);
        assertEquals("test", aggregate.getName());
        assertEquals(2, aggregate.getMeasurements());

        assertEquals(point.getTransactionTime() + pointNew.getTransactionTime(), aggregate.getTotal(), 0.0);
        assertEquals(pointNew.getTransactionTime(), aggregate.getMin(), 0.0);
        assertEquals(point.getTransactionTime(), aggregate.getMax(), 0.0);

        // check nested point
        assertTrue(aggregate.hasChilds());
        Map childs = aggregate.getChilds();

        assertEquals(3, childs.size());


        ExecutionAggregate aggregate10 = (ExecutionAggregate) childs.get("test-nest10");

        assertNotNull(aggregate10);
        assertEquals("test-nest10", aggregate10.getName());
        assertEquals(2, aggregate10.getMeasurements());


        assertTrue(aggregate10.hasChilds());
        Map childs20 = aggregate10.getChilds();

        assertEquals(2, childs20.size());

        Aggregate aggregate20 = (Aggregate) childs20.get("test-nest20");

        assertNotNull(aggregate20);
        assertEquals("test-nest20", aggregate20.getName());
        assertEquals(1, aggregate20.getMeasurements());

        Aggregate aggregate21 = (Aggregate) childs20.get("test-nest21");

        assertNotNull(aggregate21);
        assertEquals("test-nest21", aggregate21.getName());
        assertEquals(1, aggregate21.getMeasurements());

//        assertTrue(aggregate3.hasChilds());
//        Map childs3 = aggregate3.getChilds();
//        assertEquals(2, childs3.size());
//
//
//        ExecutionAggregate aggregate40 = (ExecutionAggregate) childs3.get("test-nest30");
//
//        assertNotNull(aggregate40);
//        assertEquals("test-nest30", aggregate40.getName());
//        assertEquals(1, aggregate40.getMeasurements());
//
//
//        ExecutionAggregate aggregate41 = (ExecutionAggregate) childs3.get("test-nest31");
//
//        assertNotNull(aggregate41);
//        assertEquals("test-nest31", aggregate41.getName());
//        assertEquals(1, aggregate41.getMeasurements());


      }
    });


  }


  /**
   * Tests adding one nesting etm point.
   *
   * @throws Exception Any unexpected exception.    
   */

  public void testSingleNestedPoints() throws Exception {
    final EtmPoint point = monitor.createPoint("test");
    Thread.sleep(10);

    final EtmPoint point2 = monitor.createPoint("test2");
    Thread.sleep(5);
    point2.collect();

    point.collect();




    monitor.render(new MeasurementRenderer() {
      public void render(Map points) {
        assertNotNull(points);
        assertTrue(points.size() == 1);

        assertEquals(2, new TestHelper().countExecutions(points));

        // analyze point one
        ExecutionAggregate aggregate = (ExecutionAggregate) points.get("test");

        assertNotNull(aggregate);
        assertEquals("test", aggregate.getName());
        assertEquals(1, aggregate.getMeasurements());

        assertEquals(point.getTransactionTime(), aggregate.getTotal(), 0.0);
        assertEquals(point.getTransactionTime(), aggregate.getMin(), 0.0);
        assertEquals(point.getTransactionTime(), aggregate.getMax(), 0.0);

        // check nested point
        assertTrue(aggregate.hasChilds());
        Map childs = aggregate.getChilds();

        assertEquals(1, childs.size());


        Aggregate aggregate2 = (Aggregate) childs.get("test2");

        assertNotNull(aggregate2);
        assertEquals("test2", aggregate2.getName());
        assertEquals(1, aggregate2.getMeasurements());

        assertEquals(point2.getTransactionTime(), aggregate2.getTotal(), 0.0);
        assertEquals(point2.getTransactionTime(), aggregate2.getMin(), 0.0);
        assertEquals(point2.getTransactionTime(), aggregate2.getMax(), 0.0);

      }
    });
  }


  /**
   * Tests adding multiple nesting etm point.
   *
   * @throws Exception Any unexpected exception.
   *
   */

  public void testMultipleNestedPoints() throws Exception {
    final EtmPoint point = monitor.createPoint("test");
    Thread.sleep(15);

    final EtmPoint point2 = monitor.createPoint("test2");
    Thread.sleep(5);
    point2.collect();

    final EtmPoint point3 = monitor.createPoint("test3");
    Thread.sleep(10);
    point3.collect();

    point.collect();

    monitor.render(new MeasurementRenderer() {
      public void render(Map points) {
        assertNotNull(points);
        assertTrue(points.size() == 1);

        assertEquals(3, new TestHelper().countExecutions(points));

        // analyze point one
        ExecutionAggregate aggregate = (ExecutionAggregate) points.get("test");

        assertNotNull(aggregate);
        assertEquals("test", aggregate.getName());
        assertEquals(1, aggregate.getMeasurements());

        assertEquals(point.getTransactionTime(), aggregate.getTotal(), 0.0);
        assertEquals(point.getTransactionTime(), aggregate.getMin(), 0.0);
        assertEquals(point.getTransactionTime(), aggregate.getMax(), 0.0);

        // check nested point
        assertTrue(aggregate.hasChilds());
        Map childs = aggregate.getChilds();

        assertEquals(2, childs.size());


        Aggregate aggregate2 = (Aggregate) childs.get("test2");

        assertNotNull(aggregate2);
        assertEquals("test2", aggregate2.getName());
        assertEquals(1, aggregate2.getMeasurements());

        assertEquals(point2.getTransactionTime(), aggregate2.getTotal(), 0.0);
        assertEquals(point2.getTransactionTime(), aggregate2.getMin(), 0.0);
        assertEquals(point2.getTransactionTime(), aggregate2.getMax(), 0.0);

        Aggregate aggregate3 = (Aggregate) childs.get("test3");

        assertNotNull(aggregate3);
        assertEquals("test3", aggregate3.getName());
        assertEquals(1, aggregate3.getMeasurements());

        assertEquals(point3.getTransactionTime(), aggregate3.getTotal(), 0.0);
        assertEquals(point3.getTransactionTime(), aggregate3.getMin(), 0.0);
        assertEquals(point3.getTransactionTime(), aggregate3.getMax(), 0.0);

      }
    });
  }


  /**
   * Test Deep Nesting
   *
   * @throws Exception Any unexpected exception.
   */

  public void testDeepNesting() throws Exception {

    final EtmPoint[] etmPoints = new EtmPoint[10];

    doNested(etmPoints, etmPoints.length);


    monitor.render(new MeasurementRenderer() {
      public void render(Map points) {
        assertNotNull(points);
        assertTrue(points.size() == 1);

        assertEquals(10, new TestHelper().countExecutions(points));

        analyzeNested(points, etmPoints, etmPoints.length);
      }
    });

  }


  private void analyzeNested(Map aggregationPoints, EtmPoint[] aEtmPoints, int i) {
    EtmPoint current = aEtmPoints[i - 1];

    ExecutionAggregate aggregate = (ExecutionAggregate) aggregationPoints.get(current.getName());

    assertNotNull(aggregate);
    assertEquals(current.getName(), aggregate.getName());
    assertEquals(1, aggregate.getMeasurements());

    assertEquals(current.getTransactionTime(), aggregate.getTotal(), 0.0);
    assertEquals(current.getTransactionTime(), aggregate.getMin(), 0.0);
    assertEquals(current.getTransactionTime(), aggregate.getMax(), 0.0);

    i--;
    if (i != 0) {
      assertTrue(aggregate.hasChilds());
      analyzeNested(aggregate.getChilds(), aEtmPoints, i);
    }


  }

  private void doNested(EtmPoint[] points, int i) throws Exception {
    EtmPoint point = monitor.createPoint("test" + i);
    Thread.sleep(2 * i);

    points[i - 1] = point;

    i--;
    if (i != 0) {
      doNested(points, i);
    }

    point.collect();
  }


  protected void tearDown() throws Exception {
    monitor.stop();
    monitor.reset();
    monitor = null;
    super.tearDown();
  }


  protected void setUp() throws Exception {
    super.setUp();
    monitor = new NestedMonitor();
    monitor.start();
  }


}
