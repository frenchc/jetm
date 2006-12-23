/*
 *
 * Copyright (c) 2004, 2005, 2006 void.fm
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

package etm.core.configuration;

import etm.core.aggregation.Aggregator;

import java.util.Map;
import java.util.HashMap;

/**
 *
 * EtmAggregatorConfig represents an aggregator chain
 * configuration.
 *
 * @version $Id: EtmAggregatorConfig.java,v 1.3 2006/06/11 13:55:58 french_c Exp $
 * @author void.fm
 *
 */
public class EtmAggregatorConfig {
  private Class aggregatorClass;
  private Map properties;

  public Class getAggregatorClass() {
    return aggregatorClass;
  }

  public Map getProperties() {
    return properties;
  }

  public void setAggregatorClass(String aggregatorClassName) {
    Class clazz;
    try {
      clazz = Class.forName(aggregatorClassName);
    } catch (ClassNotFoundException e) {
      throw new EtmConfigurationException("Aggregator class " + aggregatorClassName + " not found.");
    }
    if (Aggregator.class.isAssignableFrom(clazz)) {
      aggregatorClass = clazz;
    } else {
      throw new EtmConfigurationException("Class " + aggregatorClassName + " is not a valid Aggregator implementation.");
    }
  }

  public void addProperty(String propertyName, String propertyValue) {
    if (properties == null) {
      properties = new HashMap();
    }
    properties.put(propertyName, propertyValue);
  }
}
