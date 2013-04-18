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
import etm.core.monitor.EtmPoint;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.Renderer;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author void.fm
 * @version $Revision: 372 $
 */
public class CglibDelegatingRenderKit extends RenderKitFactory {

  private final RenderKitFactory delegate;

  public CglibDelegatingRenderKit(RenderKitFactory aDelegate) {
    delegate = aDelegate;
  }

  @Override
  public void addRenderKit(String s, RenderKit aRenderKit) {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(aRenderKit.getClass());
    enhancer.setCallback(new RenderKitInterceptor(aRenderKit));

    delegate.addRenderKit(s, (RenderKit) enhancer.create());
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


  class RenderKitInterceptor implements MethodInterceptor {
    private RenderKit target;
    private Map<Renderer, Renderer> proxyCache = new ConcurrentHashMap<Renderer, Renderer>();


    RenderKitInterceptor(RenderKit aTarget) {
      target = aTarget;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
      if (method.getName().equals("getRenderer")) {
        Renderer renderer = (Renderer) method.invoke(target, args);
        if (renderer == null) {
          return null;
        } else {
          if (proxyCache.containsKey(renderer)) {
            return proxyCache.get(renderer);
          }

          Renderer proxyInstance = createProxyIfPossible(renderer);
          proxyCache.put(renderer, proxyInstance);

          return proxyInstance;

        }
      } else {
        return method.invoke(target, args);
      }
    }

    protected Renderer createProxyIfPossible(Renderer aRenderer) {
      Enhancer enhancer = new Enhancer();
      enhancer.setSuperclass(aRenderer.getClass());
      enhancer.setCallback(new RenderEtmInterceptor(aRenderer));

      return (Renderer) enhancer.create();

    }
  }

  public class RenderEtmInterceptor implements MethodInterceptor {
    private Renderer target;

    public RenderEtmInterceptor(Renderer aTarget) {
      target = aTarget;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
      if (method.getName().equals("encodeBegin")) {
        FacesContext context = (FacesContext) args[0];
        UIComponent component = (UIComponent) args[1];
        EtmPoint point = EtmManager.getEtmMonitor().createPoint("Render " + component.getClientId());
        context.getAttributes().put("ETM__" + component.getClientId(), point);

        return method.invoke(target, args);

      } else if (method.getName().equals("encodeEnd")) {
        try {
          return method.invoke(target, args);
        } finally {
          FacesContext context = (FacesContext) args[0];
          UIComponent component = (UIComponent) args[1];
          EtmPoint point = (EtmPoint) context.getAttributes().get("ETM__" + component.getClientId());
          if (point != null) {
            point.collect();
          }
        }
      } else {
        return method.invoke(target, args);
      }
    }
  }

}
