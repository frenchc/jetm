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

import etm.core.aggregation.Aggregator;
import etm.core.configuration.EtmManager;
import etm.core.configuration.XmlEtmConfigurator;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.FlatMonitor;
import etm.core.monitor.NestedMonitor;
import etm.core.plugin.EtmPlugin;
import etm.core.timer.DefaultTimer;
import junit.framework.TestCase;
import etm.core.configuration.mockup.TestAggregator;
import etm.core.configuration.mockup.TestMonitor;
import etm.core.configuration.mockup.TestPlugin;
import etm.core.configuration.mockup.TestTimer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Testing XML based configuration.
 *
 * @author void.fm
 * @version $Revision$
 */
public class Xml10EtmConfiguratorTest extends TestCase {

  public void testConfigFromString() {
    String config = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<!DOCTYPE jetm-config PUBLIC \"-// void.fm //DTD JETM Config 1.0//EN\" \"http://jetm.void.fm/dtd/jetm_config_1_0.dtd\">\n" +
      "<jetm-config>\n" +
      "  <monitor-type>flat</monitor-type>\n" +
      "</jetm-config>";

    EtmManager.reset();
    XmlEtmConfigurator.configure(config);

    EtmMonitor etmMonitor = EtmManager.getEtmMonitor();
    assertEquals(FlatMonitor.class, etmMonitor.getClass());

  }

  public void testMonitorConfig() throws Exception {
    Object[][] configurations = new Object[][]{
      new Object[]{
        "etm/core/configuration/files/valid_1_0/flat-type-config.xml", FlatMonitor.class
      },
      new Object[]{
        "etm/core/configuration/files/valid_1_0/nested-type-config.xml", NestedMonitor.class
      },
      new Object[]{
        "etm/core/configuration/files/valid_1_0/monitor-class-config.xml", TestMonitor.class
      }
    };

    for (int i = 0; i < configurations.length; i++) {
      URL url = locateResource((String) configurations[i][0]);
      EtmManager.reset();
      XmlEtmConfigurator.configure(url);

      EtmMonitor etmMonitor = EtmManager.getEtmMonitor();
      assertEquals(configurations[i][1], etmMonitor.getClass());
    }
  }


  public void testTimerConfig() throws Exception {
    Object[][] configurations = new Object[][]{
      new Object[]{
        "etm/core/configuration/files/valid_1_0/default-timer-config.xml", DefaultTimer.class
      },
      new Object[]{
        "etm/core/configuration/files/valid_1_0/timer-class-config.xml", TestTimer.class
      }
    };

    for (int i = 0; i < configurations.length; i++) {
      URL url = locateResource((String) configurations[i][0]);
      EtmManager.reset();
      XmlEtmConfigurator.configure(url);

      EtmMonitor etmMonitor = EtmManager.getEtmMonitor();
      assertEquals(configurations[i][1], ((TestMonitor) etmMonitor).getExecutionTimer().getClass());
    }
  }

  public void testAggregatorConfig() throws Exception {
    URL url = locateResource("test/etm/core/configuration/files/valid_1_0/aggregator-config.xml");
    EtmManager.reset();
    XmlEtmConfigurator.configure(url);

    EtmMonitor etmMonitor = EtmManager.getEtmMonitor();
    Aggregator aggregator = ((TestMonitor) etmMonitor).getAggregator();

    assertEquals(TestAggregator.class, aggregator.getClass());
    TestAggregator testAggregator = (TestAggregator) aggregator;
    assertEquals(true, testAggregator.isBooleanTrue());
    assertEquals(false, testAggregator.isBooleanFalse());
    assertEquals(12, testAggregator.getIntValue());
    assertEquals(12124234324234L, testAggregator.getLongValue());
    assertEquals("testString", testAggregator.getStringValue());
    assertEquals(HashMap.class, testAggregator.getClazzValue());

    Map map = testAggregator.getMapValue();
    assertNotNull(map);
    assertEquals(3, map.size());
    assertEquals(map.get("key1"), "value1");
    assertEquals(map.get("key2"), "value2");
    assertEquals(map.get("key3"), "value3");

    List list = testAggregator.getListValue();
    assertNotNull(list);
    assertEquals(2, list.size());
    assertEquals(list.get(0), "value1");
    assertEquals(list.get(1), "value2");

  }


  public void testPluginConfig() throws Exception {
    URL url = locateResource("etm/core/configuration/files/valid_1_0/plugin-config.xml");
    EtmManager.reset();
    XmlEtmConfigurator.configure(url);

    EtmMonitor etmMonitor = EtmManager.getEtmMonitor();
    List plugins = ((TestMonitor) etmMonitor).getPlugins();

    assertTrue(plugins.size() > 0);

    EtmPlugin plugin = (EtmPlugin) plugins.get(0);

    assertEquals(TestPlugin.class, plugin.getClass());
    TestPlugin testPlugin = (TestPlugin) plugin;
    assertEquals(true, testPlugin.isBooleanTrue());
    assertEquals(false, testPlugin.isBooleanFalse());
    assertEquals(12, testPlugin.getIntValue());
    assertEquals(12124234324234L, testPlugin.getLongValue());
    assertEquals("testString", testPlugin.getStringValue());
    assertEquals(HashMap.class, testPlugin.getClazzValue());


    Map map = testPlugin.getMapValue();
    assertNotNull(map);
    assertEquals(3, map.size());
    assertEquals(map.get("key1"), "value1");
    assertEquals(map.get("key2"), "value2");
    assertEquals(map.get("key3"), "value3");

    List list = testPlugin.getListValue();
    assertNotNull(list);
    assertEquals(2, list.size());
    assertEquals(list.get(0), "value1");
    assertEquals(list.get(1), "value2");

    etmMonitor.start();
    assertTrue(testPlugin.isStarted());

    etmMonitor.stop();
    assertFalse(testPlugin.isStarted());
  }


  public void testAutostartConfig() throws Exception {
    URL url = locateResource("etm/core/configuration/files/valid_1_0/autostart-on-config.xml");
    EtmManager.reset();
    XmlEtmConfigurator.configure(url);

    EtmMonitor etmMonitor = EtmManager.getEtmMonitor();
    assertTrue(etmMonitor.isStarted());
    assertTrue(etmMonitor.isCollecting());

    etmMonitor.stop();

    url = locateResource("etm/core/configuration/files/valid_1_0/autostart-off-config.xml");
    EtmManager.reset();
    XmlEtmConfigurator.configure(url);
    etmMonitor = EtmManager.getEtmMonitor();
    assertFalse(etmMonitor.isStarted());
    assertTrue(etmMonitor.isCollecting());
  }

  private URL locateResource(String classPathName) throws IOException {
    URL url = getClass().getClassLoader().getResource(classPathName);
    if (url != null) {
      return url;
    } else {
      throw new FileNotFoundException(classPathName);
    }
  }


}
