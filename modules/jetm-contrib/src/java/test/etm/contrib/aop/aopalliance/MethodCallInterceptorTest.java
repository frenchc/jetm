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
package test.etm.contrib.aop.aopalliance;

import etm.contrib.aop.aopalliance.EtmMethodCallInterceptor;
import etm.core.monitor.EtmMonitor;
import etm.core.renderer.MeasurementRenderer;
import etm.core.configuration.BasicEtmConfigurator;
import etm.core.configuration.EtmManager;
import junit.framework.TestCase;
import org.aopalliance.intercept.MethodInvocation;
import test.etm.contrib.aop.resources.FooService;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Simply test the default aop alliance
 * method interceptor with and withput an exception.
 *
 * @author void.fm
 * @version $Revision$
 */
public class MethodCallInterceptorTest extends TestCase {

  private int counter = 0;

  public void testMethodCallInterceptor() throws Throwable {
    EtmManager.reset();
    BasicEtmConfigurator.configure();
    EtmMonitor monitor = EtmManager.getEtmMonitor();
    monitor.start();
    try {
      EtmMethodCallInterceptor interceptor = new EtmMethodCallInterceptor(monitor);

      interceptor.invoke(new DummyMethodInvocation());
      try {
        interceptor.invoke(new DummyMethodInvocation());
        fail("An exception should have been thrown.");
      } catch (Exception e) {
        // ignored since expected
      }

      monitor.render(new MeasurementRenderer() {
        public void render(Map points) {
          assertTrue(points.size() > 0);
          assertNotNull(points.get("FooService::doFoo"));
          assertNotNull(points.get("FooService::doFoo [Exception]"));
        }
      });
    } finally {
      monitor.stop();
    }
  }

  class DummyMethodInvocation implements MethodInvocation {

    private FooService fooService = new FooService();

    public Method getMethod() {
      try {
        return fooService.getClass().getMethod("doFoo", new Class[]{});
      } catch (NoSuchMethodException e) {
        throw new RuntimeException(e.toString());
      }
    }

    public Object[] getArguments() {
      return new Object[0];
    }

    public Object proceed() throws Throwable {
      counter ++;
      switch (counter % 2) {
        case 0:
          throw new Exception("Test Exception.");
        case 1:
          fooService.doFoo();
          return null;
        default:
          throw new RuntimeException("Unexpected Exception");
      }
    }

    public Object getThis() {
      return fooService;
    }

    public AccessibleObject getStaticPart() {
      throw new UnsupportedOperationException();
    }
  }
}
