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

package etm.core.jmx;

import etm.core.aggregation.Aggregate;
import etm.core.monitor.EtmException;
import etm.core.monitor.EtmMonitor;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.ReflectionException;
import javax.management.openmbean.OpenMBeanAttributeInfo;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import javax.management.openmbean.OpenMBeanConstructorInfo;
import javax.management.openmbean.OpenMBeanInfoSupport;
import javax.management.openmbean.OpenMBeanOperationInfo;
import javax.management.openmbean.OpenMBeanOperationInfoSupport;
import javax.management.openmbean.SimpleType;

/**
 * MBean for exporting an EtmPoint to JMX.
 *
 * @author void.fm
 * @version $Revision$
 * @since 1.2.0
 */
public class EtmPointMBean implements DynamicMBean {

  private EtmMonitor etmMonitor;
  private Aggregate aggregate;

  public EtmPointMBean(EtmMonitor aEtmMonitor, Aggregate aAggregate) {
    etmMonitor = aEtmMonitor;
    aggregate = aAggregate;
  }

  public Object getAttribute(String string) throws AttributeNotFoundException, MBeanException, ReflectionException {
    if ("measurements".equals(string)) {
      return aggregate.getMeasurements();
    } else if ("total".equals(string)) {
      return aggregate.getTotal();
    } else if ("min".equals(string)) {
      return aggregate.getMin();
    } else if ("max".equals(string)) {
      return aggregate.getMax();
    } else if ("average".equals(string)) {
      return aggregate.getAverage();
    } else {
      throw new AttributeNotFoundException(string);
    }
  }

  public void setAttribute(Attribute aAttribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
    throw new AttributeNotFoundException(aAttribute.getName());
  }

  public AttributeList getAttributes(String[] strings) {
    AttributeList list = new AttributeList();
    for (int i = 0; i < strings.length; i++) {
      try {
        list.add(i, new Attribute(strings[i], getAttribute(strings[i])));
      } catch (Exception e) {
        list.add(i, new Attribute(strings[i], null));
      }
    }
    return list;
  }

  public AttributeList setAttributes(AttributeList aAttributeList) {
    return new AttributeList();
  }

  public Object invoke(String string, Object[] aObjects, String[] aStrings) throws MBeanException, ReflectionException {
    if ("reset".equals(string)) {
      etmMonitor.reset(aggregate.getName());
    }

    return null;
  }

  public MBeanInfo getMBeanInfo() {
    return new OpenMBeanInfoSupport(
      Aggregate.class.getName(),
      "Performance results for " + aggregate.getName(),
      getAttributeInfos(),
      new OpenMBeanConstructorInfo[]{},
      getOperations(),
      new MBeanNotificationInfo[]{}
    );
  }

  private OpenMBeanOperationInfo[] getOperations() {
    try {
      return new OpenMBeanOperationInfo[]{
        new OpenMBeanOperationInfoSupport("reset", "Resets etm point.", null, SimpleType.VOID, MBeanOperationInfo.ACTION)
      };
    } catch (Exception e) {
      // this should be save
      throw new EtmException(e.getMessage());
    }
  }

  private OpenMBeanAttributeInfo[] getAttributeInfos() {
    try {
      return new OpenMBeanAttributeInfo[]{
        new OpenMBeanAttributeInfoSupport("measurements", "The number of measurements.", SimpleType.LONG, true, false, false),
        new OpenMBeanAttributeInfoSupport("average", "The average time in miliseconds.", SimpleType.DOUBLE, true, false, false),
        new OpenMBeanAttributeInfoSupport("min", "The minimum time in miliseconds..", SimpleType.DOUBLE, true, false, false),
        new OpenMBeanAttributeInfoSupport("max", "The maximum time in miliseconds.", SimpleType.DOUBLE, true, false, false),
        new OpenMBeanAttributeInfoSupport("total", "The total time in miliseconds.", SimpleType.DOUBLE, true, false, false)
      };


    } catch (Exception e) {
      // this should be save
      throw new EtmException(e.getMessage());
    }
  }
}
