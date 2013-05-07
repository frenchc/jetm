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
import etm.core.monitor.EtmPoint;
import etm.core.util.Log;
import etm.core.util.LogAdapter;

import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.ApplicationWrapper;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * @author void.fm
 * @version $Revision: 372 $
 */
public class DelegatingEtmApplicationFactory extends ApplicationFactory {

  private static final LogAdapter LOG = Log.getLog(DelegatingEtmApplicationFactory.class);

  private ApplicationFactory delegate;

  private Application wrappedDelegate;

  public DelegatingEtmApplicationFactory(ApplicationFactory aDelegate) {
    delegate = aDelegate;

    if (!isEnabled()) {
      LOG.info("JSF converter/validator monitoring disabled.");
    } else {
      LOG.debug("JSF converter/validator monitoring enabled.");
    }
  }

  @Override
  public Application getApplication() {
    if (isEnabled() && wrappedDelegate == null) {
      wrappedDelegate = new EtmApplication(delegate.getApplication());
    }  else {
      return delegate.getApplication();
    }
    return wrappedDelegate;
  }

  @Override
  public void setApplication(Application application) {
    if (isEnabled()) {
      wrappedDelegate = new EtmApplication(application);
    }
    delegate.setApplication(application);
  }

  protected Boolean isEnabled() {
    Boolean enabled = false;

    EtmMonitor monitor = EtmManager.getEtmMonitor();
    PluginMetaData pluginMetaData = monitor.getMetaData().getPluginMetaData(EtmJsfPlugin.class);
    if (pluginMetaData != null) {
      String obj = (String) pluginMetaData.getProperties().get(EtmJsfPlugin.CONFIG_CONVERTER_VALIDATOR_MONITORING);
      if (obj != null) {
        enabled = Boolean.parseBoolean(obj);
      }
    }
    return enabled;
  }


  class EtmApplication extends ApplicationWrapper {
    private Application wrapped;

    EtmApplication(Application aWrapped) {
      wrapped = aWrapped;
    }

    @Override
    public Application getWrapped() {
      return wrapped;
    }

    @Override
    public Validator createValidator(String validatorId) throws FacesException {
      Validator validator = getWrapped().createValidator(validatorId);
      if (validator != null) {
        return new EtmValidator(validator);
      }

      return null;
    }

    @Override
    public Converter createConverter(String converterId) {
      Converter converter = getWrapped().createConverter(converterId);
      if (converter != null) {
        return new EtmConverter(converter);
      }

      return null;
    }

  }

  class EtmConverter implements Converter {

    private Converter converter;
    private String asObjectName;
    private String asStringName;

    EtmConverter(Converter aAConverter) {
      converter = aAConverter;
      asObjectName = aAConverter.getClass().getSimpleName() + ":getAsObject";
      asStringName = aAConverter.getClass().getSimpleName() + ":getAsString";
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
      EtmPoint point = EtmManager.getEtmMonitor().createPoint(asObjectName);
      try {
        return converter.getAsObject(context, component, value);
      } finally {
        point.collect();
      }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
      EtmPoint point = EtmManager.getEtmMonitor().createPoint(asStringName);
      try {
        return converter.getAsString(context, component, value);
      } finally {
        point.collect();
      }
    }
  }

  class EtmValidator implements Validator, StateHolder {

    private Validator validator;
    private String pointName;

    EtmValidator(Validator aAValidator) {
      validator = aAValidator;
      pointName = aAValidator.getClass().getSimpleName() + ":validate";
    }

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
      EtmPoint point = EtmManager.getEtmMonitor().createPoint(pointName);
      try {
        validator.validate(context, component, value);
      } finally {
        point.collect();
      }
    }

    @Override
    public boolean isTransient() {
      return true;
    }

    @Override
    public Object saveState(FacesContext context) {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
      //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setTransient(boolean newTransientValue) {
      //To change body of implemented methods use File | Settings | File Templates.
    }
  }


}
