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

package etm.contrib.console.standalone;

import etm.contrib.console.ConsoleResponse;
import etm.contrib.console.util.ConsoleUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

/**
 * Response abstraction for standalone HTTP console.
 *
 * @author void.fm
 * @version $Revision$
 */
public class StandaloneConsoleResponse implements ConsoleResponse {

  // todo implement chucked output
  private static final byte[] SERVER_HEADER = "Server: JETM console\n".getBytes();
  private static final String RFC1123_PATTERN = "EEE, dd MMM yyyy HH:mm:ss zzz";

  private Map headers;
  private Writer bufferWriter;
  private ByteArrayOutputStream bufferOutStream;

  private String redirectUrl;
  private HttpStatus status;
  private OutputStream destination;

  public StandaloneConsoleResponse(OutputStream aDestination) {
    try {
      headers = new HashMap();
      bufferOutStream = new ByteArrayOutputStream();
      bufferWriter = new OutputStreamWriter(bufferOutStream, "UTF-8");
      destination = aDestination;
    } catch (UnsupportedEncodingException e) {
      throw new IllegalStateException(e.getMessage());
    }
  }

  public void addHeader(String header, String value) {
    headers.put(header, value);
  }

  public void write(String content) throws IOException {
    bufferWriter.write(content);
  }

  public void write(char[] content) throws IOException {
    bufferWriter.write(content);
  }

  public void write(byte[] content) throws IOException {
    bufferOutStream.write(content);
  }

  public void sendRedirect(String target, Map parameters) {
    redirectUrl = ConsoleUtil.appendParameters(target, parameters);
  }

  public void sendStatus(int statusCode, String description) {
    status = new HttpStatus(statusCode, description);
  }

  public void flush() throws IOException {
    if (status != null) {
      writeStatus();
    } else if (redirectUrl != null) {
      writeRedirect();
    } else {
      writeContent();
    }

    destination.flush();
  }

  private void writeStatus() throws IOException {
    destination.write("HTTP/1.0 ".getBytes());
    destination.write(String.valueOf(status.statusCode).getBytes());
    destination.write(' ');
    destination.write(status.description.getBytes());
    destination.write("\n".getBytes());
    destination.write(SERVER_HEADER);
    destination.write(("Date: " + getRfc1123Date() + "\n").getBytes());
    destination.write("Connection: close\n".getBytes());
    destination.write("\n".getBytes());
  }

  private void writeRedirect() throws IOException {
    destination.write("HTTP/1.0 302 OK\n".getBytes());
    destination.write(SERVER_HEADER);
    destination.write(("Date: " + getRfc1123Date() + "\n").getBytes());
    destination.write("Location: ".getBytes());
    destination.write(redirectUrl.getBytes());
    destination.write("\n".getBytes());
    destination.write("Connection: close\n".getBytes());
    destination.write("\n".getBytes());
  }

  private void writeContent() throws IOException {
    bufferWriter.flush();
    destination.write("HTTP/1.0 200 OK\n".getBytes());
    destination.write(SERVER_HEADER);
    destination.write(("Date: " + getRfc1123Date() + "\n").getBytes());
    for (Iterator iterator = headers.keySet().iterator(); iterator.hasNext();) {
      String name = (String) iterator.next();
      destination.write(name.getBytes());
      destination.write(": ".getBytes());
      destination.write(((String) (headers.get(name))).getBytes());
      destination.write("\n".getBytes());
    }

    destination.write(("Content-Length: " + bufferOutStream.size() + "\n").getBytes());
    destination.write("Connection: close\n".getBytes());
    destination.write("\n".getBytes());
    destination.write(bufferOutStream.toByteArray());
  }


  private String getRfc1123Date() {
    SimpleDateFormat dateFormat = new SimpleDateFormat(RFC1123_PATTERN);
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    return dateFormat.format(new Date());
  }

  class HttpStatus {
    private int statusCode;
    private String description;


    public HttpStatus(int aStatusCode, String aDescription) {
      statusCode = aStatusCode;
      description = aDescription;
    }
  }
}
