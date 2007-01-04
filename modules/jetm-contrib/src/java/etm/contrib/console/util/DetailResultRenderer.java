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
import etm.core.aggregation.ExecutionAggregate;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Renderes a result for a measurement point.
 *
 * @author void.fm
 * @version $Revision$
 */
public class DetailResultRenderer extends ConsoleRenderer {

  private String measurementPointName;

  public DetailResultRenderer(ConsoleRequest aRequest, ConsoleResponse aResponse,
                              ExecutionAggregateComparator aComparator, String aMeasurementPointName) {
    super(aRequest, aResponse, aComparator);
    measurementPointName = aMeasurementPointName;
  }

  public void render(Map points) {
    ExecutionAggregate point = (ExecutionAggregate) points.get(measurementPointName);
   
    try {
      writeConsoleHeader(measurementPointName);

      response.write("<table>\n");
      writeTableHeader();

      if (point == null) {
        response.write(NO_RESULTS.toCharArray());
      } else {
        response.write(" <tr>\n");
        response.write("  <td>");
        writeName(point, 0);
        response.write("</td>\n");
        response.write("  <td>");
        writeMeasurements(point, 0);
        response.write("</td>\n");
        response.write("  <td>");
        writeAverage(point, 0);
        response.write("</td>\n");
        response.write("  <td>");
        writeMin(point, 0);
        response.write("</td>\n");
        response.write("  <td>");
        writeMax(point, 0);
        response.write("</td>\n");
        response.write("  <td>");
        writeTotals(point, 0);
        response.write("</td>\n");
        response.write(" </tr>\n");

      }

      response.write(FOOTER);
      response.write("</table>");

    } catch (IOException e) {
      throw new RuntimeException("Unable to write to writer: " + e);
    }
  }


  protected void writeTableHeader() throws IOException {

    response.write(" <tr>\n");

    response.write("  <th width=\"200\" ");
    if (ExecutionAggregateComparator.TYPE_NAME == comparator.getType()) {
      if (comparator.isDescending()) {
        response.write("class=\"descending\"><a href=\"?sort=name&order=asc&point=" + URLEncoder.encode(measurementPointName, "UTF-8") + "\">Measurement Point</a>");
      } else {
        response.write("class=\"ascending\"><a href=\"?sort=name&order=desc&point=" + URLEncoder.encode(measurementPointName, "UTF-8") + "\">Measurement Point</a>");
      }
    } else {
      response.write("><a href=\"?sort=name&order=desc&point=" + URLEncoder.encode(measurementPointName, "UTF-8") + "\">Measurement Point</a>");

    }
    response.write("</th>\n");

    response.write("  <th width=\"30\"");
    if (ExecutionAggregateComparator.TYPE_EXCECUTIONS == comparator.getType()) {
      if (comparator.isDescending()) {
        response.write("class=\"descending\"><a href=\"?sort=executions&order=asc&point=" + URLEncoder.encode(measurementPointName, "UTF-8") + "\">#</a>");
      } else {
        response.write("class=\"ascending\"><a href=\"?sort=executions&order=desc&point=" + URLEncoder.encode(measurementPointName, "UTF-8") + "\">#</a>");
      }
    } else {
      response.write("><a href=\"?sort=executions&order=desc&point=" + URLEncoder.encode(measurementPointName, "UTF-8") + "\">#</a> ");
    }
    response.write("</th>\n");


    response.write("  <th width=\"100\"");
    if (ExecutionAggregateComparator.TYPE_AVERAGE == comparator.getType()) {
      if (comparator.isDescending()) {
        response.write("class=\"descending\"><a href=\"?sort=average&order=asc&point=" + URLEncoder.encode(measurementPointName, "UTF-8") + "\">Average</a>");
      } else {
        response.write("class=\"ascending\"><a href=\"?sort=average&order=desc&point=" + URLEncoder.encode(measurementPointName, "UTF-8") + "\">Average</a>");
      }
    } else {
      response.write("><a href=\"?sort=average&order=desc&point=" + URLEncoder.encode(measurementPointName, "UTF-8") + "\">Average</a> ");
    }
    response.write("</th>\n");

    response.write("  <th width=\"100\"");
    if (ExecutionAggregateComparator.TYPE_MIN == comparator.getType()) {
      if (comparator.isDescending()) {
        response.write("class=\"descending\"><a href=\"?sort=min&order=asc&point=" + URLEncoder.encode(measurementPointName, "UTF-8") + "\">Min</a>");
      } else {
        response.write("class=\"ascending\"><a href=\"?sort=min&order=desc&point=" + URLEncoder.encode(measurementPointName, "UTF-8") + "\">Min</a>");
      }
    } else {
      response.write("><a href=\"?sort=min&order=desc&point=" + URLEncoder.encode(measurementPointName, "UTF-8") + "\">Min</a> ");
    }
    response.write("</th>\n");

    response.write("  <th width=\"100\"");
    if (ExecutionAggregateComparator.TYPE_MAX == comparator.getType()) {
      if (comparator.isDescending()) {
        response.write("class=\"descending\"><a href=\"?sort=max&order=asc&point=" + URLEncoder.encode(measurementPointName, "UTF-8") + "\">Max</a>");
      } else {
        response.write("class=\"ascending\"><a href=\"?sort=max&order=desc&point=" + URLEncoder.encode(measurementPointName, "UTF-8") + "\">Max</a>");
      }
    } else {
      response.write("><a href=\"?sort=max&order=asc&point=" + URLEncoder.encode(measurementPointName, "UTF-8") + "\">Max</a>");
    }
    response.write("</th>\n");

    response.write("  <th width=\"100\"");
    if (ExecutionAggregateComparator.TYPE_TOTAL == comparator.getType()) {
      if (comparator.isDescending()) {
        response.write("class=\"descending\"><a href=\"?sort=total&order=asc&point=" + URLEncoder.encode(measurementPointName, "UTF-8") + "\">Total</a>");
      } else {
        response.write("class=\"ascending\"><a href=\"?sort=total&order=desc&point=" + URLEncoder.encode(measurementPointName, "UTF-8") + "\">Total</a>");
      }
    } else {
      response.write("><a href=\"?sort=total&order=desc&point=" + URLEncoder.encode(measurementPointName, "UTF-8") + "\">Total</a> ");
    }
    response.write("</th>\n");

    response.write(" </tr>\n");
  }


}