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

package etm.contrib.renderer.comparator;

import etm.core.aggregation.ExecutionAggregate;

import java.util.Comparator;

/**
 *
 * A comparator that may be used to sort ExecutionAggregates.
 *
 * @author void.fm
 * @version $Revision$
 */
public class ExecutionAggregateComparator implements Comparator {

  public static final int TYPE_NAME = 1;
  public static final int TYPE_EXCECUTIONS = 2;
  public static final int TYPE_AVERAGE = 3;
  public static final int TYPE_MIN = 4;
  public static final int TYPE_MAX = 5;
  public static final int TYPE_TOTAL = 6;

  private int type;
  private boolean descending;


  public ExecutionAggregateComparator() {
    this(TYPE_NAME, false);
  }

  public ExecutionAggregateComparator(int aType) {
    this(aType, true);
  }

  public ExecutionAggregateComparator(int aType, boolean descendingOrder) {
    type = aType;
    descending = descendingOrder;
  }

  public int compare(Object o1, Object o2) {
    ExecutionAggregate one = (ExecutionAggregate) o1;
    ExecutionAggregate two = (ExecutionAggregate) o2;
    switch (type) {
      case TYPE_NAME:
        return compareName(one, two);
      case TYPE_EXCECUTIONS:
        return compareExecutions(one, two);
      case TYPE_AVERAGE:
        return compareAverage(one, two);
      case TYPE_MIN:
        return compareMin(one, two);
      case TYPE_MAX:
        return compareMax(one, two);
      case TYPE_TOTAL:
        return compareTotal(one, two);
      default:
        throw new IllegalArgumentException("Unsupported type " + type);
    }
  }

  protected int compareName(ExecutionAggregate one, ExecutionAggregate two) {
    int value = one.getName().compareTo(two.getName());
    return descending ? value : -1 * value;
  }

  protected int compareExecutions(ExecutionAggregate one, ExecutionAggregate two) {
    if (one.getMeasurements() < two.getMeasurements()) {
      return descending ? 1 : -1;
    } else if (two.getMeasurements() < one.getMeasurements()) {
      return descending ? -1 : 1;
    } else {
      return 0;
    }
  }

  protected int compareAverage(ExecutionAggregate one, ExecutionAggregate two) {
    if (one.getAverage() < two.getAverage()) {
      return descending ? 1 : -1;
    } else if (two.getAverage() < one.getAverage()) {
      return descending ? -1 : 1;
    } else {
      return 0;
    }
  }

  protected int compareMin(ExecutionAggregate one, ExecutionAggregate two) {
    if (one.getMin() < two.getMin()) {
      return descending ? 1 : -1;
    } else if (two.getMin() < one.getMin()) {
      return descending ? -1 : 1;
    } else {
      return 0;
    }
  }

  protected int compareMax(ExecutionAggregate one, ExecutionAggregate two) {
    if (one.getMax() < two.getMax()) {
      return descending ? 1 : -1;
    } else if (two.getMax() < one.getMax()) {
      return descending ? -1 : 1;
    } else {
      return 0;
    }
  }

  protected int compareTotal(ExecutionAggregate one, ExecutionAggregate two) {
    if (one.getTotal() < two.getTotal()) {
      return descending ? 1 : -1;
    } else if (two.getTotal() < one.getTotal()) {
      return descending ? -1 : 1;
    } else {
      return 0;
    }
  }

  public int getType() {
    return type;
  }

  public boolean isDescending() {
    return descending;
  }
}
