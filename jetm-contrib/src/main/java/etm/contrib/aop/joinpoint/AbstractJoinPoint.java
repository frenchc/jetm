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

import etm.core.monitor.EtmPoint;

/**
 * Abstract etm joinpoint provides basic functionality
 * for calculating #EtmPoint names.
 *
 * @author jenglisch
 * @version $Revision$ $Date$
 * @since 1.2.4
 *
 */
public abstract class AbstractJoinPoint implements EtmJoinPoint {

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

  /**
   * Calculate name based on the method invocation.
   *
   * @param clazz A class.
   * @param methodName A methodName.
   * @return The name for the given method invocation.
   */
  protected String calculateName(Class clazz, String methodName) {
    return calculateShortName(clazz) + "::" + methodName;
  }

  /**
   * @see #alterNamePostException(EtmPoint, Throwable)
   */
   public void alterNamePostException(EtmPoint aEtmPoint, Throwable t) {
     aEtmPoint.alterName(aEtmPoint.getName() + " [" + calculateShortName(t.getClass()) + "]");
   }

}
