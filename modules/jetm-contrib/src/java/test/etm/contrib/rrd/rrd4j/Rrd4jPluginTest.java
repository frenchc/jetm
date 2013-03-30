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

package test.etm.contrib.rrd.rrd4j;

import etm.contrib.rrd.rrd4j.Rrd4jPlugin;
import etm.contrib.rrd.rrd4j.Rrd4jUtil;
import etm.core.aggregation.NotifyingAggregator;
import etm.core.aggregation.RootAggregator;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;
import etm.core.monitor.NestedMonitor;
import junit.framework.TestCase;
import org.rrd4j.core.RrdDb;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests our rrd4j plugin.
 *
 * @author void.fm
 * @version $Revision$
 */
public class Rrd4jPluginTest extends TestCase {

  public void testMissingAggregator() {
    EtmMonitor monitor = new NestedMonitor();

    Rrd4jPlugin plugin = new Rrd4jPlugin();
    monitor.addPlugin(plugin);

    PrintStream writer = System.err;
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintStream tmpErr = new PrintStream(out);
    System.setErr(tmpErr);

    try {
      monitor.start();

      tmpErr.flush();
      String s = new String(out.toByteArray());
      assertTrue(s.indexOf("NotifyingAggregator") > -1);

    } finally {
      System.setErr(writer);
      monitor.stop();
    }
  }

  public void testRrdDbWrite() throws Exception {
    URL resource = Thread.currentThread().getContextClassLoader().getResource("test/etm/contrib/rrd/rrd4j/resources/basic_db_template.xml");
    File path = File.createTempFile("test", ".rrd");

    try {
      EtmMonitor monitor = new NestedMonitor(new NotifyingAggregator(new RootAggregator()));
      try {
        path.delete();

        Rrd4jUtil rrd4jUtil = new Rrd4jUtil();
        rrd4jUtil.createRrdDb(resource, path, null);

        Rrd4jPlugin plugin = new Rrd4jPlugin();
        List configurations = new ArrayList();
        configurations.add(path.getName() + "!*");
        plugin.setDestinations(configurations);
        monitor.addPlugin(plugin);

        monitor.start();

        long endtime = System.currentTimeMillis() + 10000;
        while (System.currentTimeMillis() < endtime) {
          EtmPoint point = monitor.createPoint("testPoint");
          try {
            Thread.sleep(2);
          } finally {
            point.collect();
          }

        }
      } finally {
        monitor.stop();
      }

      RrdDb db = new RrdDb(path.getAbsolutePath(), true);
      assertTrue(db.getDatasource("transactions!").getLastValue() > 0);
      assertTrue(db.getDatasource("min").getLastValue() > 0);
      assertTrue(db.getDatasource("max").getLastValue() > db.getDatasource("min").getLastValue());
      assertTrue(db.getDatasource("average").getLastValue() > db.getDatasource("min").getLastValue());
      assertTrue(db.getDatasource("max").getLastValue() > db.getDatasource("average").getLastValue());

      db.close();
    } finally {
      path.delete();
    }
  }

}
