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

package etm.core.aggregation;

import etm.core.monitor.MeasurementPoint;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * The ExecutionAggregate represents the aggregated information of
 * an execution point. Please note that all methods of this class
 * are not synchronized.
 *
 * @author void.fm
 * @version $Revision$
 */
public class ExecutionAggregate implements Externalizable {

  private static final long serialVersionUID = 1L;

  private String name;
  private long measurements = 0;

  private double min = 0.0;
  private double max = 0.0;
  private double total = 0.0;

  // we use late init
  private Map childs;


  public ExecutionAggregate() {
  }

  public ExecutionAggregate(String aName) {
    name = aName;
  }

  public String getName() {
    return name;
  }

  public double getAverage() {
    return total / (double) measurements;
  }

  public double getMin() {
    return min;
  }

  public double getMax() {
    return max;
  }

  public long getMeasurements() {
    return measurements;
  }

  public double getTotal() {
    return total;
  }

  public Map getChilds() {
    return childs;
  }

  /**
   * Returns whether the given measurement point has childs
   * or not.
   *
   * @return True for available childs, otherwise false.
   */
  public boolean hasChilds() {
    return childs != null && childs.size() > 0;
  }

  /**
   * Adds a transaction to the current measurement point.
   *
   * @param transaction The transaction to add.
   */

  public void addTransaction(MeasurementPoint transaction) {
    double miliseconds = transaction.getTransactionTime();

    measurements++;
    total += miliseconds;
    max = max > miliseconds ? max : miliseconds;
    min = min > miliseconds || min == 0 ? miliseconds : min;
  }


  /**
   * Append a measurement result from the end of a tree.
   *
   * @param newTree The tree to the measurement result.
   */

  public void appendPath(LinkedList newTree) {
    MeasurementPoint current = (MeasurementPoint) newTree.removeFirst();

    ExecutionAggregate aggregate = getChild(current.getName());

    if (newTree.size() == 0) {
      aggregate.addTransaction(current);
    } else {
      aggregate.appendPath(newTree);
    }
  }

  protected void setMeasurements(long aMeasurements) {
    measurements = aMeasurements;
  }

  protected void setMin(double aMin) {
    min = aMin;
  }

  protected void setMax(double aMax) {
    max = aMax;
  }

  protected void setTotal(double aTotal) {
    total = aTotal;
  }

  protected void setChilds(Map aChilds) {
    childs = aChilds;
  }

  public String toString() {
    return name + " -  " + measurements + " calls -  " + total + "ms total, " + min + "ms min, " + max + "ms max";
  }

  private ExecutionAggregate getChild(String aName) {
    if (childs == null) {
      childs = new HashMap();
    }

    ExecutionAggregate aggregate = (ExecutionAggregate) childs.get(aName);

    if (aggregate == null) {
      aggregate = new ExecutionAggregate(aName);
      childs.put(aName, aggregate);
    }

    return aggregate;
  }

  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeObject(name);
    out.writeLong(measurements);
    out.writeDouble(min);
    out.writeDouble(max);
    out.writeDouble(total);
    out.writeObject(childs);
  }

  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    name = (String) in.readObject();
    measurements = in.readLong();
    min = in.readDouble();
    max = in.readDouble();
    total = in.readDouble();
    childs = (Map) in.readObject();
  }

  public void reset() {
    measurements = 0;

    min = 0.0;
    max = 0.0;
    total = 0.0;
    childs = null;
  }
}
