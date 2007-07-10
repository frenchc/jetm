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

package etm.contrib.aop.jboss;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;
import org.jboss.aop.advice.Interceptor;
import org.jboss.aop.joinpoint.ConstructorInvocation;
import org.jboss.aop.joinpoint.Invocation;

import java.lang.reflect.Constructor;

/**
 * A interceptor that may be used to advise constructor invocations. Be aware that binding
 * this interceptor to a non constructor join point will likely cause a class cast exception.
 *
 * @author void.fm
 * @version $Revision$
 * @since 1.2.2
 */
public class EtmJbossConstructorInterceptor extends JbossInterceptorSupport implements Interceptor {

  protected final EtmMonitor etmMonitor = EtmManager.getEtmMonitor();

  public String getName() {
    return "EtmJbossConstructorInterceptor";
  }

  public Object invoke(Invocation aInvocation) throws Throwable {
    EtmPoint etmPoint = etmMonitor.createPoint(calculateName((ConstructorInvocation) aInvocation));
    try {
      return aInvocation.invokeNext();
    } catch (Throwable t) {
      alterNamePostException(etmPoint, t);
      throw t;
    } finally {
      etmPoint.collect();
    }
  }

  /**
   * Calculate EtmPoint name based on the method.
   *
   * @param aInvocation The method invocation.
   * @return The name of the EtmPoint.
   */
  protected String calculateName(ConstructorInvocation aInvocation) {
    Constructor constructor = aInvocation.getConstructor();

    return calculateShortName(constructor.getDeclaringClass()) + "::" + constructor.getName();
  }


}