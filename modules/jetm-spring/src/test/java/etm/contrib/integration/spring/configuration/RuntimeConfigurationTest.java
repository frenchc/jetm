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
package etm.contrib.integration.spring.configuration;

import etm.contrib.aggregation.log.CommonsLoggingAggregator;
import etm.contrib.aggregation.log.Log4jAggregator;
import etm.contrib.renderer.plugin.Log4jDumpOnShutdownPlugin;
import etm.contrib.renderer.plugin.SystemOutDumpOnShutdownPlugin;
import etm.core.aggregation.BufferedTimedAggregator;
import etm.core.aggregation.persistence.PersistentRootAggregator;
import etm.core.metadata.AggregatorMetaData;
import etm.core.metadata.EtmMonitorMetaData;
import etm.core.metadata.PluginMetaData;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.NestedMonitor;
import etm.core.timer.DefaultTimer;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import etm.contrib.integration.spring.configuration.mockup.SpringRootAggregator;

/**
 * @author void.fm
 * @version $Revision$
 */
public class RuntimeConfigurationTest extends ConfigurationTestCase {

  public void testRuntimeFeatures() {
    ClassPathXmlApplicationContext ctx = getContext("runtime-features.xml");
    try {
      ctx.start();
      String[] monitors = ctx.getBeanNamesForType(EtmMonitor.class);
      assertEquals(1, monitors.length);

      EtmMonitor monitor = (EtmMonitor) ctx.getBean(monitors[0]);
      EtmMonitorMetaData etmMonitorMetaData = monitor.getMetaData();
      assertEquals(BufferedTimedAggregator.class, etmMonitorMetaData.getAggregatorMetaData().getImplementationClass());
      assertEquals(Log4jAggregator.class, etmMonitorMetaData.getAggregatorMetaData().getNestedMetaData().getImplementationClass());
      assertEquals(PersistentRootAggregator.class, etmMonitorMetaData.getAggregatorMetaData().getNestedMetaData().getNestedMetaData().getImplementationClass());
      assertEquals(DefaultTimer.class, etmMonitorMetaData.getTimerMetaData().getImplementationClass());

    } finally {
      ctx.destroy();
    }


  }

  public void testRuntimePlugins() {
    ClassPathXmlApplicationContext ctx = getContext("runtime-plugin.xml");
    try {
      ctx.start();
      String[] monitors = ctx.getBeanNamesForType(EtmMonitor.class);
      assertEquals(1, monitors.length);

      EtmMonitor monitor = (EtmMonitor) ctx.getBean(monitors[0]);
      EtmMonitorMetaData etmMonitorMetaData = monitor.getMetaData();

      assertEquals(2, etmMonitorMetaData.getPluginMetaData().size());
      PluginMetaData pluginOne = (PluginMetaData) etmMonitorMetaData.getPluginMetaData().get(0);
      assertEquals(Log4jDumpOnShutdownPlugin.class, pluginOne.getImplementationClass());
      assertEquals("fooBar", pluginOne.getProperties().get("logName"));
      PluginMetaData pluginTwo = (PluginMetaData) etmMonitorMetaData.getPluginMetaData().get(1);
      assertEquals(SystemOutDumpOnShutdownPlugin.class, pluginTwo.getImplementationClass());

    } finally {
      ctx.stop();
    }

  }


  public void testRuntimeChain() {
    ClassPathXmlApplicationContext ctx = getContext("runtime-chain.xml");
    try {
      ctx.start();
      String[] monitors = ctx.getBeanNamesForType(EtmMonitor.class);
      assertEquals(1, monitors.length);

      EtmMonitor monitor = (EtmMonitor) ctx.getBean(monitors[0]);
      assertEquals(NestedMonitor.class, monitor.getClass());

      EtmMonitorMetaData etmMonitorMetaData = monitor.getMetaData();
      AggregatorMetaData firstElement = etmMonitorMetaData.getAggregatorMetaData();
      assertEquals(BufferedTimedAggregator.class, firstElement.getImplementationClass());

      AggregatorMetaData secondElement = firstElement.getNestedMetaData();
      assertEquals(CommonsLoggingAggregator.class, secondElement.getImplementationClass());

      AggregatorMetaData thirdElement = secondElement.getNestedMetaData();
      assertEquals(SpringRootAggregator.class, thirdElement.getImplementationClass());

    } finally {
      ctx.stop();
    }

  }
}
