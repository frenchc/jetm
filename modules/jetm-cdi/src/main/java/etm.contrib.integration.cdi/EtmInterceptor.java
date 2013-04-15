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

import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;

/**
 *
 * Our CDI interceptor for public method monitoring.
 *
 * @author void.fm
 * @version $Revision: 3373 $
 * @since 1.3.0
 */
@Interceptor
@Measure
public class EtmInterceptor implements Serializable {
  @Inject
  private EtmMonitor monitor;

  @AroundInvoke
  public Object measure(InvocationContext ctx) throws Exception {

    Class<? extends Object> aClass = ctx.getTarget().getClass();
    if (aClass.isSynthetic()) {
      aClass = aClass.getSuperclass();
    }

    String targetMethod = aClass.getSimpleName() + ":" + ctx.getMethod().getName();

    EtmPoint point = monitor.createPoint(targetMethod);
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
    Class<? extends Object> aClass = ctx.getTarget().getClass();
    if (aClass.isSynthetic()) {
      aClass = aClass.getSuperclass();
    }

    String targetMethod = aClass.getSimpleName() + "<PostConstruct>";

    EtmPoint point = monitor.createPoint(targetMethod);
    try {
      ctx.proceed();
    } catch (Exception e) {
      point.alterName(targetMethod + "[ " + e.getClass().getSimpleName() + "]");
      throw new RuntimeException(e);
    } finally {
      point.collect();
    }
  }

}
