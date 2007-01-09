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

package etm.contrib.console;

import etm.contrib.console.actions.ActionRegistry;
import etm.contrib.console.servlet.ServletConsoleRequest;
import etm.contrib.console.servlet.ServletConsoleResponse;
import etm.contrib.console.util.ResourceAccessor;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * A servlet that renders aggregated EtmMonitor results. The EtmMonitor will be retrieve from
 * {@link etm.core.configuration.EtmManager#getEtmMonitor()}. Therefore it is recommended to
 * to this service in conjuction with the Etm Lifecycle support
 * {@link etm.contrib.integration.web.EtmMonitorContextListener}.
 *
 * @version $Revision$
 * @author void.fm
 *
 */
public class HttpConsoleServlet extends HttpServlet {

  protected ActionRegistry actionRegistry;
  protected EtmMonitor etmMonitor;
  protected ServletConfig servletConfig;

  public void init(ServletConfig aServletConfig) throws ServletException {
    super.init(aServletConfig);

    servletConfig = aServletConfig;
    // todo read expanded from config
    actionRegistry = new ActionRegistry(new ResourceAccessor(), false);
    etmMonitor = getEtmMonitor();
  }

  protected void doGet(HttpServletRequest aHttpServletRequest, HttpServletResponse aHttpServletResponse) throws ServletException, IOException {
    String actionName = null;

    String requestUri = aHttpServletRequest.getRequestURI();
    int i = requestUri.lastIndexOf("/");
    if (i >= 0 ) {
      actionName = requestUri.substring(i);
    }

    if ((actionName == null || actionName.length() == 0) && requestUri.indexOf(".") < 0 ) {
      actionName = "/"; 
    }

    ConsoleAction action = actionRegistry.getAction(actionName);
    if (action == null) {
      aHttpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    action.execute(new ServletConsoleRequest(etmMonitor, aHttpServletRequest), new ServletConsoleResponse(aHttpServletResponse));
  }

  protected EtmMonitor getEtmMonitor() throws ServletException {
    return EtmManager.getEtmMonitor();
  }

}
