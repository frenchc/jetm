/*
 *
 * Copyright (c) 2004, 2005, 2006 void.fm
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

package test.etm.contrib.aop;

import etm.core.aggregation.ExecutionAggregate;
import etm.core.monitor.EtmMonitor;
import etm.core.renderer.MeasurementRenderer;
import junit.framework.TestCase;
import test.etm.contrib.aop.resources.BarService;
import test.etm.contrib.aop.resources.FooService;
import test.etm.contrib.aop.resources.YaddaService;

import java.util.Map;

/**
 * Testbase for AOP enhanced service classes.
 *
 * @author void.fm
 * @version $Id$
 */
public abstract class AopTestBase extends TestCase {

  protected EtmMonitor etmMonitor;
  protected YaddaService yaddaService;
  protected BarService barService;
  protected FooService fooService;
  protected static final int MEASUREMENTS = 10;

  public void testMethodCallInterceptor() throws Exception {

    int i = MEASUREMENTS;
    while (i > 0) {
      yaddaService.doYadda();
      yaddaService.doYaddaYadda();
      i--;
    }

    i = MEASUREMENTS;
    while (i > 0) {
      barService.doBar();
      barService.doBarBar();
      i--;
    }

    i = MEASUREMENTS;
    while (i > 0) {
      fooService.doFoo();
      fooService.doFooFoo();
      i--;
    }

    //etmMonitor.render(new SimpleTextRenderer());

    etmMonitor.render(new MeasurementRenderer() {
      public void render(Map points) {
        assertTrue("No measurement result found.", points.size() > 0);
        ExecutionAggregate topLevelBar = ((ExecutionAggregate) points.get("BarService::doBar"));
        assertTrue(topLevelBar.getTotal() > 0);
        assertTrue(topLevelBar.getMin() > 0);
        assertTrue(topLevelBar.getMax() > 0);
        assertTrue(topLevelBar.getMin() < topLevelBar.getMax());
        assertEquals(topLevelBar.getAverage() * topLevelBar.getMeasurements(), topLevelBar.getTotal(), 0.001d);

        assertEquals(MEASUREMENTS, topLevelBar.getMeasurements());

        assertEquals(MEASUREMENTS, ((ExecutionAggregate) points.get("BarService::doBarBar")).getMeasurements());
        assertEquals(MEASUREMENTS, ((ExecutionAggregate) points.get("FooService::doFoo")).getMeasurements());
        assertEquals(MEASUREMENTS, ((ExecutionAggregate) points.get("FooService::doFooFoo")).getMeasurements());

        ExecutionAggregate doYadda = ((ExecutionAggregate) points.get("YaddaService::doYadda"));
        assertEquals(MEASUREMENTS, doYadda.getMeasurements());
        assertTrue(doYadda.hasChilds());
        assertEquals(MEASUREMENTS * 2, ((ExecutionAggregate) doYadda.getChilds().get("BarService::doBar")).getMeasurements());

        ExecutionAggregate doYaddaYadda = ((ExecutionAggregate) points.get("YaddaService::doYaddaYadda"));
        assertEquals(MEASUREMENTS, doYaddaYadda.getMeasurements());
        assertTrue(doYaddaYadda.hasChilds());

        ExecutionAggregate doBar = ((ExecutionAggregate) doYaddaYadda.getChilds().get("BarService::doBar"));
        assertEquals(MEASUREMENTS * 2, doBar.getMeasurements());
        assertTrue(doBar.hasChilds());
        assertEquals(MEASUREMENTS * 2, ((ExecutionAggregate) doBar.getChilds().get("FooService::doFoo")).getMeasurements());

        ExecutionAggregate doBarBar = ((ExecutionAggregate) doYaddaYadda.getChilds().get("BarService::doBarBar"));
        assertEquals(MEASUREMENTS, doBarBar.getMeasurements());
        assertTrue(doBarBar.hasChilds());
        assertEquals(MEASUREMENTS, ((ExecutionAggregate) doBarBar.getChilds().get("FooService::doFoo")).getMeasurements());
        assertEquals(MEASUREMENTS, ((ExecutionAggregate) doBarBar.getChilds().get("FooService::doFooFoo")).getMeasurements());

      }
    });

  }


}
