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
  private static final LogAdapter log = Log.getLog(JmxSupport.class);

  protected void registerMBean(MBeanServer mbeanServer, ObjectName objectName, Object object, boolean overwrite) throws JMException {
    try {
      mbeanServer.registerMBean(object, objectName);
    } catch (InstanceAlreadyExistsException e) {
      if (overwrite) {
        mbeanServer.unregisterMBean(objectName);
        mbeanServer.registerMBean(object, objectName);
      } else {
        log.warn("Error registering EtmMonitor MBean. An instance called " + objectName + " already exists.");
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
          break;
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
