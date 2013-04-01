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

package etm.contrib.integration.cdi;

import etm.contrib.integration.cdi.de.openknowledge.cdi.common.spi.AbstractCdiBean;
import etm.contrib.integration.cdi.de.openknowledge.cdi.common.spi.DelegatingAnnotatedType;
import etm.core.configuration.BasicEtmConfigurator;
import etm.core.configuration.EtmManager;
import etm.core.configuration.EtmMonitorConfig;
import etm.core.configuration.EtmMonitorFactory;
import etm.core.configuration.XmlConfigParser;
import etm.core.monitor.EtmException;
import etm.core.monitor.EtmMonitor;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.util.AnnotationLiteral;
import java.io.InputStream;
import java.lang.annotation.Annotation;

/**
 * A CDI extension that activates performance monitoring using basic or xml based ETM configurations.
 * Tries to locate a valid <i>jetm-config.xml</i> and uses {@link etm.core.configuration.BasicEtmConfigurator}
 * as fallback.
 * <p/>
 * This extension delays auto start to <i>AfterDeploymentValidationPhase</i>.
 *
 * @author void.fm
 * @version $Revision$
 * @since 1.3.0
 */
public class QualifiedEtmExtension implements Extension {

  private static final String DEFAULT_CONFIG_FILE = "jetm-config.xml";

  private EtmMonitorConfig monitorConfig;
  private boolean delayedAutoStart;
  private EtmMonitor etmMonitor;

  public void beforeScan(@Observes BeforeBeanDiscovery event) {
    InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(DEFAULT_CONFIG_FILE);

    try {
      if (in != null) {
        monitorConfig = XmlConfigParser.extractConfig(in);
        delayedAutoStart = monitorConfig.isAutostart();
        monitorConfig.setAutostart(false);
      } else {
        delayedAutoStart = true;
      }


    } catch (Exception e) {
      throw new EtmException(e);
    }
  }

  public void afterScan(@Observes AfterBeanDiscovery event, BeanManager bm) {
    try {
      if (monitorConfig != null) {
        etmMonitor = EtmMonitorFactory.createEtmMonitor(monitorConfig);
        CdiEtmManager.configure(etmMonitor);
      } else {
        BasicEtmConfigurator.configure(true);
        etmMonitor = EtmManager.getEtmMonitor();
      }

    } catch (Exception e) {
      event.addDefinitionError(e);
      return;
    }

    event.addBean(new EtmMonitorBean(bm, etmMonitor));
  }

  public void afterDeployment(@Observes AfterDeploymentValidation event) {
    if (delayedAutoStart) {
      etmMonitor.start();
    }
  }


  public <T> void addMeasurement(@Observes ProcessAnnotatedType<T> event) {
    AnnotatedType<T> annotatedType = event.getAnnotatedType();
    if (!annotatedType.isAnnotationPresent(Measure.class) && qualifies(annotatedType)) {
      event.setAnnotatedType(new DelegatingAnnotatedType<T>(annotatedType, new AnnotationLiteral<Measure>() {
      }));
    }
  }

  private <T> boolean qualifies(AnnotatedType<T> aAnnotatedType) {
    // TODO metadata parsing
    return false;
  }


  /**
   * @author void.fm
   * @version $Revision$
   * @since 1.3.0
   */
  static class EtmMonitorBean extends AbstractCdiBean<EtmMonitor> {

    private EtmMonitor etmMonitor;

    public EtmMonitorBean(BeanManager beanManager, EtmMonitor aEtmMonitor) {
      super(null, EtmMonitor.class, beanManager);
      etmMonitor = aEtmMonitor;
    }

    public EtmMonitor create(CreationalContext<EtmMonitor> context) {
      return etmMonitor;
    }

    public void destroy(EtmMonitor instance, CreationalContext<EtmMonitor> context) {
      instance.stop();
    }

    @Override
    public Class<? extends Annotation> getScope() {
      return ApplicationScoped.class;
    }
  }

  /**
   * @author void.fm
   * @version $Revision$
   * @since 1.3.0
   */
  static class CdiEtmManager extends EtmManager {

    /**
     * Sets the EtmMonitor.
     *
     * @param aEtmMonitor The new EtmMonitor which will be returned by {#getEtmMonitor}.
     */
    protected static void configure(EtmMonitor aEtmMonitor) {
      EtmManager.configure(aEtmMonitor);
    }
  }
}