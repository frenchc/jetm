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

import etm.tutorial.fiveminute.server.Item;
import etm.tutorial.fiveminute.server.OrderAgent;
import etm.tutorial.fiveminute.server.StockItem;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.List;

/**
 * A client class that executes remote commands using
 * the SpringFramework RMI infrastructure.
 *
 * @author void.fm
 * @version $Revision$
 */
public class OrderClient {
  private static final String SPACES = "          ";

  private OrderAgent agent;

  public OrderClient(OrderAgent aAgent) {
    agent = aAgent;
  }


  public void execute() {
    boolean shouldRun = true;

    while (shouldRun) {
      printCurrentStock();
      int itemId = readIntFromConsole();
      if (itemId > 0) {
        processOrder(itemId);
      } else if (itemId == 0) {
        shouldRun = false;
      } else {
        System.out.println("Error...");
      }
    }
  }

  protected void processOrder(int aItemId) {
    printPrompt("Enter Quantity: ");
    int quantity = readIntFromConsole();
    agent.placeOrder(aItemId, quantity);
  }

  protected int readIntFromConsole() {
    BufferedReader stdIn;
    stdIn = new BufferedReader(new InputStreamReader(System.in));
    try {
      String line = stdIn.readLine();
      return Integer.parseInt(line);
    } catch (Exception e) {
      return -1;
    }
  }

  protected void printCurrentStock() {
    System.out.println("We currently have on stock:");
    System.out.println(" ------------------------------");
    System.out.println(" | ID | Qty | Item    | Price |");
    System.out.println(" ------------------------------");

    List list = agent.listStock();
    for (int i = 0; i < list.size(); i++) {
      StockItem stockItem = (StockItem) list.get(i);
      Item item = stockItem.getItem();

      System.out.print(" |  ");
      System.out.print(item.getId());
      System.out.print(" | ");
      System.out.print(ensureWidth(String.valueOf(stockItem.getQuantity()), 3));
      System.out.print(" | ");
      System.out.print(ensureWidth(item.getName(), 7));
      System.out.print(" | ");
      System.out.print(NumberFormat.getCurrencyInstance().format(item.getPrice()));
      System.out.println(" | ");
    }
    System.out.println(" ------------------------------");
    System.out.println();
    printPrompt("Enter item id to order (or 0 exit): ");
  }

  private String ensureWidth(String value, int i) {
    if (value.length() < i) {
      return SPACES.substring(0, i - value.length()) + value;
    }
    return value;
  }

  protected void printPrompt(String message) {
    if (System.getProperty("ant.runtime") != null) {
      System.out.println(message);
    } else {
      System.out.print(message);
    }
  }

}
