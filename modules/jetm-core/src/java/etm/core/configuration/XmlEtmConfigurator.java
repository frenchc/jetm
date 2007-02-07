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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Xml based configurator. Requires a xml configuration which is valid
 * to jetm_config DTD.
 * <p/>
 * Be aware that you need to start and stop the EtmMonitor before
 * using it. See {@link etm.core.monitor.EtmMonitor} lifecycle.
 *
 * @author void.fm
 * @version $Revision$
 */
public class XmlEtmConfigurator {

  private XmlEtmConfigurator() {
  }

  /**
   * Configures the EtmManager using the given string which represents
   * a valid XmlEtmConfigurator configuration.
   *
   * @param config The xml configuration string.
   * @throws EtmConfigurationException Thrown in case an error occures.
   */
  public static void configure(String config) {
    InputStream inStream = null;
    try {
      inStream = new ByteArrayInputStream(config.getBytes());
      doConfigure(inStream);        
    } catch (EtmConfigurationException e) {
      throw e;
    } catch (Exception e) {
      throw new EtmConfigurationException(e);
    } finally {
      if (inStream != null) {
        try {
          inStream.close();
        } catch (IOException e) {
          // ignored
        }
      }
    }
  }

  /**
   * Configures the EtmManager using the given URL.
   *
   * @param configLocation The location of the file which may be remote or locally.
   * @throws EtmConfigurationException Thrown in case an error occures.
   */
  public static void configure(URL configLocation) {
    InputStream inStream = null;

    try {
      inStream = configLocation.openStream();
      doConfigure(inStream);
    } catch (EtmConfigurationException e) {
      throw e;
    } catch (Exception e) {
      throw new EtmConfigurationException(e);
    } finally {
      if (inStream != null) {
        try {
          inStream.close();
        } catch (IOException e) {
          // ignored
        }
      }
    }
  }

  /**
   * Configures the EtmManager using the given inputStream.
   * The stream will not be closed after usage.
   *
   * @param in The inputStream to be used.
   * @throws EtmConfigurationException Thrown in case an error occures.
   */
  public static void configure(InputStream in) {
    try {
      doConfigure(in);
    } catch (EtmConfigurationException e) {
      throw e;
    } catch (Exception e) {
      throw new EtmConfigurationException(e);
    }
  }

  /**
   * Configures the EtmManager using the given file. Delegates to
   * {@link #configure(java.net.URL)} only.
   *
   * @param file The config filefile to be used.
   * @throws EtmConfigurationException Thrown in case an error occures.
   */
  public static void configure(File file) {
    try {
      configure(file.toURL());
    } catch (MalformedURLException e) {
      throw new EtmConfigurationException(e);
    }
  }

  private static void doConfigure(InputStream in) throws Exception {
    EtmMonitorConfig monitorConfig = XmlConfigParser.extractConfig(in);

    EtmMonitor etmMonitor = EtmMonitorFactory.createEtmMonitor(monitorConfig);
    EtmManager.configure(etmMonitor);
  }

}
