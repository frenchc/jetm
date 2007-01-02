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

package test.etm.contrib.renderer;

import etm.contrib.renderer.SimpleHtmlRenderer;
import junit.framework.TestCase;
import test.etm.core.TestExecutionAggregate;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Tests the html renderer output.
 *
 * @author void.fm
 * @version $Revision$
 */
public class SimpleHtmlRendererTest extends TestCase {


  public void testFlatRenderer() throws Exception {
    String expectedResult = "<table>\n" +
      " <tr>\n" +
      "  <th>Measurement Point</th>\n" +
      "  <th>#</th>\n" +
      "  <th>Average</th>\n" +
      "  <th>Min</th>\n" +
      "  <th>Max</th>\n" +
      "  <th>Total</th>\n" +
      " </tr>\n" +
      " <tr>\n" +
      "  <td><div class=\"parentname\" >test1</div></td>\n" +
      "  <td><div class=\"parentmeasurement\" >10</div></td>\n" +
      "  <td><div class=\"parenttime\" >10.000</div></td>\n" +
      "  <td><div class=\"parenttime\" >10.000</div></td>\n" +
      "  <td><div class=\"parenttime\" >20.000</div></td>\n" +
      "  <td><div class=\"parenttotal\" >100.000</div></td>\n" +
      " </tr>\n" +
      " <tr>\n" +
      "  <td><div class=\"parentname\" >test2</div></td>\n" +
      "  <td><div class=\"parentmeasurement\" >20</div></td>\n" +
      "  <td><div class=\"parenttime\" >10.000</div></td>\n" +
      "  <td><div class=\"parenttime\" >50.000</div></td>\n" +
      "  <td><div class=\"parenttime\" >70.000</div></td>\n" +
      "  <td><div class=\"parenttotal\" >200.000</div></td>\n" +
      " </tr>\n" +
      " <tr><td class=\"footer\" colspan=\"6\">All times in miliseconds. Measurements provided by <a href=\"http://jetm.void.fm\" target=\"_default\">JETM</a></td></tr>\n" +
      "</table>";


    Map map = new HashMap();


    TestExecutionAggregate agt1 = new TestExecutionAggregate("test1");
    agt1.setMeasurements(10);
    agt1.setMin(10);
    agt1.setMax(20);
    agt1.setTotal(100);

    TestExecutionAggregate agt2 = new TestExecutionAggregate("test2");
    agt2.setMeasurements(20);
    agt2.setMin(50);
    agt2.setMax(70);
    agt2.setTotal(200);

    map.put(agt1.getName(), agt1);
    map.put(agt2.getName(), agt2);

    StringWriter writer = new StringWriter();

    SimpleHtmlRenderer renderer = new SimpleHtmlRenderer(writer, Locale.US);
    renderer.render(map);

    //new SimpleHtmlRenderer(new OutputStreamWriter(System.out), Locale.US).render(map);

    assertEquals(expectedResult, writer.toString());
  }

  public void testNestedRenderer() throws Exception {
    String expectedResult = "<table>\n" +
      " <tr>\n" +
      "  <th>Measurement Point</th>\n" +
      "  <th>#</th>\n" +
      "  <th>Average</th>\n" +
      "  <th>Min</th>\n" +
      "  <th>Max</th>\n" +
      "  <th>Total</th>\n" +
      " </tr>\n" +
      " <tr>\n" +
      "  <td><div class=\"parentname\" >test1<div class=\"childname\" >test1Child1<div class=\"childname\" >test1Child1Child2</div><div class=\"childname\" >test1Child1Child1</div></div></div></td>\n" +
      "  <td><div class=\"parentmeasurement\" >10<div class=\"childmeasurement\" >20<div class=\"childmeasurement\" >20</div><div class=\"childmeasurement\" >20</div></div></div></td>\n" +
      "  <td><div class=\"parenttime\" >10.000<div class=\"childtime\" >10.000<div class=\"childtime\" >10.000</div><div class=\"childtime\" >10.000</div></div></div></td>\n" +
      "  <td><div class=\"parenttime\" >10.000<div class=\"childtime\" >50.000<div class=\"childtime\" >50.000</div><div class=\"childtime\" >50.000</div></div></div></td>\n" +
      "  <td><div class=\"parenttime\" >20.000<div class=\"childtime\" >70.000<div class=\"childtime\" >70.000</div><div class=\"childtime\" >70.000</div></div></div></td>\n" +
      "  <td><div class=\"parenttotal\" >100.000<div class=\"childtotal\" >200.000<div class=\"childtotal\" >200.000</div><div class=\"childtotal\" >200.000</div></div></div></td>\n" +
      " </tr>\n" +
      " <tr>\n" +
      "  <td><div class=\"parentname\" >test2<div class=\"childname\" >test2Child1</div></div></td>\n" +
      "  <td><div class=\"parentmeasurement\" >20<div class=\"childmeasurement\" >1</div></div></td>\n" +
      "  <td><div class=\"parenttime\" >10.000<div class=\"childtime\" >3.000</div></div></td>\n" +
      "  <td><div class=\"parenttime\" >50.000<div class=\"childtime\" >1.000</div></div></td>\n" +
      "  <td><div class=\"parenttime\" >70.000<div class=\"childtime\" >2.000</div></div></td>\n" +
      "  <td><div class=\"parenttotal\" >200.000<div class=\"childtotal\" >3.000</div></div></td>\n" +
      " </tr>\n" +
      " <tr><td class=\"footer\" colspan=\"6\">All times in miliseconds. Measurements provided by <a href=\"http://jetm.void.fm\" target=\"_default\">JETM</a></td></tr>\n" +
      "</table>";

    Map map = new HashMap();


    TestExecutionAggregate agt1 = new TestExecutionAggregate("test1");
    Map agt1ChildMap = new HashMap();
    agt1.setChilds(agt1ChildMap);

    agt1.setMeasurements(10);
    agt1.setMin(10);
    agt1.setMax(20);
    agt1.setTotal(100);


    TestExecutionAggregate agt2 = new TestExecutionAggregate("test2");
    agt2.setMeasurements(20);
    agt2.setMin(50);
    agt2.setMax(70);
    agt2.setTotal(200);

    Map agt2ChildMap = new HashMap();
    agt2.setChilds(agt2ChildMap);

    map.put(agt1.getName(), agt1);
    map.put(agt2.getName(), agt2);


    TestExecutionAggregate agt1Child1 = new TestExecutionAggregate("test1Child1");
    Map agt1Child1ChildMap = new HashMap();
    agt1Child1.setChilds(agt1Child1ChildMap);


    agt1Child1.setMeasurements(20);
    agt1Child1.setMin(50);
    agt1Child1.setMax(70);
    agt1Child1.setTotal(200);
    agt1ChildMap.put(agt1Child1.getName(), agt1Child1);


    TestExecutionAggregate agt1Child1Child1 = new TestExecutionAggregate("test1Child1Child1");
    agt1Child1Child1.setMeasurements(20);
    agt1Child1Child1.setMin(50);
    agt1Child1Child1.setMax(70);
    agt1Child1Child1.setTotal(200);
    agt1Child1ChildMap.put(agt1Child1Child1.getName(), agt1Child1Child1);

    TestExecutionAggregate agt1Child1Child2 = new TestExecutionAggregate("test1Child1Child2");
    agt1Child1Child2.setMeasurements(20);
    agt1Child1Child2.setMin(50);
    agt1Child1Child2.setMax(70);
    agt1Child1Child2.setTotal(200);
    agt1Child1ChildMap.put(agt1Child1Child2.getName(), agt1Child1Child2);


    TestExecutionAggregate agt2Child1 = new TestExecutionAggregate("test2Child1");
    agt2Child1.setMeasurements(1);
    agt2Child1.setMin(1);
    agt2Child1.setMax(2);
    agt2Child1.setTotal(3);
    agt2ChildMap.put(agt2Child1.getName(), agt2Child1);


    StringWriter writer = new StringWriter();
    SimpleHtmlRenderer renderer = new SimpleHtmlRenderer(writer, Locale.US);
    renderer.render(map);


    assertEquals(expectedResult, writer.toString());

    //new SimpleHtmlRenderer(new OutputStreamWriter(System.out), Locale.US).render(map);

  }


}
