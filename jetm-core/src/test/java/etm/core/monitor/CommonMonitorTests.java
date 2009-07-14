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

package etm.core.monitor;

import etm.core.aggregation.Aggregate;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;
import etm.core.renderer.MeasurementRenderer;
import junit.framework.TestCase;
import etm.core.TestHelper;

import java.util.Map;

/**
 * Common tests for all monitor types.
 *
 * @author void.fm
 * @version $Revision$
 */
public abstract class CommonMonitorTests extends TestCase {

  protected EtmMonitor monitor;

  /**
   * Tests adding one etm point.
   *
   * @throws Exception Any unexpected exception.
   */

  public void testAddPoint() throws Exception {
    final EtmPoint point = monitor.createPoint("test");
    Thread.sleep(10);
    point.collect();


    monitor.render(new MeasurementRenderer() {
      public void render(Map points) {

        assertNotNull(points);
        assertEquals(1, points.size());
        assertEquals(1, new TestHelper().countExecutions(points));

        Aggregate aggregate = (Aggregate) points.get("test");

        assertNotNull(aggregate);
        assertEquals("test", aggregate.getName());
        assertEquals(1, aggregate.getMeasurements());

        assertEquals(point.getTransactionTime(), aggregate.getTotal(), 0.000001);
        assertEquals(point.getTransactionTime(), aggregate.getMin(), 0.000001);
        assertEquals(point.getTransactionTime(), aggregate.getMax(), 0.000001);
      }
    });
  }

  /**
   * Tests adding two etm points.
   *
   * @throws Exception Any unexpected exception.
   */

  public void testAddTwoPoints() throws Exception {
    final EtmPoint point = monitor.createPoint("test");
    Thread.sleep(10);
    point.collect();

    final EtmPoint point2 = monitor.createPoint("test2");
    Thread.sleep(5);
    point2.collect();

    monitor.render(new MeasurementRenderer() {
      public void render(Map points) {

        assertNotNull(points);
        assertEquals(2, points.size());
        assertEquals(2, new TestHelper().countExecutions((points)));

        // analyze point one
        Aggregate aggregate = (Aggregate) points.get("test");

        assertNotNull(aggregate);
        assertEquals("test", aggregate.getName());
        assertEquals(1, aggregate.getMeasurements());

        assertEquals(point.getTransactionTime(), aggregate.getTotal(), 0.000001);
        assertEquals(point.getTransactionTime(), aggregate.getMin(), 0.000001);
        assertEquals(point.getTransactionTime(), aggregate.getMax(), 0.000001);

        // analyze point two
        Aggregate aggregate2 = (Aggregate) points.get("test2");

        assertNotNull(aggregate2);
        assertEquals("test2", aggregate2.getName());
        assertEquals(1, aggregate2.getMeasurements());

        assertEquals(point2.getTransactionTime(), aggregate2.getTotal(), 0.000001);
        assertEquals(point2.getTransactionTime(), aggregate2.getMin(), 0.000001);
        assertEquals(point2.getTransactionTime(), aggregate2.getMax(), 0.000001);


      }
    });
  }


  /**
   * Tests aggregation of one etm point.
   *
   * @throws Exception Any kind of exception.
   */

  public void testOnePointAggregation() throws Exception {
    final EtmPoint point = monitor.createPoint("test");
    Thread.sleep(200);
    point.collect();

    final EtmPoint point2 = monitor.createPoint("test");
    Thread.sleep(2);
    point2.collect();


    monitor.render(new MeasurementRenderer() {
      public void render(Map points) {


        assertNotNull(points);
        assertTrue(points.size() == 1);
        assertEquals(2, new TestHelper().countExecutions(points));

        Aggregate aggregate = (Aggregate) points.get("test");

        assertNotNull(aggregate);
        assertEquals("test", aggregate.getName());
        assertEquals(2, aggregate.getMeasurements());

        assertEquals(point.getTransactionTime() + point2.getTransactionTime(), aggregate.getTotal(), 0.000001);
        assertEquals(point2.getTransactionTime(), aggregate.getMin(), 0.000001);
        assertEquals(point.getTransactionTime(), aggregate.getMax(), 0.000001);

      }

    });
  }

  /**
   * Tests aggregation of two etm points.
   *
   * @throws Exception Unexpected exception.
   */

  public void testTwoPointAggregation() throws Exception {
    final EtmPoint pointOne = monitor.createPoint("test");
    Thread.sleep(150);
    pointOne.collect();

    final EtmPoint pointOne2 = monitor.createPoint("test");
    Thread.sleep(100);
    pointOne2.collect();

    final EtmPoint pointTwo = monitor.createPoint("test2");
    Thread.sleep(50);
    pointTwo.collect();

    final EtmPoint pointTwo2 = monitor.createPoint("test2");
    Thread.sleep(1);
    pointTwo2.collect();


    monitor.render(new MeasurementRenderer() {
      public void render(Map points) {
        assertNotNull(points);
        assertTrue(points.size() == 2);
        assertEquals(4, new TestHelper().countExecutions(points));

        Aggregate aggregate = (Aggregate) points.get("test");

        assertNotNull(aggregate);
        assertEquals("test", aggregate.getName());
        assertEquals(2, aggregate.getMeasurements());


        assertEquals(pointOne.getTransactionTime() + pointOne2.getTransactionTime(), aggregate.getTotal(), 0.000001);
        assertEquals(pointOne2.getTransactionTime(), aggregate.getMin(), 0.000001);
        assertEquals(pointOne.getTransactionTime(), aggregate.getMax(), 0.000001);


        Aggregate aggregate2 = (Aggregate) points.get("test2");

        assertNotNull(aggregate2);
        assertEquals("test2", aggregate2.getName());
        assertEquals(2, aggregate2.getMeasurements());

        assertEquals(pointTwo.getTransactionTime() + pointTwo2.getTransactionTime(), aggregate2.getTotal(), 0.000001);
        assertEquals(pointTwo2.getTransactionTime(), aggregate2.getMin(), 0.000001);
        assertEquals(pointTwo.getTransactionTime(), aggregate2.getMax(), 0.000001);
      }
    });
  }

  /**
   * Tests wether etm points are visible before being collected.
   */

  public void testVisibility() {

    EtmPoint pointOne = monitor.createPoint("test");

    monitor.render(new MeasurementRenderer() {
      public void render(Map points) {
        assertNotNull(points);
        assertTrue(points.size() == 0);
      }
    });


    pointOne.collect();


    monitor.render(new MeasurementRenderer() {
      public void render(Map points) {
        assertNotNull(points);
        assertTrue(points.size() == 1);

        assertEquals(1, new TestHelper().countExecutions(points));

      }
    });
  }
}
