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

import etm.core.aggregation.ExecutionAggregate;
import etm.core.renderer.MeasurementRenderer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Renders a collapsed view with links to detailed
 * results.
 *
 * @author void.fm
 * @version $Revision$
 */
public class CollapsedResultRenderer implements MeasurementRenderer {
  private static final String HEAD =
    "<table>\n" +
      " <tr>\n" +
      "  <th>Measurement Point</th>\n" +
      "  <th>#</th>\n" +
      "  <th>Average</th>\n" +
      "  <th>Min</th>\n" +
      "  <th>Max</th>\n" +
      "  <th>Total</th>\n" +
      " </tr>\n";

  private static final String FOOTER = " <tr><td class=\"footer\" colspan=\"6\">All times in miliseconds. Measurements provided by <a href=\"http://jetm.void.fm\" target=\"_default\">JETM</a></td></tr>\n</table>";
  private static final String NO_RESULTS = " <tr><td colspan=\"6\">No measurement results available.</td></tr>\n";

  private NumberFormat timeFormatter;
  private NumberFormat numberFormatter;
  private Writer writer;


  /**
   * Constructs a SimpleHtmlRenderer using the default locale
   * and the provided writer.
   *
   * @param aWriter The writer.
   */
  public CollapsedResultRenderer(Writer aWriter) {
    this(aWriter, Locale.getDefault());
  }

  /**
   * Constructs a SimpleTextRenderer using the provided locale
   * and provided writer.
   *
   * @param aWriter The writer to write to.
   * @param aLocale The locale to use.
   */
  public CollapsedResultRenderer(Writer aWriter, Locale aLocale) {
    writer = aWriter;
    timeFormatter = NumberFormat.getNumberInstance(aLocale);
    timeFormatter.setMaximumFractionDigits(3);
    timeFormatter.setMinimumFractionDigits(3);
    timeFormatter.setGroupingUsed(true);

    numberFormatter = NumberFormat.getNumberInstance(aLocale);
    numberFormatter.setMaximumFractionDigits(0);
    numberFormatter.setMinimumFractionDigits(0);
    numberFormatter.setGroupingUsed(true);
  }


  public void render(Map points) {

    try {
      writer.write(HEAD.toCharArray());

      if (points.size() == 0) {
        writer.write(NO_RESULTS.toCharArray());
      } else {
        StringBuffer buffer = new StringBuffer();

        TreeMap map = new TreeMap(points);
        for (Iterator iterator = map.values().iterator(); iterator.hasNext();) {
          ExecutionAggregate point = (ExecutionAggregate) iterator.next();

          buffer.append(" <tr>\n");
          buffer.append("  <td>");
          writeNames(buffer, point);
          buffer.append("</td>\n");
          buffer.append("  <td>");
          writeMeasurements(buffer, point);
          buffer.append("</td>\n");
          buffer.append("  <td>");
          writeAverage(buffer, point);
          buffer.append("</td>\n");
          buffer.append("  <td>");
          writeMin(buffer, point);
          buffer.append("</td>\n");
          buffer.append("  <td>");
          writeMax(buffer, point);
          buffer.append("</td>\n");
          buffer.append("  <td>");
          writeTotals(buffer, point);
          buffer.append("</td>\n");
          buffer.append(" </tr>\n");
        }
        writer.write(buffer.toString().toCharArray());
      }
      writer.write(FOOTER.toCharArray());
      writer.flush();
    } catch (IOException e) {
      throw new RuntimeException("Unable to write to writer: " + e);
    }
  }

  private void writeNames(StringBuffer aBuffer, ExecutionAggregate aPoint) {
    aBuffer.append("<div class=\"parentname\" >");
    aBuffer.append("<a href=\"/detail?point=");
    try {
      aBuffer.append(URLEncoder.encode(aPoint.getName(), "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      // ignored
    }
    aBuffer.append("\" >");

    aBuffer.append(aPoint.getName());
    aBuffer.append("</a></div>");
  }

  private void writeTotals(StringBuffer aBuffer, ExecutionAggregate aPoint) {
    aBuffer.append("<div class=\"parenttotal\" >");
    aBuffer.append(timeFormatter.format(aPoint.getTotal()));
    aBuffer.append("</div>");
  }

  private void writeAverage(StringBuffer aBuffer, ExecutionAggregate aPoint) {
    aBuffer.append("<div class=\"parenttime\" >");
    aBuffer.append(timeFormatter.format(aPoint.getAverage()));
    aBuffer.append("</div>");
  }

  private void writeMin(StringBuffer aBuffer, ExecutionAggregate aPoint) {
    aBuffer.append("<div class=\"parenttime\" >");
    aBuffer.append(timeFormatter.format(aPoint.getMin()));
    aBuffer.append("</div>");
  }

  private void writeMax(StringBuffer aBuffer, ExecutionAggregate aPoint) {
    aBuffer.append("<div class=\"parenttime\" >");
    aBuffer.append(timeFormatter.format(aPoint.getMax()));
    aBuffer.append("</div>");
  }


  private void writeMeasurements(StringBuffer aBuffer, ExecutionAggregate aPoint) {
    aBuffer.append("<div class=\"parentmeasurement\" >");
    aBuffer.append(numberFormatter.format(aPoint.getMeasurements()));
    aBuffer.append("</div>");
  }

}