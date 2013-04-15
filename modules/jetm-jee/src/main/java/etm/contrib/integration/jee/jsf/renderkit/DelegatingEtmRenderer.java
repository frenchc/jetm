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

package etm.contrib.integration.jee.jsf.renderkit;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.render.Renderer;
import java.io.IOException;

/**
 * @author void.fm
 * @version $Revision: 372 $
 */
public class DelegatingEtmRenderer extends Renderer {
  private Renderer delegate;
  private EtmMonitor etmMonitor;

  public DelegatingEtmRenderer(Renderer aRenderer) {
    delegate = aRenderer;

    etmMonitor = EtmManager.getEtmMonitor();


  }

  @Override
  public String convertClientId(FacesContext context, String clientId) {
    return delegate.convertClientId(context, clientId);
  }

  @Override
  public void decode(FacesContext context, UIComponent component) {
    EtmPoint point = etmMonitor.createPoint("Decode " + component.getClientId());
    try {
      delegate.decode(context, component);
    } finally {
      point.collect();
    }
  }

  @Override
  public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
    EtmPoint point = etmMonitor.createPoint("Render " + component.getClientId());
    context.getAttributes().put("ETM__" + component.getClientId(), point);
    delegate.encodeBegin(context, component);
  }

  @Override
  public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
    delegate.encodeChildren(context, component);
  }

  @Override
  public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
    try {
      delegate.encodeEnd(context, component);
    } finally {
      EtmPoint point = (EtmPoint) context.getAttributes().get("ETM__" + component.getClientId());
      if (point != null) {
        point.collect();
      }
    }
  }

  @Override
  public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
    return delegate.getConvertedValue(context, component, submittedValue);
  }

  @Override
  public boolean getRendersChildren() {
    return delegate.getRendersChildren();
  }
}
