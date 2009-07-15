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

package etm.core.metadata;

import java.util.Map;

/**
 * Provide information about configured plugins.
 *
 * @author void.fm
 * @version $Revision$
 * @since 1.2.0
 */
public class PluginMetaData {

  private Class implementationClass;
  private String description;
  private Map properties;


  public PluginMetaData(Class aImplementationClass, String aDescription) {
    this(aImplementationClass, aDescription, null);
  }

  public PluginMetaData(Class aImplementationClass, String aDescription, Map aProperties) {
    implementationClass = aImplementationClass;
    description = aDescription;
    properties = aProperties;
  }


  public Class getImplementationClass() {
    return implementationClass;
  }

  public String getDescription() {
    return description;
  }

  /**
   * Returns a map of properties, may be null. See specific EtmPlugin class for
   * names of the properties. Both keys and values are always strings.
   *
   * @return A map of properties, may be null.
   */
  public Map getProperties() {
    return properties;
  }

  public String toString() {
     return "implementationClass=" + implementationClass + ", description='" + description + "'";
  }
}
