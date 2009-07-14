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

package etm.core.jmx;

import etm.core.aggregation.Aggregate;
import etm.core.util.Log;
import etm.core.util.LogAdapter;

import javax.management.InstanceAlreadyExistsException;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.util.Hashtable;

/**
 * Helper class for Jmx related classes.
 *
 * @author void.fm
 * @version $Revision$
 * @since 1.2.2
 */

class JmxSupport {
  private static final LogAdapter LOG = Log.getLog(JmxSupport.class);

  protected void registerMBean(MBeanServer mbeanServer, ObjectName objectName, 
		  					   Object object, boolean overwrite) throws JMException {
    try {
      mbeanServer.registerMBean(object, objectName);
    } catch (InstanceAlreadyExistsException e) {
      if (overwrite) {
        mbeanServer.unregisterMBean(objectName);
        mbeanServer.registerMBean(object, objectName);
      } else {
        LOG.warn("Error registering EtmMonitor MBean. An instance called " + objectName + " already exists.");
      }
    }
  }


  protected ObjectName calculateObjectName(String measurementDomain, Aggregate aAggregate) throws JMException {
    char[] chars = aAggregate.getName().toCharArray();

    for (int i = 0; i < chars.length; i++) {
      switch (chars[i]) {
        case ':':
        case ',':
        case ';':
          chars[i] = '_';
        default:
          // don't do anything
      }
    }
    Hashtable map = new Hashtable();
    map.put("type", "Measurement");
    map.put("name", new String(chars));

    return new ObjectName(measurementDomain, map);
  }
}
