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

package etm.contrib.console;

import java.io.IOException;
import java.io.InputStream;

/**
 * In memory "cache" for drop-in html console resources.
 *
 * @author void.fm
 * @version $Revision$
 */
public class ResourceAccessor {
  private byte[] favicon;
  private byte[] css;
  private byte[] robots;

  public ResourceAccessor() {
    favicon = loadResource("etm/contrib/console/favicon.ico");
    css = loadResource("etm/contrib/console/style.css");
    robots = loadResource("etm/contrib/console/robots.txt");
  }

  private byte[] loadResource(String resourcePath) {
    byte[] buffer = new byte[8192];
    int offset = 0;
    InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
    if (in != null) {
      try {
        int r;
        while ((r = in.read(buffer, offset, buffer.length - offset)) > -1) {
          offset += r;
          if (buffer.length - r < 100) {
            byte[] temp = new byte[buffer.length + 8192];
            System.arraycopy(buffer, 0, temp, 0, offset);
            buffer = temp;
          }
        }

      } catch (IOException e) {
        throw new ConsoleException(e);
      } finally {
        try {
          in.close();
        } catch (IOException e) {
          // ignored
        }
      }

    }
    byte[] content = new byte[offset];
    System.arraycopy(buffer, 0, content, 0, offset);
    return content;
  }

  public byte[] getFavicon() {
    return favicon;
  }

  public byte[] getStyleSheet() {
    return css;
  }

  public byte[] getRobotsTxt() {
    return robots;
  }
}
