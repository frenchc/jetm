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

package etm.contrib.aop.joinpoint;

import etm.core.monitor.EtmPoint;


/**
 * An etm joinpoint.
 * 
 * @author jenglisch
 * @version $Revision$ $Date$
 * @since 1.2.4 
 *
 */
public interface EtmJoinPoint {

  /**
   * Proceed with the wrapped method invocation.
   * 
   * @return The return value of the invoked method.
   * @throws Throwable Any exception that may occur during the execution of the invoked method. 
   */
  public Object proceed() throws Throwable;
  
  /**
   * Calculate EtmPoint name based on the method invocation.
   *
   * @return The name of the EtmPoint.
   */
  public String calculateName();


  /**
   * Alter name in case an exception is caught during processing. Altering the
   * name takes place after executing target method. Ensure that you never cause
   * an exception within this code.
   *
   * @param aEtmPoint The EtmPoint to alter.
   * @param t The caught throwable t.
   * 
   */
  public void alterNamePostException(EtmPoint aEtmPoint, Throwable t);
  
}
