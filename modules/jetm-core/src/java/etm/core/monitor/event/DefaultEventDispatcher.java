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

import java.lang.reflect.Method;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * Default event dispatcher implementation.
 *
 * @author void.fm
 * @version $Revision$
 * @since 1.2.0
 */
public class DefaultEventDispatcher implements EventDispatcher {

  private Map dispatchingRules = new HashMap();
  private Map listeners = new HashMap();


  public DefaultEventDispatcher() {
    registerDispatchRules();
  }


  public void register(EventListener listener) {
    Set instances = (Set) listeners.get(listener.getClass());
    if (instances != null) {
      instances.add(listener);
    }

  }

  public void deregister(EventListener listener) {
     Set instances = (Set) listeners.get(listener.getClass());
    if (instances != null) {
      instances.remove(listener);
    }
  }

  public void fire(EtmMonitorEvent event) {
    DispatchingRule rule = (DispatchingRule) dispatchingRules.get(event.getClass());

    if (rule != null) {
      List currentListeners = (List) listeners.get(rule.getListener());
      sendEvent(currentListeners.toArray(), rule.getMethod(), event);
    } else {
      System.err.println("Unable to process event from type " + event.getClass());
    }
  }

  private void sendEvent(Object[] aObjects, Method aMethod, EtmMonitorEvent aEvent) {
    for (int i = 0; i < aObjects.length; i++) {
      Object object = aObjects[i];
      try {
        aMethod.invoke(object, new Object[]{aEvent});
      } catch (Exception e) {
        // TODO
        e.printStackTrace();
      }
    }
  }

  private void registerDispatchRules() {
    dispatchingRules.put(AggregationStateLoadedEvent.class,
      new DispatchingRule(AggregationStateListener.class, "onStateLoaded"));


    dispatchingRules.put(CollectionEnabledEvent.class,
      new DispatchingRule(CollectionStatusListener.class, "onCollectionDEnabled"));
    dispatchingRules.put(CollectionDisabledEvent.class,
      new DispatchingRule(CollectionDisabledEvent.class, "onCollectionDisabled"));
    dispatchingRules.put(MonitorResetEvent.class,
      new DispatchingRule(EtmMonitorListener.class, "onMonitorReset"));

    listeners.put(AggregationStateListener.class, new HashSet());
    listeners.put(EtmMonitorListener.class, new HashSet());

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
