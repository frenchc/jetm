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


package etm.core.renderer;

import junit.framework.TestCase;
import etm.core.TestExecutionAggregate;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * SimpleTextRenderer
 *
 * @author void.fm
 * @version $Revision$
 */

public class SimpleTextRendererTest extends TestCase {


  public void testFlatRenderer() throws Exception {
    String expectedResult =
      "|-------------------|----|---------|--------|--------|---------|" + System.getProperty("line.separator") +
        "| Measurement Point |  # | Average |   Min  |   Max  |  Total  |" + System.getProperty("line.separator") +
        "|-------------------|----|---------|--------|--------|---------|" + System.getProperty("line.separator") +
        "| test1             | 10 |  10,000 | 10,000 | 20,000 | 100,000 |" + System.getProperty("line.separator") +
        "|-------------------|----|---------|--------|--------|---------|" + System.getProperty("line.separator") +
        "| test2             | 20 |  10,000 | 50,000 | 70,000 | 200,000 |" + System.getProperty("line.separator") +
        "|-------------------|----|---------|--------|--------|---------|" + System.getProperty("line.separator");


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

    SimpleTextRenderer renderer = new SimpleTextRenderer(writer, Locale.GERMAN);
    renderer.render(map);

    assertEquals(expectedResult, writer.toString());
  }

  public void testNestedRenderer() throws Exception {
    String expectedResult =
      "|-------------------|----|---------|--------|--------|---------|" + System.getProperty("line.separator") +
        "| Measurement Point |  # | Average |   Min  |   Max  |  Total  |" + System.getProperty("line.separator") +
        "|-------------------|----|---------|--------|--------|---------|" + System.getProperty("line.separator") +
        "| test1             | 10 |  10,000 | 10,000 | 20,000 | 100,000 |" + System.getProperty("line.separator") +
        "|   testChild1      | 20 |  10,000 | 50,000 | 70,000 | 200,000 |" + System.getProperty("line.separator") +
        "|     testChild2    |  1 |   3,000 |  1,000 |  2,000 |   3,000 |" + System.getProperty("line.separator") +
        "|-------------------|----|---------|--------|--------|---------|" + System.getProperty("line.separator") +
        "| test2             | 20 |  10,000 | 50,000 | 70,000 | 200,000 |" + System.getProperty("line.separator") +
        "|-------------------|----|---------|--------|--------|---------|" + System.getProperty("line.separator");

    Map map = new HashMap();


    TestExecutionAggregate agt1 = new TestExecutionAggregate("test1");
    Map agt1Childs = new HashMap();
    agt1.setChilds(agt1Childs);
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


    TestExecutionAggregate agtChild1 = new TestExecutionAggregate("testChild1");
    Map agt2Childs = new HashMap();
    agtChild1.setChilds(agt2Childs);
    agtChild1.setMeasurements(20);
    agtChild1.setMin(50);
    agtChild1.setMax(70);
    agtChild1.setTotal(200);
    agt1Childs.put(agtChild1.getName(), agtChild1);


    TestExecutionAggregate agtChild2 = new TestExecutionAggregate("testChild2");
    agtChild2.setMeasurements(1);
    agtChild2.setMin(1);
    agtChild2.setMax(2);
    agtChild2.setTotal(3);
    agt2Childs.put(agtChild2.getName(), agtChild2);


    StringWriter writer = new StringWriter();

    SimpleTextRenderer renderer = new SimpleTextRenderer(writer, Locale.GERMAN);
    renderer.render(map);

    assertEquals(expectedResult, writer.toString());
  }

}
