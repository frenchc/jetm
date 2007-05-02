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

package etm.contrib.aop.common;

import etm.core.configuration.BasicEtmConfigurator;
import etm.core.configuration.EtmManager;
import etm.core.configuration.XmlEtmConfigurator;
import etm.core.monitor.EtmException;
import etm.core.util.Log;
import etm.core.util.LogAdapter;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * A class that may be used to enable / disable JETM performance monitoring.
 * Bind this class to a life cycle method within your application that safely
 * will be started/stopped. See aspectwerkz for example usage.
 *
 * @author void.fm
 * @version $Revision$
 */

public class EtmLifecycleAspect {
  private static final LogAdapter log = Log.getLog(EtmLifecycleAspect.class);

  private static final String DEFAULT_CONFIG_FILE = "jetm-config.xml";

  /**
   * Enables performance monitoring
   */
  public void enableBasicConfig() {
    if (!EtmManager.getEtmMonitor().isStarted()) {
      BasicEtmConfigurator.configure(true);
      EtmManager.getEtmMonitor().start();
    } else {
      log.warn("Etm subsystem already initialized. Ignoring init.");
    }
  }

  public void enableXmlConfig() {
    enableXmlConfigByClasspathResource(DEFAULT_CONFIG_FILE);
  }


  public void enableXmlConfigByUrl(String url) {
    try {
      enable(new URL(url));
    } catch (MalformedURLException e) {
      throw new EtmException(e);
    }
  }

  public void enableXmlConfigByClasspathResource(String classpathResource) {
    URL resource = Thread.currentThread().getContextClassLoader().getResource(classpathResource);
    if (resource != null) {
      enable(resource);
    } else {
      throw new EtmException("Unable to locate default resource " + classpathResource + " in classpath.");
    }
  }

  protected void enable(URL url) {
    if (!EtmManager.getEtmMonitor().isStarted()) {
      XmlEtmConfigurator.configure(url);
      EtmManager.getEtmMonitor().start();
    } else {
      log.warn("Etm subsystem already initialized. Ignoring init.");
    }
  }

  public void disable() {
    EtmManager.getEtmMonitor().stop();
  }
}
