/*
 *
 * Copyright (c) 2004, 2005, 2006, 2007, 2008, 2009 void.fm
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

package etm.contrib.aop.joinpoint;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.jboss.aop.joinpoint.ConstructorInvocation;
import org.jboss.aop.joinpoint.Invocation;
import org.jboss.aop.joinpoint.MethodInvocation;


/**
 * JbossAOP joinpoint.
 * 
 * @author jenglisch
 * @version $Revision$ $Date$
 * @since 1.2.4 
 */
public class JbossJoinPoint extends AbstractJoinPoint implements EtmJoinPoint {

  private Invocation invocation;
  
  public JbossJoinPoint(Invocation anInvocation) {
    invocation = anInvocation;
  }

  /**
   * @see #proceed() 
   */
  public Object proceed() throws Throwable {
    return invocation.invokeNext();
  }
  
  /**
   * @see #calculateName()
   */
  public String calculateName() {
    if (invocation instanceof ConstructorInvocation) {
      Constructor constructor = ((ConstructorInvocation) invocation).getConstructor();
      return calculateShortName(constructor.getDeclaringClass()) + "::" + constructor.getName();      
    } else if (invocation instanceof MethodInvocation) {
      Method method = ((MethodInvocation) invocation).getMethod();
      return calculateName(method.getDeclaringClass(),  method.getName());
    } else {
      return "Unsupported JBossAOP invocation type: " + invocation.getClass();
    }
  }
  
}
