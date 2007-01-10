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

package etm.contrib.console.util;

import etm.contrib.console.ConsoleRequest;
import etm.contrib.console.ConsoleResponse;
import etm.contrib.renderer.comparator.ExecutionAggregateComparator;
import etm.core.Version;
import etm.core.aggregation.ExecutionAggregate;
import etm.core.renderer.MeasurementRenderer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Base class for our console views.
 *
 * @author void.fm
 * @version $Revision$
 */
public abstract class ConsoleRenderer implements MeasurementRenderer {
  protected static final String FOOTER = " <tr><td class=\"footer\" colspan=\"6\">All times in miliseconds. " +
    "Measurements provided by <a href=\"http://jetm.void.fm\" target=\"_default\">JETM " +
    Version.getVersion() +
    "</a></td></tr>\n";
  protected static final String NO_RESULTS = " <tr><td colspan=\"6\">No measurement results available.</td></tr>\n";
  protected NumberFormat timeFormatter;
  protected NumberFormat numberFormatter;
  protected ExecutionAggregateComparator comparator;
  protected ConsoleRequest request;
  protected ConsoleResponse response;

  public ConsoleRenderer(ConsoleRequest aRequest, ConsoleResponse aResponse, ExecutionAggregateComparator aComparator) {
    request = aRequest;
    response = aResponse;
    comparator = aComparator;

    timeFormatter = NumberFormat.getNumberInstance();
    timeFormatter.setMaximumFractionDigits(3);
    timeFormatter.setMinimumFractionDigits(3);
    timeFormatter.setGroupingUsed(true);

    numberFormatter = NumberFormat.getNumberInstance();
    numberFormatter.setMaximumFractionDigits(0);
    numberFormatter.setMinimumFractionDigits(0);
    numberFormatter.setGroupingUsed(true);
  }

  protected void writeCommonHtmlHead() throws IOException {
    response.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
    response.write(
      "<html>\n" +
        " <head> \n" +
        "  <title>JETM Console</title>\n" +
        "  <link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\"/>\n" +
        "  <link rel=\"icon\" href=\"favicon.ico\" type=\"image/x-icon\"/>" +
        " </head>\n");
    response.write("<body>\n<h1>JETM Console</h1>");

  }

  protected void writeDetailHtmlHead(String point) throws IOException {
    writeCommonHtmlHead();
    String pointEncoded = URLEncoder.encode(point, "UTF-8");

    response.write("<b>");
    response.write(point);
    response.write("</b><br /><br />\n");

    response.write("<a href=\"");
    response.write(ConsoleUtil.appendParameters("detail?point=" + pointEncoded, request.getRequestParameters()));
    response.write("\">Reload point</a>  &nbsp; \n");

    response.write("<a href=\"");
    response.write(ConsoleUtil.appendParameters("reset?point=" + pointEncoded, request.getRequestParameters()));
    response.write("\">Reset point</a>  &nbsp; ");

    response.write(" <a href=\"");
    response.write(ConsoleUtil.appendParameters("index", request.getRequestParameters(), true));
    response.write("\">Back to overview</a>\n");
  }

  protected void writeHtmlHead(boolean expanded) throws IOException {
    writeCommonHtmlHead();

    Date currentTime = new Date();

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
    response.write(ConsoleUtil.appendParameters("index", request.getRequestParameters()));
    response.write("\">Reload monitor</a></td>\n");

    response.write("    <td class=\"noborder\"><a href=\"");
    response.write(ConsoleUtil.appendParameters("reset", request.getRequestParameters()));
    response.write("\">Reset monitor</a>  &nbsp; ");

    if (request.getEtmMonitor().isCollecting()) {
      response.write(" <a href=\"");
      response.write(ConsoleUtil.appendParameters("stop", request.getRequestParameters()));
      response.write("\">Stop collection</a> &nbsp; ");
    } else {
      response.write(" <a href=\"");
      response.write(ConsoleUtil.appendParameters("start", request.getRequestParameters()));
      response.write("\">Start collection</a> &nbsp; ");
    }

    if (expanded) {
      response.write(" <a href=\"");
      response.write(ConsoleUtil.appendParameters("collapse", request.getRequestParameters()));
      response.write("\">Collapse results</a></td>\n");
    } else {
      response.write(" <a href=\"");
      response.write(ConsoleUtil.appendParameters("expand", request.getRequestParameters()));
      response.write("\">Expand results</a></td>\n");
    }

    response.write("  </tr>\n");
    response.write("</table>");

  }


  protected void writeTableHeader() throws IOException {
    response.write(" <tr>\n");

    response.write("  <th width=\"200\" ");
    if (ExecutionAggregateComparator.TYPE_NAME == comparator.getType()) {
      if (comparator.isDescending()) {
        response.write("class=\"descending\"><a href=\"?sort=name&amp;order=asc\">Measurement Point</a>");
      } else {
        response.write("class=\"ascending\"><a href=\"?sort=name&amp;order=desc\">Measurement Point</a>");
      }
    } else {
      response.write("><a href=\"?sort=name&amp;order=asc\">Measurement Point</a>");
    }
    response.write("</th>\n");

    response.write("  <th width=\"30\" ");
    if (ExecutionAggregateComparator.TYPE_EXCECUTIONS == comparator.getType()) {
      if (comparator.isDescending()) {
        response.write("class=\"descending\"><a href=\"?sort=executions&amp;order=asc\">#</a>");
      } else {
        response.write("class=\"ascending\"><a href=\"?sort=executions&amp;order=desc\">#</a>");
      }
    } else {
      response.write("><a href=\"?sort=executions&amp;order=desc\">#</a> ");
    }
    response.write("</th>\n");


    response.write("  <th width=\"80\" ");
    if (ExecutionAggregateComparator.TYPE_AVERAGE == comparator.getType()) {
      if (comparator.isDescending()) {
        response.write("class=\"descending\"><a href=\"?sort=average&amp;order=asc\">Average</a>");
      } else {
        response.write("class=\"ascending\"><a href=\"?sort=average&amp;order=desc\">Average</a>");
      }
    } else {
      response.write("><a href=\"?sort=average&amp;order=desc\">Average</a> ");
    }
    response.write("</th>\n");

    response.write("  <th width=\"80\" ");
    if (ExecutionAggregateComparator.TYPE_MIN == comparator.getType()) {
      if (comparator.isDescending()) {
        response.write("class=\"descending\"><a href=\"?sort=min&amp;order=asc\">Min</a>");
      } else {
        response.write("class=\"ascending\"><a href=\"?sort=min&amp;order=desc\">Min</a>");
      }
    } else {
      response.write("><a href=\"?sort=min&amp;order=desc\">Min</a> ");
    }
    response.write("</th>\n");

    response.write("  <th width=\"80\"");
    if (ExecutionAggregateComparator.TYPE_MAX == comparator.getType()) {
      if (comparator.isDescending()) {
        response.write("class=\"descending\"><a href=\"?sort=max&amp;order=asc\">Max</a>");
      } else {
        response.write("class=\"ascending\"><a href=\"?sort=max&amp;order=desc\">Max</a>");
      }
    } else {
      response.write("><a href=\"?sort=max&amp;order=desc\">Max</a>");
    }
    response.write("</th>\n");

    response.write("  <th width=\"80\"");
    if (ExecutionAggregateComparator.TYPE_TOTAL == comparator.getType()) {
      if (comparator.isDescending()) {
        response.write("class=\"descending\"><a href=\"?sort=total&amp;order=asc\">Total</a>");
      } else {
        response.write("class=\"ascending\"><a href=\"?sort=total&amp;order=desc\">Total</a>");
      }
    } else {
      response.write("><a href=\"?sort=total&amp;order=desc\">Total</a> ");
    }
    response.write("</th>\n");

    response.write(" </tr>\n");
  }

  protected void writeName(ExecutionAggregate aPoint) throws IOException {
    String link = "detail?point=";
    try {
      link = link + URLEncoder.encode(aPoint.getName(), "UTF-8");
    } catch (UnsupportedEncodingException e) {
      // ignored
    }
    link = ConsoleUtil.appendParameters(link, request.getRequestParameters());
    response.write("<div class=\"parentname\" >");
    response.write("<a href=\"");
    response.write(link);
    response.write("\" >");

    response.write(aPoint.getName());
    response.write("</a></div>");
  }

  protected void writeTotals(ExecutionAggregate aPoint) throws IOException {
    response.write("<div class=\"parenttotal\" >");
    response.write(timeFormatter.format(aPoint.getTotal()));
    response.write("</div>");
  }

  protected void writeAverage(ExecutionAggregate aPoint) throws IOException {
    response.write("<div class=\"parenttime\" >");
    response.write(timeFormatter.format(aPoint.getAverage()));
    response.write("</div>");
  }

  protected void writeMin(ExecutionAggregate aPoint) throws IOException {
    response.write("<div class=\"parenttime\" >");
    response.write(timeFormatter.format(aPoint.getMin()));
    response.write("</div>");
  }

  protected void writeMax(ExecutionAggregate aPoint) throws IOException {
    response.write("<div class=\"parenttime\" >");
    response.write(timeFormatter.format(aPoint.getMax()));
    response.write("</div>");
  }

  protected void writeMeasurements(ExecutionAggregate aPoint) throws IOException {
    response.write("<div class=\"parentmeasurement\" >");
    response.write(numberFormatter.format(aPoint.getMeasurements()));
    response.write("</div>");
  }

  protected void writeName(SortedExecutionGraph aElement, int depth) throws IOException {
    if (depth > 0) {
      response.write("<div class=\"childname\" >");
    } else {
      response.write("<div class=\"parentname\" >");
    }

    response.write(aElement.getName());

    if (aElement.hasChilds()) {
      int currentDepth = depth + 1;

      for (Iterator iterator = aElement.getSortedChilds().iterator(); iterator.hasNext();) {
        writeName((SortedExecutionGraph) iterator.next(), currentDepth);
      }
    }

    response.write("</div>");
  }

  protected void writeTotals(SortedExecutionGraph aElement, int depth) throws IOException {
    if (depth > 0) {
      response.write("<div class=\"childtotal\" >");
    } else {
      response.write("<div class=\"parenttotal\" >");
    }

    response.write(timeFormatter.format(aElement.getTotal()));

    if (aElement.hasChilds()) {
      int currentDepth = depth + 1;

      for (Iterator iterator = aElement.getSortedChilds().iterator(); iterator.hasNext();) {
        writeTotals((SortedExecutionGraph) iterator.next(), currentDepth);
      }
    }
    response.write("</div>");
  }

  protected void writeAverage(SortedExecutionGraph aElement, int depth) throws IOException {
    if (depth > 0) {
      response.write("<div class=\"childtime\" >");
    } else {
      response.write("<div class=\"parenttime\" >");
    }

    response.write(timeFormatter.format(aElement.getAverage()));

    if (aElement.hasChilds()) {
      int currentDepth = depth + 1;

      for (Iterator iterator = aElement.getSortedChilds().iterator(); iterator.hasNext();) {
        writeAverage((SortedExecutionGraph) iterator.next(), currentDepth);
      }
    }

    response.write("</div>");
  }

  protected void writeMin(SortedExecutionGraph aElement, int depth) throws IOException {
    if (depth > 0) {
      response.write("<div class=\"childtime\" >");
    } else {
      response.write("<div class=\"parenttime\" >");
    }

    response.write(timeFormatter.format(aElement.getMin()));

    if (aElement.hasChilds()) {
      int currentDepth = depth + 1;

      for (Iterator iterator = aElement.getSortedChilds().iterator(); iterator.hasNext();) {
        writeMin((SortedExecutionGraph) iterator.next(), currentDepth);
      }
    }

    response.write("</div>");
  }

  protected void writeMax(SortedExecutionGraph aElement, int depth) throws IOException {
    if (depth > 0) {
      response.write("<div class=\"childtime\" >");
    } else {
      response.write("<div class=\"parenttime\" >");
    }

    response.write(timeFormatter.format(aElement.getMax()));

    if (aElement.hasChilds()) {
      int currentDepth = depth + 1;

      for (Iterator iterator = aElement.getSortedChilds().iterator(); iterator.hasNext();) {
        writeMax((SortedExecutionGraph) iterator.next(), currentDepth);
      }
    }

    response.write("</div>");
  }

  protected void writeMeasurements(SortedExecutionGraph aElement, int depth) throws IOException {
    if (depth > 0) {
      response.write("<div class=\"childmeasurement\" >");
    } else {
      response.write("<div class=\"parentmeasurement\" >");
    }

    response.write(numberFormatter.format(aElement.getMeasurements()));

    if (aElement.hasChilds()) {
      int currentDepth = depth + 1;

      for (Iterator iterator = aElement.getSortedChilds().iterator(); iterator.hasNext();) {
        writeMeasurements((SortedExecutionGraph) iterator.next(), currentDepth);
      }
    }

    response.write("</div>");
  }

  protected class SortedExecutionGraph extends ExecutionAggregate {
    private List sortedChilds;

    public SortedExecutionGraph(ExecutionAggregate aAggregate, ExecutionAggregateComparator aComparator) {
      super(aAggregate.getName());
      setMin(aAggregate.getMin());
      setMax(aAggregate.getMin());
      setTotal(aAggregate.getMin());
      setMeasurements(aAggregate.getMeasurements());

      if (aAggregate.hasChilds()) {
        Map childs = aAggregate.getChilds();
        sortedChilds = new ArrayList();
        for (Iterator iterator = childs.values().iterator(); iterator.hasNext();) {
          SortedExecutionGraph child = new SortedExecutionGraph((ExecutionAggregate) iterator.next(), aComparator);
          sortedChilds.add(child);
        }
        Collections.sort(sortedChilds, aComparator);
      }
    }

    public List getSortedChilds() {
      return sortedChilds;
    }


    public boolean hasChilds() {
      return sortedChilds != null;
    }
  }
}
