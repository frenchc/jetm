package etm.contrib.console.actions;

import etm.contrib.console.ConsoleRequest;
import etm.contrib.console.ConsoleResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Date;

/**
 * Renders Top Level Measurement Points Only.
 *
 * @author void.fm
 * @version $Revision$
 */
public class CollapsedResultViewAction extends AbstractAction {

  public void execute(ConsoleRequest request, ConsoleResponse response) throws IOException {
    response.addHeader("Content-Type", "text/html;charset=UTF-8");
    response.addHeader("Pragma", "no-cache");
    response.addHeader("Cache-Control", "no-cache");
    
    StringWriter writer = new StringWriter();
    request.getEtmMonitor().render(new CollapsedResultRenderer(writer));

    writeConsoleHeader(response, request.getEtmMonitor(), null);
    response.write(writer.toString().getBytes(UTF_8));
    response.write(" </body>\n</html>".getBytes());
  }
}