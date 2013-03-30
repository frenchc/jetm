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
import etm.core.monitor.EtmMonitor;
import etm.core.renderer.MeasurementRenderer;
import junit.framework.TestCase;
import org.apache.log4j.BasicConfigurator;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * HTTP request filter test.
 *
 * @author void.fm
 * @version $Revision$
 */
public class HttpRequestPerformanceFilterTest extends TestCase {


  protected Filter filter;
  protected EtmMonitor etmMonitor;

  public void setUp() throws Exception {
    EtmManager.reset();
    BasicConfigurator.configure();
    filter = new HttpRequestPerformanceFilter();
    filter.init(null);
    etmMonitor = EtmManager.getEtmMonitor();
    etmMonitor.start();
  }

  public void tearDown() {
    etmMonitor.stop();
  }

  public void testHttpRequestMonitoring() throws Exception {

    filter.doFilter(getHttpServletRequest(), null, getFilterChain());
    etmMonitor.render(new MeasurementRenderer() {

      public void render(Map points) {
        String key = "POST request /test/testrequest";
        assertTrue(points.containsKey(key));
        Aggregate aggregate = (Aggregate) points.get(key);
        assertEquals(1, aggregate.getMeasurements());
        assertEquals(15d, aggregate.getMin(), 0);
      }
    });

  }

  protected FilterChain getFilterChain() {
    return new FilterChain() {

      public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {
        try {
          Thread.sleep(15);
        } catch (InterruptedException e) {
          // ignored
        }
      }
    };
  }

  private ServletRequest getHttpServletRequest() throws Exception {
    return (ServletRequest) Proxy.newProxyInstance(getClass().getClassLoader(),
      new Class[]{HttpServletRequest.class},
      new InvocationHandler() {

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
          if (method.getName().equals("toString")) {
            return HttpServletRequest.class.getName();
          } else if (method.getName().equals("getRequestURI")) {
            return "/test/testrequest";
          } else if (method.getName().equals("getMethod")) {
            return "POST";
          } else {
            return null;
          }
        }
      });

  }
}
