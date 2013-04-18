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

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmPoint;
import etm.core.util.Log;
import etm.core.util.LogAdapter;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewDeclarationLanguage;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author void.fm
 * @version $Revision: 372 $
 */
public class DelegatingEtmViewHandler extends ViewHandler {

  private static final LogAdapter LOG = Log.getLog(DelegatingEtmViewHandler.class);

  private ViewHandler delegate;

  public DelegatingEtmViewHandler(ViewHandler aDelegate) {
    delegate = aDelegate;
    LOG.debug("Activated " + getClass().getSimpleName() + ".");
  }

  @Override
  public void addProtectedView(String urlPattern) {
    delegate.addProtectedView(urlPattern);
  }

  @Override
  public String calculateCharacterEncoding(FacesContext context) {
    return delegate.calculateCharacterEncoding(context);
  }

  @Override
  public String deriveLogicalViewId(FacesContext context, String rawViewId) {
    return delegate.deriveLogicalViewId(context, rawViewId);
  }

  @Override
  public String deriveViewId(FacesContext context, String rawViewId) {
    return delegate.deriveViewId(context, rawViewId);
  }

  @Override
  public String getBookmarkableURL(FacesContext context, String viewId, Map<String, List<String>> parameters, boolean includeViewParams) {
    return delegate.getBookmarkableURL(context, viewId, parameters, includeViewParams);
  }

  @Override
  public Set<String> getProtectedViewsUnmodifiable() {
    return delegate.getProtectedViewsUnmodifiable();
  }

  @Override
  public String getRedirectURL(FacesContext context, String viewId, Map<String, List<String>> parameters, boolean includeViewParams) {
    return delegate.getRedirectURL(context, viewId, parameters, includeViewParams);
  }

  @Override
  public ViewDeclarationLanguage getViewDeclarationLanguage(FacesContext context, String viewId) {
    return delegate.getViewDeclarationLanguage(context, viewId);
  }

  @Override
  public void initView(FacesContext context) throws FacesException {
    delegate.initView(context);
  }

  @Override
  public boolean removeProtectedView(String urlPattern) {
    return delegate.removeProtectedView(urlPattern);
  }

  @Override
  public Locale calculateLocale(FacesContext aFacesContext) {
    return delegate.calculateLocale(aFacesContext);
  }

  @Override
  public String calculateRenderKitId(FacesContext aFacesContext) {
    return delegate.calculateRenderKitId(aFacesContext);
  }

  @Override
  public UIViewRoot createView(FacesContext aFacesContext, String s) {
    EtmPoint point = EtmManager.getEtmMonitor().createPoint("Create view " + s);
    try {
      return delegate.createView(aFacesContext, s);
    } finally {
      point.collect();
    }
  }

  @Override
  public String getActionURL(FacesContext aFacesContext, String s) {
    return delegate.getActionURL(aFacesContext, s);
  }

  @Override
  public String getResourceURL(FacesContext aFacesContext, String s) {
    return delegate.getResourceURL(aFacesContext, s);
  }

  @Override
  public void renderView(FacesContext aFacesContext, UIViewRoot aUIViewRoot) throws IOException, FacesException {
    EtmPoint point = EtmManager.getEtmMonitor().createPoint("Render view " + aUIViewRoot.getViewId());
    try {
      delegate.renderView(aFacesContext, aUIViewRoot);
    } finally {
      point.collect();
    }
  }

  @Override
  public UIViewRoot restoreView(FacesContext aFacesContext, String s) {
    EtmPoint point = EtmManager.getEtmMonitor().createPoint("Restore view " + s);
    try {
      return delegate.restoreView(aFacesContext, s);
    } finally {
      point.collect();
    }
  }

  @Override
  public void writeState(FacesContext aFacesContext) throws IOException {
    EtmPoint point = EtmManager.getEtmMonitor().createPoint("Write state");
    try {
      delegate.writeState(aFacesContext);
    } finally {
      point.collect();
    }
  }
}
