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
package etm.core.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Adapter to java util logging.
 *
 * @author void.fm
 * @version $Revision$
 * @since 1.2.0
 */
public class Java14LogAdapter extends Logger implements LogAdapter {

  public Java14LogAdapter(Class aClazz) {
    super(aClazz.getName(), null);
    LogManager.getLogManager().addLogger(this);    
  }

  public void debug(String message) {
    fine(message);
  }

  public void warn(String message) {
    warning(message);
  }

  public void warn(String message, Throwable t) {
    log(Level.WARNING, message, t);
  }

  public void error(String message, Throwable t) {
    log(Level.SEVERE, message, t);
  }

  public void fatal(String message, Throwable t) {
    log(Level.SEVERE, message, t);
  }

  public static boolean isConfigured() {
    List expectedNames = new ArrayList();

    Enumeration names = LogManager.getLogManager().getLoggerNames();

    while(names.hasMoreElements()) {
      expectedNames.add(names.nextElement());
      if (expectedNames.size() == 0) {
        return true;
      }
    }

    expectedNames.remove("");
    expectedNames.remove("global");

    return expectedNames.size() > 0;
  }
}
