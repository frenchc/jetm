/*
 *
 * Copyright (c) 2004, 2005, 2006 void.fm
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

package etm.contrib.console.actions;

import etm.contrib.console.ConsoleAction;
import etm.core.monitor.EtmMonitor;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;

/**
 * Base class for all actions.
 *
 * @author void.fm
 * @version $Id: AbstractAction.java,v 1.2 2006/09/10 13:33:26 french_c Exp $
 */
public abstract class AbstractAction implements ConsoleAction {
  protected static final String UTF_8 = "UTF-8";
  protected static final byte[] SERVER_HEADER = "Server: JETM drop-in HTML console\n".getBytes();


  protected void sendRedirect(OutputStream out, String location) throws IOException {
    out.write("HTTP/1.0 302 OK\n".getBytes());
    out.write(SERVER_HEADER);
    out.write(("Date: " + new Date() + "\n").getBytes());
    out.write("Location: ".getBytes());
    out.write(location.getBytes(UTF_8));
    out.write("\n".getBytes());
    out.write("Connection: close\n".getBytes());
    out.write("\n".getBytes());
  }

  protected void sendStatus(OutputStream out, String errorCode, String errorDescription) throws IOException {
    out.write("HTTP/1.0 ".getBytes());
    out.write(errorCode.getBytes());
    out.write(' ');
    out.write(errorDescription.getBytes());
    out.write("\n".getBytes());
    out.write(SERVER_HEADER);
    out.write(("Date: " + new Date() + "\n").getBytes());
    out.write("Connection: close\n".getBytes());
    out.write("\n".getBytes());
  }

  protected void writeConsoleHeader(OutputStream out, EtmMonitor etmMonitor, String point) throws IOException {
    Date currentTime = new Date();
    String pointEncoded = null;
    if (point != null) {
      pointEncoded = URLEncoder.encode(point, "UTF-8");
    }

    out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">".getBytes());
    out.write((
      "<html>\n" +
        " <head> \n" +
        "  <title>JETM HTML Console</title>\n" +
        "  <link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\"/>\n" +
        "  <link rel=\"icon\" href=\"favicon.ico\" type=\"image/x-icon\">" +
        " </head>\n").getBytes(UTF_8));
    out.write("<body>\n<h1>JETM HTML Console</h1>".getBytes());


    if (point != null) {
      out.write(("<b>").getBytes());
      out.write(point.getBytes());
      out.write(("</b><br /><br />\n").getBytes());

      out.write(("<a href=\"/detail?point=").getBytes());
      out.write(pointEncoded.getBytes());
      out.write(("\">Reload point</a>  &nbsp; \n").getBytes());

      out.write(("<a href=\"/reset?point=").getBytes());
      out.write(pointEncoded.getBytes());
      out.write(("\">Reset point</a>  &nbsp; ").getBytes());
      out.write((" <a href=\"/\">Back to overview</a>\n").getBytes());


    } else {
      out.write("<table class=\"noborder\">\n".getBytes());

      out.write(("  <tr class=\"noborder\">\n" +
        "    <td class=\"noborder\">Application start:</td>\n" +
        "    <td class=\"noborder\">" + etmMonitor.getMetaData().getStartTime() + "</td>\n" +
        "  </tr>\n" +
        "  <tr class=\"noborder\">\n" +
        "    <td class=\"noborder\">Monitoring period:</td>\n" +
        "    <td class=\"noborder\">" + etmMonitor.getMetaData().getLastResetTime() + " - " + currentTime + "</td>\n" +
        "  </tr>\n" +
        "  <tr class=\"noborder\">\n" +
        "    <td class=\"noborder\">Monitoring status:</td>\n").getBytes());

      if (etmMonitor.isStarted()) {
        out.write("    <td class=\"noborder\"><span class=\"enabled\">enabled</span></td>\n".getBytes());
      } else {
        out.write("    <td class=\"noborder\"><span class=\"disabled\">disabled</span></td>\n".getBytes());
      }

      out.write((
        "  </tr>\n" +
          "  <tr class=\"noborder\">\n" +
          "    <td class=\"noborder\">Collecting status:</td>\n").getBytes());

      if (etmMonitor.isCollecting()) {
        out.write("    <td class=\"noborder\"><span class=\"enabled\">enabled</span></td>\n".getBytes());
      } else {
        out.write("    <td class=\"noborder\"><span class=\"disabled\">disabled</span></td>\n".getBytes());
      }

      out.write((
        "  </tr>\n" +
          "  <tr class=\"noborder\">\n" +
          "    <td class=\"noborder\">&nbsp;</td>\n" +
          "    <td class=\"noborder\">&nbsp;</td>\n" +
          "  </tr>\n" +
          "  <tr class=\"noborder\">\n" +
          "    <td class=\"noborder\"><a href=\"").getBytes());
      out.write('/');
      out.write(("\">Reload monitor</a></td>\n").getBytes());
      out.write(("    <td class=\"noborder\"><a href=\"").getBytes());
      out.write(("/reset\">Reset monitor</a>  &nbsp; ").getBytes());

      if (etmMonitor.isCollecting()) {
        out.write((" <a href=\"/stop\">Stop collection</a></td>\n").getBytes());
      } else {
        out.write((" <a href=\"/start\">Start collection</a></td>\n").getBytes());
      }
      out.write(("  </tr>\n").getBytes());
      out.write(("</table>").getBytes());
    }

  }
}