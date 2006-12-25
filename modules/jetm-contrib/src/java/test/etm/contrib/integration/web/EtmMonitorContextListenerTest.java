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

package test.etm.contrib.integration.web;

import etm.contrib.integration.web.EtmMonitorContextListener;
import etm.core.configuration.EtmManager;
import junit.framework.TestCase;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Tests for Servlet Context listeners using static Etm Configuration.
 *
 * @author void.fm
 * @version $Id$
 */
public class EtmMonitorContextListenerTest extends TestCase {

  public void testClassPath() throws Exception {
    EtmManager.reset();
    EtmMonitorContextListener listener = new EtmMonitorContextListener();

    HashMap attributes = new HashMap();
    attributes.put("jetm.config.filename", "test/etm/contrib/integration/web/classpath-config.xml");
    ServletContext ctx = getServletContext(attributes);

    assertTrue(Proxy.isProxyClass(EtmManager.getEtmMonitor().getClass()));
    listener.contextInitialized(new ServletContextEvent(ctx));

    assertFalse(Proxy.isProxyClass(EtmManager.getEtmMonitor().getClass()));
    assertTrue(EtmManager.getEtmMonitor().isStarted());

    listener.contextDestroyed(new ServletContextEvent(ctx));
    assertFalse(EtmManager.getEtmMonitor().isStarted());
  }

  public void testFilePath() throws Exception {
    File file = new File(System.getProperty("java.io.tmpdir"), "jetm-config.xml");
    try {
      FileOutputStream out = new FileOutputStream(file);
      out.write(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<!DOCTYPE jetm-config PUBLIC \"-// void.fm //DTD JETM Config 1.0//EN\" \"http://jetm.void.fm/dtd/jetm_config_1_0.dtd\">\n" +
        "<jetm-config>\n" +
        "\n" +
        "</jetm-config>").getBytes());
      EtmManager.reset();
      EtmMonitorContextListener listener = new EtmMonitorContextListener();

      HashMap attributes = new HashMap();
      attributes.put("jetm.config.filepath", file.getParentFile().getAbsolutePath());
      ServletContext ctx = getServletContext(attributes);

      assertTrue(Proxy.isProxyClass(EtmManager.getEtmMonitor().getClass()));
      listener.contextInitialized(new ServletContextEvent(ctx));

      assertFalse(Proxy.isProxyClass(EtmManager.getEtmMonitor().getClass()));
      assertTrue(EtmManager.getEtmMonitor().isStarted());

      listener.contextDestroyed(new ServletContextEvent(ctx));
      assertFalse(EtmManager.getEtmMonitor().isStarted());
    } finally {
      if (file.exists()){
        file.delete();
      }
    }
  }

   public void testSystemPropertyPath() throws Exception {
    File file = new File(System.getProperty("java.io.tmpdir"), "jetm-config.xml");
    try {
      FileOutputStream out = new FileOutputStream(file);
      out.write(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<!DOCTYPE jetm-config PUBLIC \"-// void.fm //DTD JETM Config 1.0//EN\" \"http://jetm.void.fm/dtd/jetm_config_1_0.dtd\">\n" +
        "<jetm-config>\n" +
        "\n" +
        "</jetm-config>").getBytes());
      EtmManager.reset();
      EtmMonitorContextListener listener = new EtmMonitorContextListener();

      HashMap attributes = new HashMap();
      attributes.put("jetm.config.filepath", "${myapp.jetm.config}");

      System.setProperty("myapp.jetm.config", file.getParentFile().getAbsolutePath());

      ServletContext ctx = getServletContext(attributes);

      assertTrue(Proxy.isProxyClass(EtmManager.getEtmMonitor().getClass()));
      listener.contextInitialized(new ServletContextEvent(ctx));

      assertFalse(Proxy.isProxyClass(EtmManager.getEtmMonitor().getClass()));
      assertTrue(EtmManager.getEtmMonitor().isStarted());

      listener.contextDestroyed(new ServletContextEvent(ctx));
      assertFalse(EtmManager.getEtmMonitor().isStarted());
    } finally {
      if (file.exists()){
        file.delete();
      }
    }
  }


  private ServletContext getServletContext(final Map contextAttributes) throws Exception {
    InvocationHandler handler = new InvocationHandler() {

      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("toString")) {
          return ServletContext.class.getName();
        } else {
          return contextAttributes.get(args[0]);
        }
      }
    };

    return (ServletContext) Proxy.newProxyInstance(
      getClass().getClassLoader(),
      new Class[]{ServletContext.class},
      handler);
  }

}
