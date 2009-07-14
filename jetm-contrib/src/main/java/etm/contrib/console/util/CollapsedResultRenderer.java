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
import etm.contrib.util.ExecutionAggregateComparator;
import etm.core.aggregation.Aggregate;
import etm.core.monitor.EtmException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

/**
 * Renders a collapsed view with links to detailed
 * results.
 *
 * @author void.fm
 * @version $Revision$
 */
public class CollapsedResultRenderer extends ConsoleRenderer {


  /**
   * Constructs a CollapsedResultRenderer that writes results to
   * the given response.
   *
   * @param aRequest    The current request
   * @param aResponse   The current respone
   * @param aComparator A comparator used for sorting results
   */

  public CollapsedResultRenderer(ConsoleRequest aRequest, ConsoleResponse aResponse, ExecutionAggregateComparator aComparator) {
    super(aRequest, aResponse, aComparator);
  }


  public void render(Map points) {
    Object[] values = points.values().toArray();

    try {
      writeHtmlHead(false);
      response.write("<!-- Begin results -->");
      response.write("<table>\n");
      writeTableHeader();

      if (points.size() == 0) {
        response.write(NO_RESULTS);
      } else {

        Arrays.sort(values, comparator);
        for (int i = 0; i < values.length; i++) {
          Aggregate point = (Aggregate) values[i];

          response.write(" <tr>\n");
          response.write("  <td>");
          writeName(point);
          response.write("</td>\n");
          response.write("  <td>");
          writeMeasurements(point);
          response.write("</td>\n");
          response.write("  <td>");
          writeAverage(point);
          response.write("</td>\n");
          response.write("  <td>");
          writeMin(point);
          response.write("</td>\n");
          response.write("  <td>");
          writeMax(point);
          response.write("</td>\n");
          response.write("  <td>");
          writeTotals(point);
          response.write("</td>\n");
          response.write(" </tr>\n");
        }

      }
      response.write(FOOTER);
      response.write("</table>\n");
      response.write(" </body>\n</html>");

    } catch (IOException e) {
      throw new EtmException("Unable to write to writer: " + e);
    }
  }

}