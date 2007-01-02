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

package etm.contrib.console.actions;

import etm.contrib.console.ConsoleAction;
import etm.contrib.console.ConsoleResponse;
import etm.core.monitor.EtmMonitor;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;

/**
 * Base class for all actions.
 *
 * @author void.fm
 * @version $Revision$
 */
public abstract class AbstractAction implements ConsoleAction {
  protected static final String UTF_8 = "UTF-8";
  protected static final byte[] SERVER_HEADER = "Server: JETM drop-in HTML console\n".getBytes();

  protected void writeConsoleHeader(ConsoleResponse response, EtmMonitor etmMonitor, String point) throws IOException {
    Date currentTime = new Date();
    String pointEncoded = null;
    if (point != null) {
      pointEncoded = URLEncoder.encode(point, "UTF-8");
    }

    response.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">".getBytes());
    response.write((
      "<html>\n" +
        " <head> \n" +
        "  <title>JETM HTML Console</title>\n" +
        "  <link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\"/>\n" +
        "  <link rel=\"icon\" href=\"favicon.ico\" type=\"image/x-icon\">" +
        " </head>\n").getBytes(UTF_8));
    response.write("<body>\n<h1>JETM HTML Console</h1>".getBytes());


    if (point != null) {
      response.write(("<b>").getBytes());
      response.write(point.getBytes());
      response.write(("</b><br /><br />\n").getBytes());

      response.write(("<a href=\"/detail?point=").getBytes());
      response.write(pointEncoded.getBytes());
      response.write(("\">Reload point</a>  &nbsp; \n").getBytes());

      response.write(("<a href=\"/reset?point=").getBytes());
      response.write(pointEncoded.getBytes());
      response.write(("\">Reset point</a>  &nbsp; ").getBytes());
      response.write((" <a href=\"/\">Back to overview</a>\n").getBytes());


    } else {
      response.write("<table class=\"noborder\">\n".getBytes());

      response.write(("  <tr class=\"noborder\">\n" +
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
        response.write("    <td class=\"noborder\"><span class=\"enabled\">enabled</span></td>\n".getBytes());
      } else {
        response.write("    <td class=\"noborder\"><span class=\"disabled\">disabled</span></td>\n".getBytes());
      }

      response.write((
        "  </tr>\n" +
          "  <tr class=\"noborder\">\n" +
          "    <td class=\"noborder\">Collecting status:</td>\n").getBytes());

      if (etmMonitor.isCollecting()) {
        response.write("    <td class=\"noborder\"><span class=\"enabled\">enabled</span></td>\n".getBytes());
      } else {
        response.write("    <td class=\"noborder\"><span class=\"disabled\">disabled</span></td>\n".getBytes());
      }

      response.write((
        "  </tr>\n" +
          "  <tr class=\"noborder\">\n" +
          "    <td class=\"noborder\">&nbsp;</td>\n" +
          "    <td class=\"noborder\">&nbsp;</td>\n" +
          "  </tr>\n" +
          "  <tr class=\"noborder\">\n" +
          "    <td class=\"noborder\"><a href=\"").getBytes());
      response.write('/');
      response.write(("\">Reload monitor</a></td>\n").getBytes());
      response.write(("    <td class=\"noborder\"><a href=\"").getBytes());
      response.write(("/reset\">Reset monitor</a>  &nbsp; ").getBytes());

      if (etmMonitor.isCollecting()) {
        response.write((" <a href=\"/stop\">Stop collection</a></td>\n").getBytes());
      } else {
        response.write((" <a href=\"/start\">Start collection</a></td>\n").getBytes());
      }
      response.write(("  </tr>\n").getBytes());
      response.write(("</table>").getBytes());
    }

  }
}