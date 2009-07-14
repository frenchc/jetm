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

  private static final int DEBUG = 500;
  private static final int INFO = 400;
  private static final int WARN = 300;
  private static final int ERROR = 200;
  private static final int FATAL = 100;

  private static int logLevel = INFO;

  private String clazzName;


  static {
    String s = System.getProperty("jetm.log.level");
    if (s != null) {
      if ("debug".equalsIgnoreCase(s)) {
        logLevel = DEBUG;
      } else if ("info".equalsIgnoreCase(s)) {
        logLevel = INFO;
      } else if ("warn".equalsIgnoreCase(s)) {
        logLevel = WARN;
      } else if ("error".equalsIgnoreCase(s)) {
        logLevel = ERROR;
      } else if ("fatal".equalsIgnoreCase(s)) {
        logLevel = FATAL;
      } else {
        System.err.println("Unsupported log level " + logLevel);
      }
    }

  }

  public DefaultLogAdapter(Class aClazz) {
    int i = aClazz.getName().lastIndexOf('.');
    clazzName = "[" + aClazz.getName().substring(i + 1) + "] ";
  }

  public void debug(String message) {
    if (logLevel >= DEBUG) {
      System.out.println("[DEBUG] " + clazzName + message);
    }
  }

  public void info(String message) {
    if (logLevel >= INFO) {

      System.out.println("[INFO ] " + clazzName + message);
    }
  }

  public void warn(String message, Throwable t) {
    if (logLevel >= WARN) {

      System.err.println("[WARN ] " + clazzName + message + getThrowable(t));
    }
  }

  public void warn(String message) {
    if (logLevel >= WARN) {
      System.out.println("[WARN ] " + clazzName + message);
    }
  }

  public void error(String message, Throwable t) {
    if (logLevel >= ERROR) {
      System.err.println("[ERROR] " + clazzName + message + getThrowable(t));
    }
  }

  public void fatal(String message, Throwable t) {
    if (logLevel >= FATAL) {
      System.err.println("[FATAL] " + clazzName + message + getThrowable(t));
    }
  }

  private String getThrowable(Throwable t) {
    StringWriter writer = new StringWriter();
    t.printStackTrace(new PrintWriter(writer));

    return writer.toString();
  }
}
