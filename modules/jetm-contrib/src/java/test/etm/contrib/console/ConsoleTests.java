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

package test.etm.contrib.console;

import etm.contrib.console.ConsoleResponse;
import etm.contrib.console.standalone.StandaloneConsoleRequest;
import etm.contrib.console.util.CollapsedResultRenderer;
import etm.contrib.util.ExecutionAggregateComparator;
import etm.core.monitor.EtmMonitor;
import etm.core.renderer.MeasurementRenderer;
import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Map;

/**
 * Tests for Html Console that might run as
 * standalone or plugin console.
 *
 * @author void.fm
 * @version $Revision$
 */
public abstract class ConsoleTests extends TestCase {

  protected EtmMonitor monitor;


  public void testResultRendering() throws Exception {

    String serverResponse = executeRequest("/index");

    final ByteArrayOutputStream out = new ByteArrayOutputStream();

    StandaloneConsoleRequest request = new StandaloneConsoleRequest(monitor);
    ConsoleResponse testResponse = new ConsoleResponse() {
      OutputStreamWriter writer = new OutputStreamWriter(out);

      public void addHeader(String header, String value) {
      }

      public void write(String content) throws IOException {
        writer.write(content);
        writer.flush();
      }

      public void write(char[] content) throws IOException {
        writer.write(content);
        writer.flush();
      }

      public void write(byte[] content) throws IOException {
        out.write(content);
      }

      public void sendRedirect(String target, Map parameters) {
      }

      public void sendStatus(int statusCode, String description) {
      }
    };

    CollapsedResultRenderer renderer = new CollapsedResultRenderer(request,
      testResponse, new ExecutionAggregateComparator(ExecutionAggregateComparator.TYPE_AVERAGE, true)
    );
    monitor.render(renderer);

    String expected = new String(out.toByteArray(), "UTF-8");

    String s = serverResponse.substring(serverResponse.indexOf("close") + 5).trim();
    assertEquals(expected, s);
  }

  public void testMonitorReset() throws Exception {
    executeRequest("/reset");

    monitor.render(new MeasurementRenderer() {
      public void render(Map points) {
        assertEquals(points.size(), 0);
      }
    });
  }

  public void testCollectionStop() throws Exception {
    executeRequest("/stop");
    assertFalse(monitor.isCollecting());
  }

  public void testCollectionStart() throws Exception {
    // we stop our monitor and activate it using the console
    monitor.disableCollection();
    executeRequest("/start");
    assertTrue(monitor.isCollecting());
  }


  protected String executeRequest(String request) throws Exception {
    Socket socket = new Socket("127.0.0.1", 40000);
    socket.setSoTimeout(30000);
    OutputStream outputStream = socket.getOutputStream();
    outputStream.write(("GET " + request + " HTTP/1.0\n").getBytes());
    outputStream.flush();

    byte[] buffer = new byte[65535];
    int pos = 0;
    InputStream in = socket.getInputStream();

    int r;
    while ((r = in.read(buffer, pos, buffer.length - pos)) > -1) {
      pos += r;
    }

    return new String(buffer);

  }
}
