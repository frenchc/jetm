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

package etm.contrib.integration.jee.jsf.wrapped;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmPoint;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import java.lang.reflect.Method;

/**
 * @author void.fm
 * @version $Revision: 372 $
 */
public class CGlibDelegatingApplicationFactory extends ApplicationFactory {

  private ApplicationFactory delegate;
  private Application application;

  public CGlibDelegatingApplicationFactory(ApplicationFactory aDelegate) {
    delegate = aDelegate;
    application = createdProxiedApplication(aDelegate.getApplication());
  }


  @Override
  public Application getApplication() {
    return application;
  }

  @Override
  public void setApplication(Application aApplication) {
    application = createdProxiedApplication(aApplication);
    delegate.setApplication(aApplication);
  }

  protected Application createdProxiedApplication(Application aDelegate) {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(Application.class);
    enhancer.setCallback(new ApplicationInterceptor(aDelegate));

    return (Application) enhancer.create();
  }


  class ApplicationInterceptor implements MethodInterceptor {

    private Application delegateApplication;

    public ApplicationInterceptor(Application aDelegateApplication) {
      delegateApplication = aDelegateApplication;
    }

    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
      if (method.getName().equals("createConverter")) {
        Converter result = (Converter) method.invoke(delegateApplication, args);
        if (result != null) {
          return new EtmConverter(result);
        } else {
          return null;
        }
      } else if (method.getName().equals("createValidator")) {
        Validator result = (Validator) method.invoke(delegateApplication, args);
        if (result != null) {
          return new EtmValidator(result);
        } else {
          return null;
        }

      } else {
        return method.invoke(delegateApplication, args);
      }
    }


  }

  class EtmConverter implements Converter {

    private Converter delegate;
    private String asObjectName;
    private String asStringName;

    EtmConverter(Converter aDelegate) {
      delegate = aDelegate;
      asObjectName = aDelegate.getClass().getSimpleName() + ":getAsObject";
      asStringName = aDelegate.getClass().getSimpleName() + ":getAsString";
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
      EtmPoint point = EtmManager.getEtmMonitor().createPoint(asObjectName);
      try {
        return delegate.getAsObject(context, component, value);
      } finally {
        point.collect();
      }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
      EtmPoint point = EtmManager.getEtmMonitor().createPoint(asStringName);
      try {
        return delegate.getAsString(context, component, value);
      } finally {
        point.collect();
      }
    }
  }

  class EtmValidator implements Validator {

    private Validator delegate;
    private String pointName;

    EtmValidator(Validator aDelegate) {
      delegate = aDelegate;
      pointName = aDelegate.getClass().getSimpleName() + ":validate";
    }

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
      EtmPoint point = EtmManager.getEtmMonitor().createPoint(pointName);
      try {
        delegate.validate(context, component, value);
      } finally {
        point.collect();
      }
    }
  }


}
