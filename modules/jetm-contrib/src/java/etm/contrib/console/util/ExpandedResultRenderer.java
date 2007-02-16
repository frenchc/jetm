package etm.contrib.console.util;

import etm.contrib.console.ConsoleRequest;
import etm.contrib.console.ConsoleResponse;
import etm.contrib.renderer.comparator.ExecutionAggregateComparator;
import etm.core.aggregation.Aggregate;
import etm.core.monitor.EtmException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

/**
 * Renders a expanded view.
 *
 * @author void.fm
 * @version $Revision$
 */
public class ExpandedResultRenderer extends ConsoleRenderer {

  /**
   * Constructs a CollapsedResultRenderer that writes results to
   * the given response.
   *
   * @param aRequest    The current request
   * @param aResponse   The current respone
   * @param aComparator A comparator used for sorting results
   */

  public ExpandedResultRenderer(ConsoleRequest aRequest, ConsoleResponse aResponse, ExecutionAggregateComparator aComparator) {
    super(aRequest, aResponse, aComparator);
  }


  public void render(Map points) {
    Object[] values = points.values().toArray();

    try {
      writeHtmlHead(true);

      response.write("<table>\n");
      writeTableHeader();

      if (points.size() == 0) {
        response.write(NO_RESULTS);
      } else {

        Arrays.sort(values, comparator);
        for (int i = 0; i < values.length; i++) {
          SortedExecutionGraph graphSorted = new SortedExecutionGraph((Aggregate) values[i], comparator);

          response.write(" <tr>\n");
          response.write("  <td>");
          writeName(graphSorted, 0);
          response.write("</td>\n");
          response.write("  <td>");
          writeMeasurements(graphSorted, 0);
          response.write("</td>\n");
          response.write("  <td>");
          writeAverage(graphSorted, 0);
          response.write("</td>\n");
          response.write("  <td>");
          writeMin(graphSorted, 0);
          response.write("</td>\n");
          response.write("  <td>");
          writeMax(graphSorted, 0);
          response.write("</td>\n");
          response.write("  <td>");
          writeTotals(graphSorted, 0);
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
