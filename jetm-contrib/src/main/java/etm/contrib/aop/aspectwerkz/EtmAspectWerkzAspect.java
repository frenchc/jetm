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

package etm.contrib.aop.aspectwerkz;

import org.codehaus.aspectwerkz.joinpoint.StaticJoinPoint;

import etm.contrib.aop.common.AbstractEtmAspect;
import etm.contrib.aop.joinpoint.JoinPointFactory;

/**
 * AspectWerkz aspect that supports method invocations. Internally it uses
 * a static EtmMonitor provided by {@link etm.core.configuration.EtmManager#getEtmMonitor()}.
 * <p/>
 * Example usage that
 * records all method calls of all classes ending with <code>Service</code> using aop.xml.
 * <pre>
 * &lt;aspect class="etm.contrib.aop.aspectwerkz.EtmAspectWerkzAspect" deployment-model="perClass"&gt;
 * <p/>
 *  &lt;pointcut name="monitorServices" expression="execution(* ..*Service.*(..))    "/&gt;
 *  &lt;advice name="monitor" type="around" bind-to="monitorServices"/&gt;
 * &lt;/aspect&gt;
 * </pre>
 *
 * @author void.fm
 * @version $Revision$
 */
public class EtmAspectWerkzAspect extends AbstractEtmAspect {

  public Object monitor(StaticJoinPoint aJoinPoint) throws Throwable {
    return monitor(JoinPointFactory.create(aJoinPoint));
  }

}
