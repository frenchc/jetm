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
package etm.contrib.integration.jca;

import etm.core.configuration.EtmManager;
import etm.core.configuration.XmlEtmConfigurator;
import etm.core.monitor.EtmMonitor;
import etm.core.util.Log;
import etm.core.util.LogAdapter;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.resource.Referenceable;
import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.transaction.xa.XAResource;
import java.io.Serializable;
import java.net.URL;

/**
 * A JCA connector that may be used to initialize and shutdown a JETM runtime within a Java EE environment.
 * Supports both static or JNDI name exposal.
 *
 * @author void.fm
 * @version $Revision$
 * @since 1.2.2
 */
public class EtmManagerConnector implements ResourceAdapter, Referenceable, Serializable {
  private static final LogAdapter log = Log.getLog(EtmManagerConnector.class);

  private String jndiName;
  private static final String DEFAULT_CONFIG_FILE_NAME = "jetm-config.xml";
  private String configFileName = DEFAULT_CONFIG_FILE_NAME;

  public void setConfigFile(String fileName) {
    configFileName = fileName;
  }

  public void setJndiName(String aJndiName) {
    jndiName = aJndiName;
  }

  public void start(BootstrapContext aBootstrapContext) throws ResourceAdapterInternalException {
    ClassLoader loader = EtmMonitor.class.getClassLoader();
    URL resource = loader.getResource(configFileName);

    if (resource == null) {
      throw new ResourceAdapterInternalException("Unable to locate JETM config file " + configFileName + " in classpath.");
    }

    log.debug("Using JETM configuration file " + resource);

    if (jndiName == null) {
      // static usage
      XmlEtmConfigurator.configure(resource);
    } else {
      // jndi usage
      throw new UnsupportedOperationException("JNDI Bind currently not supported.");
//      InputStream in = null;
//      InitialContext ctx = null;
//      try {
//        in = resource.openStream();
//        EtmMonitorConfig monitorConfig = XmlConfigParser.extractConfig(in);
//        monitor = EtmMonitorFactory.createEtmMonitor(monitorConfig);
//
//        ctx = new InitialContext();
//        // todo this may not work due to missing sub contexts
//        ctx.bind(jndiName, monitor);
//
//      } catch (Exception e) {
//        throw new ResourceAdapterInternalException(e);
//      } finally {
//        if (in != null) {
//          try {
//            in.close();
//          } catch (IOException e) {
//            // ignored
//          }
//        }
//
//        if (ctx != null) {
//          try {
//            ctx.close();
//          } catch (NamingException e) {
//            // ignored
//          }
//        }
//      }
    }
    EtmManager.getEtmMonitor().start();
  }

  public void stop() {
    EtmManager.getEtmMonitor().stop();
//    if (jndiName != null) {
//      InitialContext ctx = null;
//      try {
//        ctx = new InitialContext();
//        ctx.unbind(jndiName);
//      } catch (NameNotFoundException e) {
//        // ignore
//      } catch (Exception e) {
//        log.warn("Unable to deregister JETM monitor " + jndiName + " from JNDI tree", e);
//      } finally {
//        if (ctx != null) {
//          try {
//            ctx.close();
//          } catch (NamingException e) {
//            // ignored
//          }
//        }
//      }
//    }
  }

  public void endpointActivation(MessageEndpointFactory aMessageEndpointFactory, ActivationSpec aActivationSpec) throws ResourceException {
  }

  public void endpointDeactivation(MessageEndpointFactory aMessageEndpointFactory, ActivationSpec aActivationSpec) {
  }

  public XAResource[] getXAResources(ActivationSpec[] aActivationSpecs) throws ResourceException {
    return null;
  }

  public Reference getReference() throws NamingException {
    return new Reference(EtmManagerConnector.class.getName());
  }

  public void setReference(Reference aReference) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
