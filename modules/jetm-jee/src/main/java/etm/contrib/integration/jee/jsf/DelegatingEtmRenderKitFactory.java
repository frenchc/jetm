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
import etm.core.metadata.PluginMetaData;
import etm.core.monitor.EtmMonitor;
import etm.core.util.Log;
import etm.core.util.LogAdapter;

import javax.faces.context.FacesContext;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import java.lang.reflect.Constructor;
import java.util.Iterator;

/**
 * @author void.fm
 * @version $Revision: 372 $
 */
public class DelegatingEtmRenderKitFactory extends RenderKitFactory {

  private static final LogAdapter LOG = Log.getLog(DelegatingEtmRenderKitFactory.class);

  private static final String CGLIB_DELEGATE_CLASS_NAME = "etm.contrib.integration.jee.jsf.renderkit.CglibDelegatingRenderKit";

  private RenderKitFactory delegate;

  public DelegatingEtmRenderKitFactory(RenderKitFactory aKitFactory) {
    // default value, may be overridden
    delegate = aKitFactory;

    if (!isEnabled()) {
      LOG.info("JSF component monitoring disabled.");
    } else if (isCglibAvailable()) {
      try {
        Class<RenderKitFactory> aClass = (Class<RenderKitFactory>) Class.forName(CGLIB_DELEGATE_CLASS_NAME);
        Constructor<RenderKitFactory> constructor = aClass.getConstructor(new Class[]{RenderKitFactory.class});
        delegate = constructor.newInstance(aKitFactory);

        LOG.debug("Activated JSF component monitoring.");
      } catch (Exception e) {
        LOG.warn("Unable to create CGLIB proxy for " + aKitFactory.getClass() + ". Component monitoring disabled:" + e.getMessage());
      }
    } else {
      LOG.warn("CGLIB not found. Component monitoring disabled.");
    }
  }


  @Override
  public void addRenderKit(String renderKitId, RenderKit renderKit) {
    delegate.addRenderKit(renderKitId, renderKit);
  }

  @Override
  public RenderKit getRenderKit(FacesContext context, String renderKitId) {
    return delegate.getRenderKit(context, renderKitId);
  }

  @Override
  public Iterator<String> getRenderKitIds() {
    return delegate.getRenderKitIds();
  }

  @Override
  public RenderKitFactory getWrapped() {
    return delegate;
  }

  protected Boolean isEnabled() {
    Boolean enabled = false;

    EtmMonitor monitor = EtmManager.getEtmMonitor();
    PluginMetaData pluginMetaData = monitor.getMetaData().getPluginMetaData(EtmJsfPlugin.class);
    if (pluginMetaData != null) {
      String obj = (String) pluginMetaData.getProperties().get(EtmJsfPlugin.CONFIG_COMPONENT_MONITORING);
      if (obj != null) {
        enabled = Boolean.parseBoolean(obj);
      }
    }
    return enabled;
  }

  protected boolean isCglibAvailable() {
    try {
      Class.forName("net.sf.cglib.proxy.Enhancer");
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }

}
