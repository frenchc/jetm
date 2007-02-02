package etm.contrib.console.actions;

import etm.contrib.console.ConsoleRequest;
import etm.contrib.console.ConsoleResponse;
import etm.contrib.console.util.CollapsedResultRenderer;
import etm.contrib.renderer.comparator.ExecutionAggregateComparator;

import java.io.IOException;

/**
 * Renders Top Level EtmPoints Only.
 *
 * @author void.fm
 * @version $Revision$
 */
public class CollapsedResultViewAction extends AbstractAction {

  public void execute(ConsoleRequest request, ConsoleResponse response) throws IOException {
    response.addHeader("Content-Type", "text/html;charset=UTF-8");
    response.addHeader("Pragma", "no-cache");
    response.addHeader("Cache-Control", "no-cache");


    ExecutionAggregateComparator comparator = getComparator(request);
    CollapsedResultRenderer collapsedResultRenderer = new CollapsedResultRenderer(request, response, comparator);
    request.getEtmMonitor().render(collapsedResultRenderer);
  }


}