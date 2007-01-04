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
import etm.contrib.console.ConsoleRequest;
import etm.contrib.console.ConsoleResponse;
import etm.contrib.console.util.ConsoleUtil;
import etm.contrib.renderer.comparator.ExecutionAggregateComparator;

import java.io.IOException;
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

  protected void writeConsoleHeader(ConsoleRequest request, ConsoleResponse response, String point) throws IOException {
    Date currentTime = new Date();
    String pointEncoded = null;
    if (point != null) {
      pointEncoded = URLEncoder.encode(point, "UTF-8");
    }

    response.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
    response.write(
      "<html>\n" +
        " <head> \n" +
        "  <title>JETM HTML Console</title>\n" +
        "  <link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\"/>\n" +
        "  <link rel=\"icon\" href=\"favicon.ico\" type=\"image/x-icon\">" +
        " </head>\n");
    response.write("<body>\n<h1>JETM HTML Console</h1>");


    if (point != null) {
      response.write("<b>");
      response.write(point);
      response.write("</b><br /><br />\n");

      response.write("<a href=\"");
      response.write(ConsoleUtil.appendParameters("/detail?point=" + pointEncoded, request.getRequestParameters()));
      response.write("\">Reload point</a>  &nbsp; \n");

      response.write("<a href=\"");
      response.write(ConsoleUtil.appendParameters("/reset?point=" + pointEncoded, request.getRequestParameters()));
      response.write("\">Reset point</a>  &nbsp; ");

      response.write(" <a href=\"");
      response.write(ConsoleUtil.appendParameters("/", request.getRequestParameters(), true));
      response.write("\">Back to overview</a>\n");



    } else {
      response.write("<table class=\"noborder\">\n");

      response.write("  <tr class=\"noborder\">\n" +
        "    <td class=\"noborder\">Application start:</td>\n" +
        "    <td class=\"noborder\">" + request.getEtmMonitor().getMetaData().getStartTime() + "</td>\n" +
        "  </tr>\n" +
        "  <tr class=\"noborder\">\n" +
        "    <td class=\"noborder\">Monitoring period:</td>\n" +
        "    <td class=\"noborder\">" + request.getEtmMonitor().getMetaData().getLastResetTime() + " - " + currentTime + "</td>\n" +
        "  </tr>\n" +
        "  <tr class=\"noborder\">\n" +
        "    <td class=\"noborder\">Monitoring status:</td>\n");

      if (request.getEtmMonitor().isStarted()) {
        response.write("    <td class=\"noborder\"><span class=\"enabled\">enabled</span></td>\n");
      } else {
        response.write("    <td class=\"noborder\"><span class=\"disabled\">disabled</span></td>\n");
      }

      response.write(
        "  </tr>\n" +
          "  <tr class=\"noborder\">\n" +
          "    <td class=\"noborder\">Collecting status:</td>\n");

      if (request.getEtmMonitor().isCollecting()) {
        response.write("    <td class=\"noborder\"><span class=\"enabled\">enabled</span></td>\n");
      } else {
        response.write("    <td class=\"noborder\"><span class=\"disabled\">disabled</span></td>\n");
      }

      response.write(
        "  </tr>\n" +
          "  <tr class=\"noborder\">\n" +
          "    <td class=\"noborder\">&nbsp;</td>\n" +
          "    <td class=\"noborder\">&nbsp;</td>\n" +
          "  </tr>\n" +
          "  <tr class=\"noborder\">\n");

      response.write("    <td class=\"noborder\"><a href=\"");
      response.write(ConsoleUtil.appendParameters("/", request.getRequestParameters()));
      response.write("\">Reload monitor</a></td>\n");

      response.write("    <td class=\"noborder\"><a href=\"");
      response.write(ConsoleUtil.appendParameters("/reset", request.getRequestParameters()));
      response.write("\">Reset monitor</a>  &nbsp; ");

      if (request.getEtmMonitor().isCollecting()) {
        response.write(" <a href=\"");
        response.write(ConsoleUtil.appendParameters("/stop", request.getRequestParameters()));
        response.write("\">Stop collection</a></td>\n");
      } else {
        response.write(" <a href=\"");
        response.write(ConsoleUtil.appendParameters("/start", request.getRequestParameters()));
        response.write("\">Start collection</a></td>\n");
      }
      response.write("  </tr>\n");
      response.write("</table>");
    }

  }

  protected ExecutionAggregateComparator getComparator(ConsoleRequest request) {
    String sort = request.getRequestParameter("sort");
    boolean isDescending = !"asc".equals(request.getRequestParameter("order"));

    if ("name".equals(sort)) {
      return new ExecutionAggregateComparator(ExecutionAggregateComparator.TYPE_NAME, isDescending);
    } else if ("executions".equals(sort)) {
      return new ExecutionAggregateComparator(ExecutionAggregateComparator.TYPE_EXCECUTIONS, isDescending);
    } else if ("average".equals(sort)) {
      return new ExecutionAggregateComparator(ExecutionAggregateComparator.TYPE_AVERAGE, isDescending);
    } else if ("min".equals(sort)) {
      return new ExecutionAggregateComparator(ExecutionAggregateComparator.TYPE_MIN, isDescending);
    } else if ("max".equals(sort)) {
      return new ExecutionAggregateComparator(ExecutionAggregateComparator.TYPE_MAX, isDescending);
    } else if ("total".equals(sort)) {
      return new ExecutionAggregateComparator(ExecutionAggregateComparator.TYPE_TOTAL, isDescending);
    } else {
      return new ExecutionAggregateComparator();
    }
  }
}