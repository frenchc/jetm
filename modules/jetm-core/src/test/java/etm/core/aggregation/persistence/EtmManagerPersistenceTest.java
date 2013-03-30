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

package etm.core.aggregation.persistence;

import etm.core.configuration.EtmManager;
import etm.core.configuration.XmlEtmConfigurator;
import junit.framework.TestCase;
import etm.core.aggregation.persistence.mockup.TestPersistenceBackend;
import etm.core.aggregation.persistence.mockup.TestPersistentNestedAggregator;
import etm.core.configuration.mockup.TestMonitor;

import java.util.HashMap;

/**
 * Tests whether its possible to set persistance details through an etm manager configuration.
 *
 * @author void.fm
 * @version $Revision:96 $
 */
public class EtmManagerPersistenceTest extends TestCase {

  private static final String JETM_CONFIG =
    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<!DOCTYPE jetm-config PUBLIC \"-// void.fm //DTD JETM Config 1.0//EN\" \"http://jetm.void.fm/dtd/jetm_config_1_0.dtd\">\n" +
      "<jetm-config>\n" +
      "  <monitor-class>etm.core.configuration.mockup.TestMonitor</monitor-class>" +
      "  <aggregator-chain>\n" +
      "    <chain-root>\n" +
      "      <aggregator-class>etm.core.aggregation.persistence.mockup.TestPersistentNestedAggregator</aggregator-class>\n" +
      "      <properties>\n" +
      "        <property name=\"persistenceBackendClass\">etm.core.aggregation.persistence.mockup.TestPersistenceBackend</property>\n" +
      "        <property name=\"backendProperties.booleanTrue\">true</property>" +
      "        <property name=\"backendProperties.booleanFalse\">false</property>\n" +
      "        <property name=\"backendProperties.longValue\">12124234324234</property>\n" +
      "        <property name=\"backendProperties.intValue\">12</property>\n" +
      "        <property name=\"backendProperties.stringValue\">testString</property>\n" +
      "        <property name=\"backendProperties.clazzValue\">java.util.HashMap</property>\n" +
      "      </properties>\n" +
      "    </chain-root>\n" +
      "  </aggregator-chain>\n" +
      "</jetm-config>";

  protected void setUp() throws Exception {
    super.setUp();
    XmlEtmConfigurator.configure(JETM_CONFIG);
    EtmManager.getEtmMonitor().start();
  }

  protected void tearDown() throws Exception {
    EtmManager.getEtmMonitor().stop();

    EtmManager.reset();
    super.tearDown();
  }

  public void testSetProperties() {
    TestMonitor etmMonitor = (TestMonitor) EtmManager.getEtmMonitor();
    TestPersistentNestedAggregator aggregator = (TestPersistentNestedAggregator) etmMonitor.getAggregator();
    TestPersistenceBackend backend = (TestPersistenceBackend) aggregator.getPersistenceBackend();
    assertEquals(true, backend.isBooleanTrue());
    assertEquals(false, backend.isBooleanFalse());
    assertEquals(12, backend.getIntValue());
    assertEquals(12124234324234L, backend.getLongValue());
    assertEquals("testString", backend.getStringValue());
    assertEquals(HashMap.class, backend.getClazzValue());
  }
}
