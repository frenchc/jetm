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

import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.faces.render.Renderer;
import javax.faces.render.ResponseStateManager;
import java.io.OutputStream;
import java.io.Writer;

/**
 * @author void.fm
 * @version $Revision: 372 $
 */
public class DelegatingEtmRenderKit extends RenderKit {

  private RenderKit delegate;

  public DelegatingEtmRenderKit(RenderKit aDelegate) {
    delegate = aDelegate;
  }

  @Override
  public void addRenderer(String s, String s2, Renderer aRenderer) {
    delegate.addRenderer(s, s2, aRenderer);
  }

  @Override
  public Renderer getRenderer(String s, String s2) {
    Renderer renderer = delegate.getRenderer(s, s2);
    if (renderer == null) {
      return null;
    } else {
      return new DelegatingEtmRenderer(renderer);
    }
  }

  @Override
  public ResponseStateManager getResponseStateManager() {
    return delegate.getResponseStateManager();
  }

  @Override
  public ResponseWriter createResponseWriter(Writer aWriter, String s, String s2) {
    return delegate.createResponseWriter(aWriter, s, s2);
  }

  @Override
  public ResponseStream createResponseStream(OutputStream aOutputStream) {
    return delegate.createResponseStream(aOutputStream);
  }

}
