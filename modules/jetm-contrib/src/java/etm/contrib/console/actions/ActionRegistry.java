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

import etm.contrib.console.ConsoleAction;
import etm.contrib.console.ConsoleRequest;
import etm.contrib.console.ConsoleResponse;
import etm.contrib.console.util.ResourceAccessor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author void.fm
 * @version $Revision$
 */
public class ActionRegistry {
  private Map actions = new HashMap();

  public ActionRegistry(ResourceAccessor resourceAccessor, boolean expanded) {
    if (expanded) {
      enableExpanded();
    } else {
      enableCollapsed();
    }
    
    actions.put("/", new RedirectAction("index"));
    actions.put("/reset", new ResetMonitorAction());
    actions.put("/start", new StartMonitorAction());
    actions.put("/stop", new StopMonitorAction());

    // content requests
    actions.put("/style.css", new ResourceAction("text/css;charset=UTF-8", resourceAccessor.getStyleSheet()));
    actions.put("/robots.txt", new ResourceAction("text/plain;charset=UTF-8", resourceAccessor.getRobotsTxt()));
    actions.put("/favicon.ico", new ResourceAction("image/x-icon", resourceAccessor.getFavicon()));
    actions.put("/down-arrow.png", new ResourceAction("image/png", resourceAccessor.getDownarrow()));
    actions.put("/up-arrow.png", new ResourceAction("image/png", resourceAccessor.getUparrow()));

    // workaround to alter actions at runtime
    actions.put("/expand", new RedirectAction("index") {
      public void execute(ConsoleRequest request, ConsoleResponse response) throws IOException {
        enableExpanded();
        super.execute(request, response);
      }
    });

    actions.put("/collapse", new RedirectAction("index") {
      public void execute(ConsoleRequest request, ConsoleResponse response) throws IOException {
        enableCollapsed();
        super.execute(request, response);
      }
    });

  }

  public ConsoleAction getAction(String action) {
    return (ConsoleAction) actions.get(action);
  }

  private void enableCollapsed() {
    actions.put("/index", new CollapsedResultViewAction());
    actions.put("/detail", new DetailAction());
  }

  private void enableExpanded() {
    actions.put("/index", new ExpandedResultViewAction());
  }

}
