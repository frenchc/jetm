package test.etm.contrib.aggregation.persistence.mockup;

import etm.contrib.aggregation.persistence.PersistenceBackend;
import etm.contrib.aggregation.persistence.PersistentEtmState;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jens
 * Date: Jan 16, 2007
 * Time: 1:57:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestPersistenceBackend implements PersistenceBackend {

  private boolean booleanTrue;
  private boolean booleanFalse;
  private long longValue;
  private int intValue;
  private String stringValue;
  private Class clazzValue;
  private List listValue;
  private Map mapValue;

  public PersistentEtmState load() {
    return null;
  }

  public void store(PersistentEtmState state) {
  }


  public boolean isBooleanTrue() {
    return booleanTrue;
  }

  public void setBooleanTrue(boolean aBooleanTrue) {
    booleanTrue = aBooleanTrue;
  }

  public boolean isBooleanFalse() {
    return booleanFalse;
  }

  public void setBooleanFalse(boolean aBooleanFalse) {
    booleanFalse = aBooleanFalse;
  }

  public long getLongValue() {
    return longValue;
  }

  public void setLongValue(long aLongValue) {
    longValue = aLongValue;
  }

  public int getIntValue() {
    return intValue;
  }

  public void setIntValue(int aIntValue) {
    intValue = aIntValue;
  }

  public String getStringValue() {
    return stringValue;
  }

  public void setStringValue(String aStringValue) {
    stringValue = aStringValue;
  }

  public Class getClazzValue() {
    return clazzValue;
  }

  public void setClazzValue(Class aClazzValue) {
    clazzValue = aClazzValue;
  }

  public List getListValue() {
    return listValue;
  }

  public void setListValue(List aListValue) {
    listValue = aListValue;
  }

  public Map getMapValue() {
    return mapValue;
  }

  public void setMapValue(Map aMapValue) {
    mapValue = aMapValue;
  }
}
