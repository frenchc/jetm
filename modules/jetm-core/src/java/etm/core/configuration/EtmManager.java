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

package etm.core.configuration;

import etm.core.monitor.EtmMonitor;
import etm.core.monitor.NullMonitor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Static helper class for accessing the EtmMonitor instance
 * created by EtmConfigurators such as {@link BasicEtmConfigurator}
 * or {@link XmlEtmConfigurator}
 *
 * @author void.fm
 * @version $Revision$
 */
public class EtmManager {

  private static EtmMonitor etmMonitor;
  private static boolean isProxy;
  private static MonitorProxyInvocationHandler handler = new MonitorProxyInvocationHandler();


  static {
    init();
  }


  private EtmManager() {
  }

  /**
   * Sets the EtmMonitor.
   *
   * @param aEtmMonitor The new EtmMonitor which will be returned by {#getEtmMonitor}.
   */
  protected static void configure(EtmMonitor aEtmMonitor) {
    if (isProxy) {
      handler.proxyMonitor = aEtmMonitor;
      etmMonitor = aEtmMonitor;
      isProxy = false;
    }
  }

  /**
   * Returns the currently configured EtmMonitor instance.
   *
   * @return The etmMonitor.
   */

  public static EtmMonitor getEtmMonitor() {
    return etmMonitor;
  }

  /**
   * Resets the current EtmManager. Be aware that cached monitor
   * instance outside EtmManager will not be reset.
   */
  public static void reset() {
    init();
    handler.proxyMonitor = new NullMonitor();
  }

  private static void init() {
    etmMonitor = (EtmMonitor) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
      new Class[]{EtmMonitor.class},
      handler
    );
    isProxy = true;
  }

  /**
   * Simple proxy that allows late EtmManager init.
   */
  static class MonitorProxyInvocationHandler implements InvocationHandler {
    private EtmMonitor proxyMonitor = new NullMonitor();

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      return method.invoke(proxyMonitor, args);
    }
  }
}
