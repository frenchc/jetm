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

import etm.contrib.integration.jee.jsf.renderkit.DelegatingEtmRenderKit;
import etm.core.util.Log;
import etm.core.util.LogAdapter;

import javax.faces.context.FacesContext;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import java.util.Iterator;

/**
 * @author void.fm
 * @version $Revision: 372 $
 */
public class DelegatingEtmRenderKitFactory extends RenderKitFactory {

  private static final LogAdapter LOG = Log.getLog(DelegatingEtmRenderKitFactory.class);


  private final RenderKitFactory delegate;

  public DelegatingEtmRenderKitFactory(RenderKitFactory aKitFactory) {
    delegate = aKitFactory;
    LOG.debug("Activated " + getClass().getSimpleName() + ".");
  }

  @Override
  public void addRenderKit(String s, RenderKit aRenderKit) {
    delegate.addRenderKit(s, new DelegatingEtmRenderKit(aRenderKit));
  }

  @Override
  public RenderKit getRenderKit(FacesContext aFacesContext, String s) {
    return delegate.getRenderKit(aFacesContext, s);
  }

  @Override
  public Iterator<String> getRenderKitIds() {
    return delegate.getRenderKitIds();
  }

  @Override
  public RenderKitFactory getWrapped() {
    return delegate.getWrapped();
  }
}
