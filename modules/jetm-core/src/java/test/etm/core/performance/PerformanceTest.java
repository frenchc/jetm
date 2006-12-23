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


package test.etm.core.performance;

import etm.core.monitor.EtmMonitor;
import etm.core.monitor.FlatMonitor;
import etm.core.monitor.MeasurementPoint;
import etm.core.monitor.NestedMonitor;
import etm.core.timer.DefaultTimer;
import junit.framework.TestCase;

import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Collect a few metrics for future references.
 *
 * @author void.fm
 * @version $Id: PerformanceTest.java,v 1.5 2006/05/23 10:00:45 french_c Exp $
 */
public class PerformanceTest extends TestCase {

  private static final int THREAD_SIZE = 100;
  private static final int THREADED_RUNS = 1000;
  private static final int SINGLE_RUNS = 1000000;

  private static final int WARMUP_RUNS = 1000;


  protected EtmMonitor monitor;

  private Object lock = new Object();
  private int running;

//  public void testNestedThreaded() throws Exception {
//    monitor = new NestedMonitor(new DefaultTimer());
//    long time;
//    long elapsed;
//
//    //warm up, no thread
//    new Runner("warmup", WARMUP_RUNS).run();
//    monitor.reset();
//
//
//    // measurement run
//    Runner[] runners = new Runner[THREAD_SIZE];
//    for (int i = 0; i < THREAD_SIZE; i++) {
//      runners[i] = new Runner("test", THREADED_RUNS);
//    }
//
//
//    time = System.currentTimeMillis();
//    try {
//
//      for (int i = 0; i < runners.length; i++) {
//        runners[i].start();
//      }
//
//      do {
//        synchronized (lock) {
//          lock.wait();
//        }
//      } while (running > 0);
//    } finally {
//      elapsed = System.currentTimeMillis() - time;
//    }
//
//    write("NestedThreaded", THREAD_SIZE * THREADED_RUNS, elapsed);
//
//  }

  private void write(String s, int i, long aElapsed) throws Exception {
    OutputStream out = new FileOutputStream("testoutput", true);
    out.write(("TestMode: " + s + ", Measurements: " + (3 * i) + ", Elapsed: " + aElapsed + ", Average: " + ((double) aElapsed) / ((double) 3 * i)).getBytes());

    out.write(System.getProperty("line.separator").getBytes());
    out.close();
  }

//  public void testFlatThreaded() {
//    monitor = new NestedMonitor(new DefaultTimer());
//
//    //warm up
//    new Runner("warmup", WARMUP_RUNS).start();
//    monitor.reset();
//
//
//    // measurement run
//    Runner[] runners = new Runner[THREAD_SIZE];
//    for (int i = 0; i < THREAD_SIZE; i++) {
//
//      runners[i] = new Runner("test", THREADED_RUNS);
//    }
//
//    for (int i = 0; i < runners.length; i++) {
//      runners[i].start();
//    }
//  }
//

  public void testNested() throws Exception {
    long time;
    long elapsed;

    monitor = new NestedMonitor(new DefaultTimer());

    //warm up
    new Runner("warmup", WARMUP_RUNS).run();
    monitor.reset();

    // measurement run
    time = System.currentTimeMillis();
    try {
      new Runner("test", SINGLE_RUNS).start();

      do {
        synchronized (lock) {
          lock.wait();
        }
      } while (running > 0);
    } finally {
      elapsed = System.currentTimeMillis() - time;
    }

    write("Nested", SINGLE_RUNS, elapsed);
  }

  public void testFlat() throws Exception {
    long time;
    long elapsed;

    monitor = new FlatMonitor(new DefaultTimer());

    //warm up
    new Runner("warmup", WARMUP_RUNS).run();
    monitor.reset();

    // measurement run
    time = System.currentTimeMillis();
    try {
      new Runner("test", SINGLE_RUNS).start();

      do {
        synchronized (lock) {
          lock.wait();
        }
      } while (running > 0);
    } finally {
      elapsed = System.currentTimeMillis() - time;
    }

    write("Flat", SINGLE_RUNS, elapsed);
  }


  public class Runner extends Thread {
    private String testPointName;
    private int runs;

    public Runner(String aTestPointName, int aRuns) {
      testPointName = aTestPointName;
      runs = aRuns;
    }

    public void run() {
      running++;
      while (runs > 0) {
        final MeasurementPoint point = new MeasurementPoint(monitor, testPointName);
        final MeasurementPoint nested = new MeasurementPoint(monitor, "nested" + testPointName);
        final MeasurementPoint nesteded = new MeasurementPoint(monitor, "nestedNested" + testPointName);
        nesteded.collect();
        nested.collect();
        point.collect();

        runs--;
      }

      running--;
      synchronized (lock) {
        lock.notifyAll();
      }
    }

  }
}
