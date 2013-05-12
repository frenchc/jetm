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

package etm.contrib.integration.cdi;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmPoint;

import javax.annotation.PostConstruct;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Our CDI interceptor for public method monitoring. Will be used for class and
 * method level binding and supports both lifecycle and method interception.
 *
 * @author void.fm
 * @version $Revision: 3373 $
 * @since 1.3.0
 */
@Interceptor
@Measure
public class EtmInterceptor implements Serializable {

  private transient Map<Method, String> methodNameCache = new ConcurrentHashMap<Method, String>();
  private transient Map<Class, String> classNameCache = new ConcurrentHashMap<Class, String>();

  private Set<Class> proxyClasses = new HashSet<Class>();
  private static final String[] PROXY_CLASSES = {"javassist.util.proxy.ProxyObject","org.jboss.weld.bean.proxy.ProxyObject"};

  public EtmInterceptor() {
    for (String className : PROXY_CLASSES) {
      try {
        proxyClasses.add(Class.forName(className));
      } catch (ClassNotFoundException e) {
        // ignore, does not exist in classpath
      }
    }
  }

  @AroundInvoke
  public Object measure(InvocationContext ctx) throws Exception {
    String targetMethod = calculateMethodName(ctx);

    EtmPoint point = EtmManager.getEtmMonitor().createPoint(targetMethod);
    try {
      return ctx.proceed();
    } catch (Exception e) {
      point.alterName(targetMethod + "[ " + e.getClass().getSimpleName() + "]");
      throw e;
    } finally {
      point.collect();
    }
  }


  @PostConstruct
  public void measureCreate(InvocationContext ctx) {
    Class aClass = ctx.getTarget().getClass();
    String targetMethod = calculateClassName(aClass) + ":<PostConstruct>";

    EtmPoint point = EtmManager.getEtmMonitor().createPoint(targetMethod);
    try {
      ctx.proceed();
    } catch (Exception e) {
      point.alterName(targetMethod + "[ " + e.getClass().getSimpleName() + "]");
      throw new RuntimeException(e);
    } finally {
      point.collect();
    }
  }

  protected String calculateMethodName(InvocationContext ctx) {
    Class targetClass = ctx.getTarget().getClass();
    Method method = ctx.getMethod();

    String name = methodNameCache.get(method);
    if (name == null) {


      StringBuilder buffer = new StringBuilder();
      buffer.append(calculateClassName(targetClass));
      buffer.append(':');
      buffer.append(method.getName());
      buffer.append('(');
      Class<?>[] parameterTypes = method.getParameterTypes();
      for (Class clazz : parameterTypes) {
        buffer.append(clazz.getSimpleName());
        buffer.append(",");
      }
      if (parameterTypes.length > 0) {
        buffer.setCharAt(buffer.length() - 1, ')');
      } else {
        buffer.append(')');
      }
      name = buffer.toString();
      methodNameCache.put(method, name);
    }
    return name;
  }

  protected String calculateClassName(Class clazz) {
    String className = classNameCache.get(clazz);

    if (className == null) {
      if (clazz.isSynthetic() || isProxy(clazz)) {
        className = clazz.getSuperclass().getSimpleName();
      } else {
        className = clazz.getSimpleName();
      }
      classNameCache.put(clazz, className);
    }

    return className;
  }


  private boolean isProxy(Class aTargetClass) {
    for(Class clazz : aTargetClass.getInterfaces()) {
      if (proxyClasses.contains(clazz)) {
        return true;
      }
    }

    return false;
  }

  public void readObject(ObjectInputStream in) throws IOException,
                                   ClassNotFoundException {
    in.defaultReadObject();
    // start with empty caches.
    classNameCache = new ConcurrentHashMap<Class, String>();
    methodNameCache = new ConcurrentHashMap<Method, String>();

  }

}
