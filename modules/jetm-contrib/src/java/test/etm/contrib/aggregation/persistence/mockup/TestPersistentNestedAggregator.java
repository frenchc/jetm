package test.etm.contrib.aggregation.persistence.mockup;

import etm.contrib.aggregation.persistence.PersistenceBackend;
import etm.contrib.aggregation.persistence.PersistentNestedAggregator;

/**
 * Created by IntelliJ IDEA.
 * User: jens
 * Date: Jan 16, 2007
 * Time: 2:03:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestPersistentNestedAggregator extends PersistentNestedAggregator {
  
  public PersistenceBackend getPersistenceBackend() {
    return persistenceBackend;
  }
}
