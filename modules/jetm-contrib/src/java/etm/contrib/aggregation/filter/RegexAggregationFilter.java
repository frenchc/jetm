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

package etm.contrib.aggregation.filter;

import etm.core.aggregation.AggregationFilter;
import etm.core.monitor.MeasurementPoint;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * The RegexAggregationFilter filters measurement point names based
 * on a list of JDK 1.4 regex.
 *
 * @version $Revision$
 * @author void.fm
 * @since 1.2.0
 * @see java.util.regex.Pattern
 */
public class RegexAggregationFilter implements AggregationFilter {
  protected final HashSet validNames;
  protected Pattern[] pattern;

  /**
   *
   * Create a RegexAggregationFilter instance based on a list
   * of regex pattern separated by semicolon.
   *
   * @param listOfPattern Java 5 Regex separated by semicolon.
   */
  public RegexAggregationFilter(String listOfPattern) {
    this(listOfPattern.split(";"));
  }


  /**
   *
   * Create a RegexAggregationFilter
   *
   * @param regexPattern The Java 5 regex patterm/
   */
  public RegexAggregationFilter(String[] regexPattern) {
    this.pattern = new Pattern[regexPattern.length];

    for (int i = 0; i < regexPattern.length; i++) {
      String string = regexPattern[i].trim();
      if (string.length() > 0) {
        this.pattern[i] = Pattern.compile(string);
      }
    }

    validNames = new HashSet();
  }

  public boolean matches(MeasurementPoint aPoint) {
    String name = aPoint.getName();
    if (validNames.contains(name)) {
      return true;
    }

    for (int i = 0; i < pattern.length; i++) {
      Matcher matcher = pattern[i].matcher(name);
      if (matcher.matches()) {
        synchronized (validNames) {
          validNames.add(name);
        }
        return true;
      }
    }
    return false;
  }
}
