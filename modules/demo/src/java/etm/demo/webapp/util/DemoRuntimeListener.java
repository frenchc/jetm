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
package etm.demo.webapp.util;

import etm.contrib.rrd.rrd4j.Rrd4jUtil;
import etm.core.monitor.EtmException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.net.URL;

/**
 *
 *
 * @version $Revision$
 * @author void.fm
 *
 */
public class DemoRuntimeListener implements ServletContextListener {
  private static final String DB_TEMPLATE = "etm/contrib/rrd/rrd4j/template/mediumres-db-template.xml";

  public void contextInitialized(ServletContextEvent event) {
    // create required rrd database if needed
    File file = new File(System.getProperty("java.io.tmpdir"), "jetm-demo.rrd");
    if (!file.exists()) {
      URL url = Thread.currentThread().getContextClassLoader().getResource(DB_TEMPLATE);
      if (url == null) {
        throw new EtmException("Unable to locate db template at " + DB_TEMPLATE);
      }
      Rrd4jUtil util = new Rrd4jUtil();
      util.createDb(file, url);      
    }
  }

  public void contextDestroyed(ServletContextEvent event) {
  }
}
