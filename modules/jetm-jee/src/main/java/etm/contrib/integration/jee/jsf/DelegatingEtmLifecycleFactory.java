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

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The outer jsf request performance monitoring. Defaults to request uri for
 * outer measurement point unless {@link NameInterceptingActionListener}
 * detects a valid action name.
 *
 * @author void.fm
 * @version $Revision: 372 $
 */
public class DelegatingEtmLifecycleFactory extends LifecycleFactory {

  private LifecycleFactory delegate;
  private Map<String, Lifecycle> interceptedLifeCycles = new ConcurrentHashMap<String, Lifecycle>();

  public DelegatingEtmLifecycleFactory(LifecycleFactory aDelegate) {
    delegate = aDelegate;
  }

  @Override
  public void addLifecycle(String lifecycleId, Lifecycle lifecycle) {
    delegate.addLifecycle(lifecycleId, lifecycle);
  }

  @Override
  public Lifecycle getLifecycle(String lifecycleId) {
    Lifecycle lifecycle = interceptedLifeCycles.get(lifecycleId);
    if (lifecycle == null) {
      Lifecycle defaultLifeCycle = delegate.getLifecycle(lifecycleId);
      lifecycle = new InterceptedLifeCycle(defaultLifeCycle);
      interceptedLifeCycles.put(lifecycleId, lifecycle);
    }
    return lifecycle;
  }

  public Iterator<String> getLifecycleIds() {
    return delegate.getLifecycleIds();
  }

  static class InterceptedLifeCycle extends Lifecycle {

    private Lifecycle delegate;

    InterceptedLifeCycle(Lifecycle aDelegate) {
      delegate = aDelegate;
    }

    @Override
    public void addPhaseListener(PhaseListener listener) {
      delegate.addPhaseListener(listener);
    }

    @Override
    public void execute(FacesContext context) throws FacesException {
      EtmPoint requestPoint = EtmManager.getEtmMonitor().createPoint(getDefaultRequestName(context));
      context.getAttributes().put(EtmJsfPlugin.ROOT_ETM_POINT, requestPoint);

      delegate.execute(context);
    }

    @Override
    public PhaseListener[] getPhaseListeners() {
      return delegate.getPhaseListeners();
    }

    @Override
    public void removePhaseListener(PhaseListener listener) {
      delegate.removePhaseListener(listener);
    }

    @Override
    public void render(FacesContext context) throws FacesException {
      try {
        delegate.render(context);
      } finally {
        EtmPoint requestPoint = (EtmPoint) context.getAttributes().get(EtmJsfPlugin.ROOT_ETM_POINT);
        if (requestPoint != null) {
          requestPoint.collect();
          context.getAttributes().remove(EtmJsfPlugin.ROOT_ETM_POINT);
        }
      }
    }

    protected String getDefaultRequestName(FacesContext context) {
      HttpServletRequest httpServletRequest = (HttpServletRequest) context.getExternalContext().getRequest();
      String request = httpServletRequest.getRequestURI();
      int index = request.indexOf(';'); // get rid of jsession id's etc.
      if (index > 0) {
        request = request.substring(0, index);
      }

      String method = httpServletRequest.getMethod();
      return "HTTP " + method + " " + request;
    }
  }
}
