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
package etm.core.jmx;

import etm.core.aggregation.Aggregate;
import etm.core.metadata.AggregatorMetaData;
import etm.core.monitor.EtmMonitor;
import etm.core.renderer.MeasurementRenderer;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * The ManagedEtmMonitor wraps an existing EtmMonitor instance
 * and exposes it through JMX.
 *
 * @author void.fm
 * @version $Revision$
 * @deprecated Please use {@link EtmMonitorMBean} and {@link EtmMonitorJmxPlugin} instead. Will be removed with JETM 2.0.0.
 */
public class ManagedEtmMonitor implements ManagedEtmMonitorMBean {

  private static final String LINE_SEPARATOR = System.getProperty("line.separator");

  private EtmMonitor monitor;
  private MeasurementRenderer textRenderer;
  private CharArrayWriter writer;


  public ManagedEtmMonitor(EtmMonitor aMonitor) {
    this(aMonitor, Locale.getDefault());
  }

  public ManagedEtmMonitor(EtmMonitor aMonitor, Locale locale) {
    monitor = aMonitor;
    writer = new CharArrayWriter();
    textRenderer = new HtmlRenderer(writer, locale);
  }

  public synchronized String showHtmlInfos() {
    try {
      monitor.render(textRenderer);

      return writer.toString();
    } finally {
      writer.reset();
    }
  }

  public synchronized void reset() {
    monitor.reset();
  }

  public Date getStartTime() {
    return monitor.getMetaData().getStartTime();
  }

  public Date getLastResetTime() {
    return monitor.getMetaData().getLastResetTime();
  }

  public String getMonitorImplementationClass() {
    return monitor.getMetaData().getImplementationClass().getName();
  }

  public String getMonitorDescription() {
    return monitor.getMetaData().getDescription();
  }

  public String getTimerImplementationClass() {
    return monitor.getMetaData().getTimerMetaData().getImplementationClass().getName();
  }

  public String getTimerDescription() {
    return monitor.getMetaData().getTimerMetaData().getDescription();
  }

  public long getTimerTicksPerSecond() {
    return monitor.getMetaData().getTimerMetaData().getTicksPerSecond();
  }

  public String[] getAggregatorChain() {
    List list = new ArrayList();
    AggregatorMetaData aggregatorMetaData = monitor.getMetaData().getAggregatorMetaData();
    while (aggregatorMetaData != null) {
      list.add(aggregatorMetaData.toString() + LINE_SEPARATOR);
      aggregatorMetaData = aggregatorMetaData.getNestedMetaData();
    }
    return (String[]) list.toArray(new String[list.size()]);
  }

  public void start() {
    monitor.start();
  }

  public void stop() {
    monitor.stop();
  }

  public boolean isStarted() {
    return monitor.isStarted();
  }


  class HtmlRenderer implements MeasurementRenderer {
    private static final String TREE_SIGN = "|-";
    private static final String HEAD =
      "<table border=\"1\" cellpadding=\"5\" >\n" +
        " <tr>\n" +
        "  <th>Etm Point</th>\n" +
        "  <th>Measurements</th>\n" +
        "  <th>Average</th>\n" +
        "  <th>Min/Max</th>\n" +
        "  <th>Total Time</th>\n" +
        " </tr>\n";

    private static final String FOOTER =
      "</table>";


    private NumberFormat timeFormatter;
    private Writer responseWriter;
    private static final String SPACE = "&nbsp;";

    /**
     * Constructs a SimpleTextRenderer using the provided locale
     * and provided writer.
     *
     * @param aWriter The writer to write to.
     * @param aLocale The locale to use.
     */
    public HtmlRenderer(Writer aWriter, Locale aLocale) {
      responseWriter = aWriter;
      timeFormatter = NumberFormat.getNumberInstance(aLocale);
      timeFormatter.setMaximumFractionDigits(3);
      timeFormatter.setMinimumFractionDigits(3);
      timeFormatter.setGroupingUsed(true);
    }


    public void render(Map points) {

      try {
        responseWriter.write(HEAD.toCharArray());
        StringBuffer buffer = new StringBuffer();

        TreeMap map = new TreeMap(points);
        for (Iterator iterator = map.values().iterator(); iterator.hasNext();) {
          Aggregate point = (Aggregate) iterator.next();

          buffer.append(" <tr>\n");
          buffer.append("  <td align=\"left\">");
          writeNames(buffer, point, 0);
          buffer.append("</td>\n");
          buffer.append("  <td align=\"right\">");
          writeMeasurements(buffer, point, 0);
          buffer.append("</td>\n");
          buffer.append("  <td align=\"right\">");
          writeTime(buffer, point, 0);
          buffer.append("</td>\n");
          buffer.append("  <td align=\"right\">");
          writeMinMax(buffer, point, 0);
          buffer.append("</td>\n");
          buffer.append("  <td align=\"right\">");
          writeTotals(buffer, point, 0);
          buffer.append("</td>\n");
          buffer.append(" </tr>\n");
        }

        responseWriter.write(buffer.toString().toCharArray());
        responseWriter.write(FOOTER.toCharArray());
        responseWriter.flush();
      } catch (IOException e) {
        throw new RuntimeException("Unable to write to writer: " + e);
      }
    }

    private void writeNames(StringBuffer aBuffer, Aggregate aPoint, int depth) {
      if (depth > 0) {
        writeNestingLevel(aBuffer, depth);
        aBuffer.append(TREE_SIGN);
      }

      aBuffer.append(aPoint.getName());
      aBuffer.append("<br />");

      if (aPoint.hasChilds()) {
        int currentDepth = depth + 1;

        Map childs = aPoint.getChilds();
        for (Iterator iterator = childs.values().iterator(); iterator.hasNext();) {
          Aggregate child = (Aggregate) iterator.next();
          writeNames(aBuffer, child, currentDepth);
        }
      }

    }

    private void writeTotals(StringBuffer aBuffer, Aggregate aPoint, int depth) {
      aBuffer.append(timeFormatter.format(aPoint.getTotal()));
      aBuffer.append("<br />");

      if (aPoint.hasChilds()) {
        Map childs = aPoint.getChilds();

        int currentDepth = depth + 1;

        for (Iterator iterator = childs.values().iterator(); iterator.hasNext();) {
          Aggregate child = (Aggregate) iterator.next();
          writeTotals(aBuffer, child, currentDepth);
        }
      }

    }

    private void writeTime(StringBuffer aBuffer, Aggregate aPoint, int depth) {
      aBuffer.append(timeFormatter.format(aPoint.getAverage()));
      aBuffer.append("<br />");

      if (aPoint.hasChilds()) {
        Map childs = aPoint.getChilds();

        int currentDepth = depth + 1;
        for (Iterator iterator = childs.values().iterator(); iterator.hasNext();) {
          Aggregate child = (Aggregate) iterator.next();
          writeTime(aBuffer, child, currentDepth + 1);
        }
      }

    }

    private void writeMinMax(StringBuffer aBuffer, Aggregate aPoint, int depth) {
      aBuffer.append(timeFormatter.format(aPoint.getMin()));
      aBuffer.append("/");
      aBuffer.append(timeFormatter.format(aPoint.getMax()));
      aBuffer.append("<br />");

      if (aPoint.hasChilds()) {
        Map childs = aPoint.getChilds();

        int currentDepth = depth + 1;
        for (Iterator iterator = childs.values().iterator(); iterator.hasNext();) {
          Aggregate child = (Aggregate) iterator.next();
          writeMinMax(aBuffer, child, currentDepth + 1);
        }
      }

    }

    private void writeMeasurements(StringBuffer aBuffer, Aggregate aPoint, int depth) {
      aBuffer.append(aPoint.getMeasurements());
      aBuffer.append("<br />");

      if (aPoint.hasChilds()) {
        Map childs = aPoint.getChilds();

        int currentDepth = depth + 1;
        for (Iterator iterator = childs.values().iterator(); iterator.hasNext();) {
          Aggregate child = (Aggregate) iterator.next();
          writeMeasurements(aBuffer, child, currentDepth);
        }
      }

    }

    protected void writeNestingLevel(StringBuffer buffer, int newDepth) {
      for (int i = 0; i < newDepth; i++) {
        buffer.append(SPACE);
      }
    }

  }


}
