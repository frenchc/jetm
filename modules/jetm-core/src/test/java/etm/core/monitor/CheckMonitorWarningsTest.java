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
import junit.framework.TestCase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Runs tests whether certain warnings are shown or not.
 *
 * @author void.fm
 * @version $Revision$
 */
public class CheckMonitorWarningsTest extends TestCase {

  @Override
  protected void setUp() {
    removeLog4jAppender();
  }

  public void testEtmMonitorSupportWarning() throws Exception {
    EtmManager.reset();

    PrintStream outWriter = System.out;
    PrintStream errorWriter = System.err;

    try {
      System.setProperty("etm.core.util.jdk.logging.disabled", "true");

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      PrintStream tmp = new PrintStream(out);
      System.setOut(tmp);
      System.setErr(tmp);


      EtmMonitor etmMonitor = EtmManager.getEtmMonitor();
      EtmPoint point = etmMonitor.createPoint("test");
      point.collect();

      tmp.flush();
      String s = new String(out.toByteArray(), Charset.defaultCharset().name());

      assertThat(s, containsString("Warning - Performance Monitoring currently disabled."));
    } finally {
      System.setProperty("etm.core.util.jdk.logging.disabled", "false");

      System.setOut(outWriter);
      System.setErr(errorWriter);
    }
  }

  public void testNullMonitorWarning() throws Exception {
    EtmManager.reset();

    PrintStream outWriter = System.out;
    PrintStream errorWriter = System.err;

    try {
      System.setProperty("etm.core.util.jdk.logging.disabled", "true");

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      PrintStream tmp = new PrintStream(out);
      System.setOut(tmp);
      System.setErr(tmp);

      EtmMonitor etmMonitor = EtmManager.getEtmMonitor();
      etmMonitor.start();
      EtmPoint point = etmMonitor.createPoint("test");
      point.collect();

      tmp.flush();
      String s = new String(out.toByteArray(), Charset.defaultCharset().name());
      etmMonitor.stop();

      assertThat(s, containsString("Warning - NullMonitor active. Performance results are discarded."));
    } finally {
      System.setProperty("etm.core.util.jdk.logging.disabled", "false");
      System.setOut(outWriter);
      System.setErr(errorWriter);
    }
  }

  /**
   * Removes all appender from the log4j2 logger.
   *
   * <p>Since version 2, log4j activates a default configuration if it does not find an explicit
   * configuration at startup.  For the tests in this suite, it is necessary that no configuration
   * is found. This method is used to delete all log appender before executing a test.
   */
  private void removeLog4jAppender() {
    LoggerContext context = (LoggerContext) LogManager.getContext(false);
    LoggerConfig config = context.getConfiguration().getLoggerConfig("loggerName");
    config.getAppenders().keySet().forEach(config::removeAppender);
    context.updateLoggers();
  }
}
