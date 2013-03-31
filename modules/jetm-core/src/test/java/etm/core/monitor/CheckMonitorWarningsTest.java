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

package etm.core.monitor;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;
import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;

/**
 *
 * Runs tests whether certain warnings are shown or not.
 *
 * @author void.fm
 * @version $Revision$
 */
public class CheckMonitorWarningsTest extends TestCase {

  public void testEtmMonitorSupportWarning() throws Exception {
    EtmManager.reset();

    PrintStream writer = System.out;

    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      PrintStream tmp = new PrintStream(out);
      System.setOut(tmp);

      EtmMonitor etmMonitor = EtmManager.getEtmMonitor();
      EtmPoint point = etmMonitor.createPoint("test");
      point.collect();

      tmp.flush();
      String s = new String(out.toByteArray(), Charset.defaultCharset());
      assertTrue(s.indexOf("Warning - Performance Monitoring currently disabled.") > -1);

    } finally {
      System.setOut(writer);
    }
  }

  public void testNullMonitorWarning() throws Exception {
    EtmManager.reset();

    PrintStream writer = System.out;

    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      PrintStream tmp = new PrintStream(out);
      System.setOut(tmp);

      EtmMonitor etmMonitor = EtmManager.getEtmMonitor();
      etmMonitor.start();
      EtmPoint point = etmMonitor.createPoint("test");
      point.collect();

      tmp.flush();
      String s = new String(out.toByteArray(), Charset.defaultCharset());
      assertTrue(s.indexOf("Warning - NullMonitor active. Performance results are discarded.") > -1);
      etmMonitor.stop();
    } finally {
      System.setOut(writer);
    }
  }
}
