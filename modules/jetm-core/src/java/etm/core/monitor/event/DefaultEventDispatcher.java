/*
 *
 * Copyright (c) 2004, 2005, 2006, 2007 void.fm
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
package etm.core.monitor.event;

import etm.core.util.Log;
import etm.core.util.LogAdapter;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Default event dispatcher implementation.
 *
 * @author void.fm
 * @version $Revision$
 * @since 1.2.0
 */
public class DefaultEventDispatcher implements EventDispatcher {

  private static final LogAdapter log = Log.getLog(DefaultEventDispatcher.class);


  private Map dispatchingRules = new HashMap();
  private Map listeners = new HashMap();


  public DefaultEventDispatcher() {
    registerDispatchRules();
  }


  public void register(EtmMonitorListener listener) {
    for (Iterator iterator = listeners.keySet().iterator(); iterator.hasNext();) {
      Class clazz = (Class) iterator.next();
      if (clazz.isAssignableFrom(listener.getClass())) {
        Set set = (Set) listeners.get(clazz);
        set.add(listener);
      }
    }

  }

  public void deregister(EtmMonitorListener listener) {
    for (Iterator iterator = listeners.keySet().iterator(); iterator.hasNext();) {
      Class clazz = (Class) iterator.next();
      if (clazz.isAssignableFrom(listener.getClass())) {
        Set set = (Set) listeners.get(clazz);
        set.remove(listener);
      }
    }


  }

  public void fire(EtmMonitorEvent event) {
    DispatchingRule rule = (DispatchingRule) dispatchingRules.get(event.getClass());

    if (rule != null) {
      Set currentListeners = (Set) listeners.get(rule.getListener());
      if (currentListeners != null) {
        sendEvent(currentListeners.toArray(), rule.getMethod(), event);
      }
    } else {
      log.warn("Unable to process event from type " + event.getClass());
    }
  }

  protected void sendEvent(Object[] aObjects, Method aMethod, EtmMonitorEvent aEvent) {
    for (int i = 0; i < aObjects.length; i++) {
      Object object = aObjects[i];
      try {
        aMethod.invoke(object, new Object[]{aEvent});
      } catch (Exception e) {
        log.warn("Unable to send event " + aEvent, e);
      }
    }
  }

  protected void registerDispatchRules() {
    dispatchingRules.put(AggregationStateLoadedEvent.class,
      new DispatchingRule(AggregationStateListener.class, "onStateLoaded"));

    dispatchingRules.put(PreMonitorResetEvent.class,
      new DispatchingRule(AggregationListener.class, "preStateReset"));
    dispatchingRules.put(MonitorResetEvent.class,
      new DispatchingRule(AggregationListener.class, "onStateReset"));
    dispatchingRules.put(RootCreateEvent.class,
      new DispatchingRule(AggregationListener.class, "onRootCreate"));
    dispatchingRules.put(RootResetEvent.class,
      new DispatchingRule(AggregationListener.class, "onRootReset"));
    dispatchingRules.put(PreRootResetEvent.class,
      new DispatchingRule(AggregationListener.class, "preRootReset"));

    dispatchingRules.put(CollectionEnabledEvent.class,
      new DispatchingRule(CollectionStatusListener.class, "onCollectionEnabled"));
    dispatchingRules.put(CollectionDisabledEvent.class,
      new DispatchingRule(CollectionStatusListener.class, "onCollectionDisabled"));

    dispatchingRules.put(CollectEvent.class,
      new DispatchingRule(CollectionListener.class, "onCollect"));

    listeners.put(AggregationStateListener.class, new HashSet());
    listeners.put(AggregationListener.class, new HashSet());
    listeners.put(CollectionStatusListener.class, new HashSet());
    listeners.put(CollectionListener.class, new HashSet());
  }


  class DispatchingRule {
    private Method method;
    private Class listener;


    public DispatchingRule(Class aListener, String aMethodName) {
      listener = aListener;

      Method[] declaredMethods = listener.getDeclaredMethods();
      for (int i = 0; i < declaredMethods.length; i++) {
        Method declaredMethod = declaredMethods[i];
        if (aMethodName.equals(declaredMethod.getName()) &&
          declaredMethod.getParameterTypes().length == 1) {
          method = declaredMethod;
          break;
        }
      }

      if (method == null) {
        throw new IllegalArgumentException("There is no matching method " + aMethodName + " in " + aListener.getName());
      }
    }

    public Method getMethod() {
      return method;
    }

    public Class getListener() {
      return listener;
    }
  }
}
