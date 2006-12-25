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

package etm.tutorial.fiveminute.client;

import etm.tutorial.fiveminute.server.OrderAgent;
import etm.tutorial.fiveminute.server.StockItem;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;

/**
 * A client class that executes remote commands using
 * the SpringFramework RMI infrastructure.
 *
 * @author void.fm
 * @version $Revision$
 */
public class OrderClient {

  private OrderAgent agent;

  public OrderClient(OrderAgent aAgent) {
    agent = aAgent;
  }


  public void execute() {
    boolean shouldRun = true;

    while (shouldRun) {
      printCurrentStock();
      printLine();
      int itemId = readAction();
      if (itemId > 0) {
        processOrder(itemId);
      } else if (itemId == 0) {
        shouldRun = false;
      } else {
        System.out.println("Error...");
      }
    }
  }

  private void processOrder(int aItemId) {
    System.out.print("Enter Quantity: ");
    int quantity = readAction();
    agent.placeOrder(aItemId, quantity);
  }

  private int readAction() {
    BufferedReader stdIn;
    stdIn = new BufferedReader(new InputStreamReader(System.in));
    try {
      String line = stdIn.readLine();
      return Integer.parseInt(line);
    } catch (IOException e) {
      return -1;
    }
  }

  private void printLine() {
    System.out.print("Enter item id to order (or 0 exit): ");
  }

  private void printCurrentStock() {
    System.out.println("ID : Item");
    List list = agent.listStock();
    for (int i = 0; i < list.size(); i++) {
      StockItem stockItem = (StockItem) list.get(i);
      System.out.print(" ");
      System.out.print(stockItem.getItem().getId());
      System.out.print(" : ");
      System.out.println(stockItem);
    }
  }

}
