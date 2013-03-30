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

package etm.contrib.aggregation.log;

import etm.core.monitor.EtmPoint;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Default log output formatter. It writes log messages in the format
 * <pre>
 *  etmPoint=&lt;$1&gt;, parent=&lt;$2&gt;, transactionTime=&lt;$3&gt;, recordingTime=&lt;$4&gt;
 * </pre>
 * where
 * <pre>
 *  $1 is the name of the EtmPoint,
 *  $2 the of the parent if any (is empty otherwhise),
 *  $3 the transactionTime in ms
 *  $4 the time of the measurement taken using {@link System#currentTimeMillis()}
 * </pre>
 * <p/>
 * The transaction time is written using a NumberFormat with min/max friction digits of 3.
 *
 * @author void.fm
 * @version $Revision$
 */
public class DefaultOutputFormatter implements LogOutputFormatter {

  private final NumberFormat numberFormat;


  /**
   * A DefaultOutputFormatter using default locale.
   */
  public DefaultOutputFormatter() {
    this(Locale.getDefault());
  }

  /**
   * A DefaultOutputFormatter using the given locale.
   *
   * @param locale The locale to use for transactiontime format.
   */
  public DefaultOutputFormatter(Locale locale) {
    numberFormat = NumberFormat.getInstance(locale);
    numberFormat.setMinimumFractionDigits(3);
    numberFormat.setMaximumFractionDigits(3);
    numberFormat.setGroupingUsed(false);
  }

  public String format(EtmPoint aEtmPoint) {
    String parentName = aEtmPoint.getParent() == null ? "" : calculateParentHierarchie(aEtmPoint);

    return new StringBuffer().
      append("measurementPoint=<").
      append(aEtmPoint.getName()).
      append(">, parent=<").
      append(parentName).
      append(">, transactionTime=<").
      append(numberFormat.format(aEtmPoint.getTransactionTime())).
      append(">, recordingTime=<").
      append(aEtmPoint.getStartTimeMillis()).
      append(">").toString();
  }

  protected String calculateParentHierarchie(EtmPoint aEtmPoint) {
    // todo remove string concat
    String hierarchie = "";
    EtmPoint parent = aEtmPoint.getParent();

    while (parent != null) {
      hierarchie = ";" + parent.getName() + hierarchie;
      parent = parent.getParent();
    }

    return hierarchie.substring(1);
  }
}
