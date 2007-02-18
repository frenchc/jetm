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

package test.etm.contrib.rrd.rrd4j;

import etm.contrib.rrd.rrd4j.Rrd4jPlugin;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.NestedMonitor;
import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Created by IntelliJ IDEA.
 * User: jens
 * Date: Feb 17, 2007
 * Time: 9:58:27 AM
 * To change this template use File | Settings | File Templates.
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

//  public void testRrdDbCreate() {
//    EtmMonitor monitor = new NestedMonitor(new NotifyingAggregator(new RootAggregator()));
//
//    Rrd4jPlugin plugin = new Rrd4jPlugin();
//    List configurations = new ArrayList();
//    configurations.add("test.rrd|*");
//    plugin.setDestinations(configurations);
//    monitor.addPlugin(plugin);
//
//
//    try {
//      monitor.start();
//    } finally {
//      monitor.stop();
//    }
//  }

}
