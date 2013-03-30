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

import etm.core.configuration.EtmConfigurationException;
import etm.core.configuration.EtmManager;
import etm.core.configuration.XmlEtmConfigurator;
import etm.core.monitor.EtmMonitor;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * The EtmMonitorContextListener configures and starts an EtmMonitor using
 * an {@link XmlEtmConfigurator}. By default it assumes a file called
 * <code>jetm-config.xml</code> in classpath. You enable performance monitoring
 * by adding this code fragement to your web.xml file.
 * <p/>
 * <pre>
 * &lt;listener&gt;
 *    &lt;listener-class&gt;etm.contrib.integration.web.EtmMonitorContextListener&lt;/listener-class&gt;
 * &lt;/listener&gt;
 * </pre>
 * It is possible to override the name of the file using the web app init parameter
 * <code>jetm.config.filename</code>. Example:
 * <pre>
 * &lt;context-param&gt;
 *   &lt;param-name&gt;jetm.config.filename&lt;/param-name&gt;
 *   &lt;param-value&gt;custom-config.xml&lt;/param-value&gt;
 * &lt;/context-param&gt;
 * </pre>
 * As an alternative the jetm config file may be loaded from file system. This feature can be enabled
 * by specifying <config>jetm.config.filepath</config> as context parameter. In the example below
 * the jetm config file is expected at <code>/etc/yourapp</code> under the name supplied with <code>
 * jetm.config.filename</code>. If jetm.config.filename is unset, the default name <code>jetm-config.xml</code>
 * will be used.
 * <pre>
 * &lt;context-param&gt;
 *   &lt;param-name&gt;jetm.config.filepath&lt;/param-name&gt;
 *   &lt;param-value&gt;/etc/yourapp&lt;/param-value&gt;
 * &lt;/context-param&gt;
 * </pre>
 * While this feaure sounds convinient a hard coded filepath within a web.xml descriptor is often considered
 * a design or programming error. Therefore the path to a jetm config file can be specified using
 * a system property. Just write <code>jetm.config.filepath</code> as ant style property and its
 * value will be retrieved from {@link System#getProperty(String)} using the given name. Example:
 * <p/>
 * <pre>
 * &lt;context-param&gt;
 *   &lt;param-name&gt;jetm.config.filepath&lt;/param-name&gt;
 *   &lt;param-value&gt;${myapp.jetm.path}&lt;/param-value&gt;
 * &lt;/context-param&gt;
 * </pre>
 *
 * @author void.fm
 * @version $Revision$
 */
public class EtmMonitorContextListener implements ServletContextListener {

  private static final String DEFAULT_CONFIG_FILE = "jetm-config.xml";

  private static final String CONTEXT_FILE_NAME = "jetm.config.filename";
  private static final String CONTEXT_FILE_PATH = "jetm.config.filepath";

  public void contextInitialized(ServletContextEvent servletContextEvent) {
    ServletContext ctx = servletContextEvent.getServletContext();

    String fileName = ctx.getInitParameter(CONTEXT_FILE_NAME);
    String filePath = ctx.getInitParameter(CONTEXT_FILE_PATH);
    if (filePath != null && filePath.startsWith("${") && filePath.endsWith("}")) {
      String key = filePath.substring(2, filePath.length() - 1);
      filePath = System.getProperty(key);
      if (filePath == null) {
        throw new EtmConfigurationException("Error reading jetm config filepath from System properties using name " + key);
      }
    }

    if (fileName == null) {
      fileName = DEFAULT_CONFIG_FILE;
    }

    if (filePath != null) {
      File file = new File(filePath, fileName);
      if (file.canRead()) {
        try {
          FileInputStream in = new FileInputStream(file);
          XmlEtmConfigurator.configure(in);
        } catch (Exception e) {
          throw new EtmConfigurationException("Error reading JETM configuration file at " + file.getAbsolutePath() + ". Cause:" + e.getMessage());
        }
      } else {
        throw new EtmConfigurationException("Unable to locate JETM configuration file at " + file.getAbsolutePath());
      }
    } else {
      InputStream in = EtmMonitor.class.getClassLoader().getResourceAsStream(fileName);
      if (in != null) {
        try {
          XmlEtmConfigurator.configure(in);
        } catch (Exception e) {
          throw new EtmConfigurationException("Error reading JETM configuration file  " + fileName + " from classpath. Cause:" + e.getMessage());
        }
      } else {
        throw new EtmConfigurationException("Unable to locate JETM configuration file " + fileName + " in classpath.");
      }
    }

    EtmManager.getEtmMonitor().start();
  }

  public void contextDestroyed(ServletContextEvent servletContextEvent) {
    EtmManager.getEtmMonitor().stop();
  }
}
