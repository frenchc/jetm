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


package etm.contrib.integration.web;

import etm.core.aggregation.Aggregate;
import etm.core.configuration.EtmManager;
import etm.core.renderer.MeasurementRenderer;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @author void.fm
 * @version $Revision$
 */
public class SoapActionPerformanceFilterTest extends HttpRequestPerformanceFilterTest {

  public void setUp() throws Exception {
    EtmManager.reset();
    Configurator.initialize(new DefaultConfiguration());
    filter = new SoapActionPerformanceFilter();
    filter.init(null);

    etmMonitor = EtmManager.getEtmMonitor();
    etmMonitor.start();
  }

  public void tearDown() {
    etmMonitor.stop();
  }

  public void testSoapActionMonitoring() throws Exception {
    filter.doFilter(getSoapActionRequest(), null, getFilterChain());
    etmMonitor.render(new MeasurementRenderer() {

      public void render(Map points) {
        String key = "SoapAction ASoapRequest";
        assertTrue(points.containsKey(key));
        Aggregate aggregate = (Aggregate) points.get(key);
        assertEquals(1, aggregate.getMeasurements());
        assertEquals(15d, aggregate.getMin(), 0);
      }
    });
  }

  private ServletRequest getSoapActionRequest() throws Exception {
    return (ServletRequest) Proxy.newProxyInstance(getClass().getClassLoader(),
      new Class[]{HttpServletRequest.class},
      new InvocationHandler() {
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
          if (method.getName().equals("toString")) {
            return HttpServletRequest.class.getName();
          } else if (method.getName().equals("getHeader") && args[0].equals("SoapAction")) {
            return "ASoapRequest";
          } else {
            return null;
          }
        }
      });
  }
}
