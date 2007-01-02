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

package etm.contrib.aop.aspectwerkz;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.MeasurementPoint;
import org.codehaus.aspectwerkz.joinpoint.Signature;
import org.codehaus.aspectwerkz.joinpoint.StaticJoinPoint;

/**
 * AspectWerkz aspect that supports method invocations. Interally it uses
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
public class EtmAspectWerkzAspect {

  protected final EtmMonitor etmMonitor = EtmManager.getEtmMonitor();


  public Object monitor(StaticJoinPoint joinPoint) throws Throwable {
    MeasurementPoint measurementPoint = new MeasurementPoint(etmMonitor, calculateName(joinPoint));
    try {
      return joinPoint.proceed();
    } catch (Throwable t) {
      alterNamePostException(measurementPoint, t);
      throw t;
    } finally {
      measurementPoint.collect();
    }

  }

  /**
   * Calculate measurement point name based on the method invocation.
   *
   * @param joinPoint The method invocation.
   * @return The name of the measurement point.
   */
  protected String calculateName(StaticJoinPoint joinPoint) {
    Signature method = joinPoint.getSignature();

    return calculateShortName(method.getDeclaringType()) + "::" + method.getName();
  }

  /**
   * Alter name in case an exception is caught during processing. Altering the
   * name takes place after executing target method. Ensure that you never cause
   * an exception within this code.
   *
   * @param aMeasurementPoint The measurement point to alter.
   * @param t                 The caught throwable t.
   */
  protected void alterNamePostException(MeasurementPoint aMeasurementPoint, Throwable t) {
    aMeasurementPoint.alterName(aMeasurementPoint.getName() + " [" + calculateShortName(t.getClass()) + "]");
  }


  /**
   * Calculate short name for a given class.
   *
   * @param clazz The class object.
   * @return The short name for the given class.
   */
  protected String calculateShortName(Class clazz) {
    String name = clazz.getName();
    int beginIndex = name.lastIndexOf('.');
    if (beginIndex > 0) {
      return name.substring(beginIndex + 1);
    } else {
      return name;
    }
  }

}
