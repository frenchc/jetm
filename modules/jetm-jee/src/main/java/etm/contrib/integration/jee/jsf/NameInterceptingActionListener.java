/*
 *
 * Copyright (c) void.fm
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

package etm.contrib.integration.jee.jsf;

import etm.core.monitor.EtmPoint;

import jakarta.el.MethodExpression;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AbortProcessingException;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.event.ActionListener;

/**
 * Renames request parent etm point to current executed action
 * and delegates action event to delegate.
 *
 * @author void.fm
 * @version $Revision: 372 $
 */
public class NameInterceptingActionListener implements ActionListener {

  private ActionListener delegate;

  public NameInterceptingActionListener(ActionListener aDelegate) {
    delegate = aDelegate;
  }

  public void processAction(ActionEvent actionEvent) throws AbortProcessingException {
    UIComponent component = actionEvent.getComponent();
    EtmPoint etmRootPoint =
      (EtmPoint) FacesContext.getCurrentInstance().getAttributes().get(EtmJsfPlugin.ROOT_ETM_POINT);
    if (etmRootPoint != null) {
      MethodExpression me = null;

      if (component instanceof jakarta.faces.component.UICommand) {
        me = ((jakarta.faces.component.UICommand) component).getActionExpression();
      }

      if (me != null) {
        etmRootPoint.alterName("JSF Action " + me.getExpressionString());
      }
    }

    delegate.processAction(actionEvent);
  }
}
