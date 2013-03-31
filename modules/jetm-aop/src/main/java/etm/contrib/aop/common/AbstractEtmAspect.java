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

package etm.contrib.aop.common;

import etm.contrib.aop.joinpoint.EtmJoinPoint;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

/**
 * Base etm aspect that provides execution time measuring
 * in {@link #monitor(EtmJoinPoint)} and {@link #monitor(EtmJoinPoint, String)}.
 * 
 * @author jenglisch
 * @version $Revision$ $Date$
 * @since 1.2.4
 *
 */
public abstract class AbstractEtmAspect {

  private EtmMonitor etmMonitor;

  public AbstractEtmAspect() {
    this(EtmManager.getEtmMonitor());
  }
  
  public AbstractEtmAspect(EtmMonitor anEtmMonitor) {
    etmMonitor = anEtmMonitor;
  }
  
  public Object monitor(EtmJoinPoint aJoinPoint) throws Throwable {
    return monitor(aJoinPoint, aJoinPoint.calculateName());
  }
  
  public Object monitor(EtmJoinPoint aJoinPoint, String aJoinPointName) throws Throwable {
    EtmPoint etmPoint = etmMonitor.createPoint(aJoinPointName);
    try {
      return aJoinPoint.proceed();
    } catch (Throwable t) {
      aJoinPoint.alterNamePostException(etmPoint, t);
      throw t;
    } finally {
      etmPoint.collect();
    }
  }  

}
 