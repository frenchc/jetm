/*
 *
 * Copyright (c) 2004, 2005, 2006 void.fm
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

import etm.contrib.console.ConsoleRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Date;

/**
 * Base class for all actions.
 *
 * @author void.fm
 * @version $Id: DetailAction.java,v 1.2 2006/09/10 13:33:26 french_c Exp $
 */
public class DetailAction extends AbstractAction {


  public void execute(ConsoleRequest request, OutputStream out) throws IOException {
    String point = request.getRequestParameter("point");

    if (point == null) {
      sendRedirect(out, "/");
    } else {
      out.write("HTTP/1.0 200 OK\n".getBytes());
      out.write(SERVER_HEADER);
      out.write("Content-Type: text/html;charset=UTF-8\n".getBytes());
      out.write(("Date: " + new Date() + "\n").getBytes());
      out.write(("Pragma: no-cache\n").getBytes());
      out.write(("Cache-Control: no-cache\n").getBytes());
      out.write("Connection: close\n".getBytes());
      out.write("\n".getBytes());

      StringWriter writer = new StringWriter();
      request.getEtmMonitor().render(new DetailResultRenderer(writer, point));

      writeConsoleHeader(out, request.getEtmMonitor(), point);
      out.write(writer.toString().getBytes(UTF_8));
      out.write(" </body>\n</html>".getBytes());
    }
  }
}