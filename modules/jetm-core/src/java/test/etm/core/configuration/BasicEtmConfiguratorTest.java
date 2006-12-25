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

package test.etm.core.configuration;

import junit.framework.TestCase;
import etm.core.configuration.EtmManager;
import etm.core.configuration.BasicEtmConfigurator;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.MeasurementPoint;
import etm.core.monitor.NullMonitor;
import etm.core.monitor.FlatMonitor;
import etm.core.monitor.NestedMonitor;
import etm.core.renderer.MeasurementRenderer;

import java.util.Map;
import java.lang.reflect.Proxy;

/**
 *
 * Testing basic configurator behavior.
 *
 * @version $Id$
 * @author void.fm
 */
public class BasicEtmConfiguratorTest extends TestCase {

  public void testNoConfiguration() {
    EtmManager.reset();

    EtmMonitor etmMonitor = EtmManager.getEtmMonitor();
    assertTrue(Proxy.isProxyClass(etmMonitor.getClass()));

    etmMonitor.start();

    MeasurementPoint point = new MeasurementPoint(etmMonitor, "testPoint");
    point.collect();

    etmMonitor.render(new MeasurementRenderer() {
      public void render(Map points) {
        assertNotNull(points);
        assertEquals(0, points.size());
      }
    });


    etmMonitor.stop();
  }

  public void testFlatConfiguration() {
    EtmManager.reset();

    BasicEtmConfigurator.configure();
    EtmMonitor etmMonitor = EtmManager.getEtmMonitor();
    assertTrue(etmMonitor instanceof FlatMonitor);

    etmMonitor.start();

    MeasurementPoint point = new MeasurementPoint(etmMonitor, "testPoint");
    point.collect();

    etmMonitor.render(new MeasurementRenderer() {
      public void render(Map points) {
        assertNotNull(points);
        assertEquals(1, points.size());
      }
    });


    etmMonitor.stop();
  }

  public void testNestedConfiguration() {
    EtmManager.reset();

    BasicEtmConfigurator.configure(true);
    EtmMonitor etmMonitor = EtmManager.getEtmMonitor();
    assertTrue(etmMonitor instanceof NestedMonitor);

    etmMonitor.start();

    MeasurementPoint point = new MeasurementPoint(etmMonitor, "testPoint");
    point.collect();

    etmMonitor.render(new MeasurementRenderer() {
      public void render(Map points) {
        assertNotNull(points);
        assertEquals(1, points.size());
      }
    });


    etmMonitor.stop();
  }


}
