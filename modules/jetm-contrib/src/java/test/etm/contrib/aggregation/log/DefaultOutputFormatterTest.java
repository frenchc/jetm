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
package test.etm.contrib.aggregation.log;

import etm.contrib.aggregation.log.DefaultOutputFormatter;
import etm.core.monitor.MeasurementPoint;
import junit.framework.TestCase;
import test.etm.core.TestPointGenerator;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Minor runtime tests for output formatter.
 *
 * @author void.fm
 * @version $Revision$
 */
public class DefaultOutputFormatterTest extends TestCase {


  public void testOutput() {
    MeasurementPoint measurementPoint = new TestPointGenerator().getMeasurementPoint();
    DefaultOutputFormatter formatter = new DefaultOutputFormatter();

    // validate MS format
    NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
    numberFormat.setMinimumFractionDigits(3);
    numberFormat.setMaximumFractionDigits(3);
    numberFormat.setGroupingUsed(false);

    String s = formatter.format(measurementPoint);
    assertTrue(s.indexOf(measurementPoint.getName()) > 0);
    if (measurementPoint.getParent() != null) {
      assertTrue(s.indexOf("parent=<" + measurementPoint.getParent().getName() + ">") > 0);
    }
    assertTrue(s.indexOf(String.valueOf(measurementPoint.getStartTimeMillis())) > 0);
    assertTrue(s.indexOf(numberFormat.format(measurementPoint.getTransactionTime())) > 0);

  }

}
