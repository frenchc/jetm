package test.etm.contrib.rrd.rrd4j;

import etm.contrib.rrd.rrd4j.Rrd4jPlugin;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.NestedMonitor;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: jens
 * Date: Feb 17, 2007
 * Time: 9:58:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class Rrd4jPluginTest extends TestCase {

  public void testMissingAggregator() {
    EtmMonitor monitor = new NestedMonitor();

    Rrd4jPlugin plugin = new Rrd4jPlugin();
    monitor.addPlugin(plugin);

    try {
      monitor.start();
    } finally {
      monitor.stop();
    }
  }


//  public void testRrdDbCreate() {
//    EtmMonitor monitor = new NestedMonitor(new NotifyingAggregator(new RootAggregator()));
//
//    Rrd4jPlugin plugin = new Rrd4jPlugin();
//    List configurations = new ArrayList();
//    configurations.add("test.rrd|*");
//    plugin.setDestinations(configurations);
//    monitor.addPlugin(plugin);
//
//
//    try {
//      monitor.start();
//    } finally {
//      monitor.stop();
//    }
//  }

}
