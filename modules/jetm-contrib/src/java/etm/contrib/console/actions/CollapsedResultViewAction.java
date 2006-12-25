package etm.contrib.console.actions;

import etm.contrib.console.ConsoleRequest;

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

  public void execute(ConsoleRequest request, OutputStream out) throws IOException {
    out.write("HTTP/1.0 200 OK\n".getBytes());
    out.write(SERVER_HEADER);
    out.write("Content-Type: text/html;charset=UTF-8\n".getBytes());
    out.write(("Date: " + new Date() + "\n").getBytes());
    out.write(("Pragma: no-cache\n").getBytes());
    out.write(("Cache-Control: no-cache\n").getBytes());
    out.write("Connection: close\n".getBytes());
    out.write("\n".getBytes());

    StringWriter writer = new StringWriter();
    request.getEtmMonitor().render(new CollapsedResultRenderer(writer));

    writeConsoleHeader(out, request.getEtmMonitor(), null);
    out.write(writer.toString().getBytes(UTF_8));
    out.write(" </body>\n</html>".getBytes());
  }
}