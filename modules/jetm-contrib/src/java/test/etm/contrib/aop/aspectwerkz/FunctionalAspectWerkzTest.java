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

package test.etm.contrib.aop.aspectwerkz;

import junit.framework.TestCase;
import etm.core.configuration.BasicEtmConfigurator;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.renderer.MeasurementRenderer;
import etm.contrib.aop.aspectwerkz.EtmAspectWerkzAspect;

import java.util.Map;
import java.lang.reflect.Modifier;

import org.codehaus.aspectwerkz.joinpoint.JoinPoint;
import org.codehaus.aspectwerkz.joinpoint.Rtti;
import org.codehaus.aspectwerkz.joinpoint.StaticJoinPoint;
import org.codehaus.aspectwerkz.joinpoint.Signature;
import org.codehaus.aspectwerkz.joinpoint.EnclosingStaticJoinPoint;
import org.codehaus.aspectwerkz.joinpoint.management.JoinPointType;
import test.etm.contrib.aop.resources.FooService;

/**
 * Tests aspectwerkz support
 * method interceptor with and withput an exception.
 *
 * @author void.fm
 * @version $Id$
 */
public class FunctionalAspectWerkzTest extends TestCase {

  private int counter = 0;

  public void testAspectWerkzAspect() throws Throwable {
    EtmManager.reset();
    BasicEtmConfigurator.configure();
    EtmMonitor monitor = EtmManager.getEtmMonitor();
    monitor.start();
    try {
      EtmAspectWerkzAspect aspect = new EtmAspectWerkzAspect();

      aspect.monitor(new DummyJoinPoint());
      try {
        aspect.monitor(new DummyJoinPoint());
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

  class DummyJoinPoint implements JoinPoint {
    private FooService fooService = new FooService();


    public Signature getSignature() {
      return new Signature() {

        public Class getDeclaringType() {
          return FooService.class;
        }

        public int getModifiers() {
          return Modifier.PUBLIC;
        }

        public String getName() {
          return "doFoo";
        }
      };
    }

    public Object proceed() throws Throwable {
      counter++;
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

    public Object getCallee() {
      return null;
    }

    public Object getCaller() {
      return null;
    }

    public Object getTarget() {
      return null;
    }

    public Rtti getRtti() {
      return null;
    }

    public StaticJoinPoint copy() {
      return null;
    }

    public Object getMetaData(Object object) {
      return null;
    }

    public void addMetaData(Object object, Object object1) {

    }

    public Class getCallerClass() {
      return null;
    }

    public Class getCalleeClass() {
      return null;
    }

    public Class getTargetClass() {
      return null;
    }

    public JoinPointType getType() {
      return null;
    }

    public EnclosingStaticJoinPoint getEnclosingStaticJoinPoint() {
      return null;
    }
  }
}
