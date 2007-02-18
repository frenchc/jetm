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
package etm.core.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Default log implementation.
 *
 * @author void.fm
 * @version $Revision$
 * @since 1.2.0
 */
class DefaultLogAdapter implements LogAdapter {

  private String clazzName;

  public DefaultLogAdapter(Class aClazz) {
    int i = aClazz.getName().lastIndexOf('.');
    clazzName = "[" + aClazz.getName().substring(i+1) + "] ";
  }

  public void debug(String message) {
    System.out.println(clazzName + message);
  }

  public void info(String message) {
    System.out.println(clazzName + message);
  }

  public void warn(String message, Throwable t) {
    System.err.println(clazzName + message + getThrowable(t));
  }

  public void warn(String message) {
    System.out.println(clazzName + message);
  }

  public void error(String message, Throwable t) {
    System.err.println(clazzName + message + getThrowable(t));
  }

  public void fatal(String message, Throwable t) {
    System.err.println(clazzName + message + getThrowable(t));
  }

  private String getThrowable(Throwable t) {
    StringWriter writer = new StringWriter();
    t.printStackTrace(new PrintWriter(writer));

    return writer.toString();
  }
}
