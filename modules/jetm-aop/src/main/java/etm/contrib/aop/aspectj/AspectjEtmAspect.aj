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

package etm.contrib.aop.aspectj;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;

import etm.contrib.aop.common.AbstractEtmAspect;
import etm.contrib.aop.joinpoint.EtmJoinPoint;
import etm.contrib.aop.joinpoint.JoinPointFactory;

/**
 * Abstract AspectJ aspect that must be extended
 * with a pointcut definition in order to be weaved
 * into an application at compile time.
 * 
 * Example:
 * 
 * <code>
 * public aspect AdFetcherEtmAspect extends AspectjEtmAspect {
 *   pointcut methodsToBeMonitored(): execution(public * x.y.z.*.*(..));
 * }
 * </code>
 * 
 * Note: AspectJ 1.6 plans to support the configuration of concrete pointcuts
 * for abstract aspects via aop.xml. Until then a new aspect has to be derived.
 *
 * 
 * @author jenglisch
 * @version $Revision$ $Date$
 * @since 1.2.4
 */
public abstract aspect AspectjEtmAspect {

	private static final Log LOG = LogFactory.getLog(AspectjEtmAspect.class);
	
	Object around() throws Throwable: methodsToBeMonitored() {
  	if (thisJoinPoint instanceof ProceedingJoinPoint) {
  	  EtmJoinPoint joinPoint = JoinPointFactory.create((ProceedingJoinPoint) thisJoinPoint);
  	  DefaultEtmAspect etmAspect = new DefaultEtmAspect();
  	  return etmAspect.monitor(joinPoint);
  	} else {
  	  LOG.info("No ProceedingJoinPoint provided. Unable to measure execution times.");
  	  return proceed();
  	}
  }

	private class DefaultEtmAspect extends AbstractEtmAspect {};
  
}
