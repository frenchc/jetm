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
import etm.tutorial.fiveminute.server.OrderStatus;
import etm.tutorial.fiveminute.server.StockItem;
import etm.tutorial.fiveminute.spring.SpringRuntime;

import java.util.List;

/**
 * A client class that executes remote commands using
 * the SpringFramework RMI infrastructure.
 *
 * @author void.fm
 * @version $Id: OrderClient.java,v 1.1 2006/10/29 14:33:22 french_c Exp $
 */
public class OrderClient extends SpringRuntime {

  private OrderAgent agent;

  public OrderClient() {
    super("order-client.xml");
    agent = (OrderAgent) context.getBean("orderAgent");
    start();
  }

  public static void main(String[] args) {
    if (args.length == 0) {
      printMissingArgument();
      System.exit(-1);
    }

    if ("liststock".equals(args[0])) {
      listStock();
      System.exit(0);
    } else if ("order".equals(args[0]) && args.length >= 3) {
      try {
        String item = args[1];
        int quantity = Integer.parseInt(args[2]);
        orderItem(item, quantity);
        System.exit(0);
      } catch (NumberFormatException e) {
        printInvalidQuantity(args[2]);
        System.exit(-1);
      }
    }
    printParameterError(args);
    System.exit(-1);


  }

  private static void orderItem(String item, int quantity) {
    try {
      OrderClient client = new OrderClient();
      OrderStatus orderStatus = client.agent.placeOrder(item, quantity);
      if (orderStatus.isSuccess()) {
        System.out.println("Successfully ordered " + quantity + " " + orderStatus.getItem() + "." +
          " Order ID is " + orderStatus.getOrderId());
      } else {
        System.out.println("Unable to order item " + quantity + " " + item + ".");
      }
    } catch (Exception e) {
      System.err.println("Error accessing stock server:  " + e.getMessage());
    }
  }

  private static void listStock() {
    try {
      OrderClient client = new OrderClient();

      List list = client.agent.listStock();
      if (list.size() > 0) {
        System.out.println("Currently in stock:");
        for (int i = 0; i < list.size(); i++) {
          StockItem stockItem = (StockItem) list.get(i);
          System.out.println(" " + stockItem.toString());
        }
      } else {
        System.err.println("No more items in stock.");
      }
    } catch (Exception e) {
      System.err.println("Error accessing stock server:  " + e.getMessage());
    }

  }

  private static void printInvalidQuantity(String arg) {
    System.err.println(" Error: '" + arg + "' is not a valid quantity.");
    printUsage();
  }

  private static void printParameterError(String[] args) {
    String parameters = "";
    for (int i = 0; i < args.length; i++) {
      parameters += args[i];
      parameters += ' ';
    }
    System.err.println(" Error: Unsupported operation '" + parameters.trim() + "'.");
    printUsage();
  }

  private static void printMissingArgument() {
    System.err.println(" Error: Missing agent operation.");
    printUsage();
  }

  private static void printUsage() {
    System.err.println(" Usage: orderAgent operation [parameters].");
    System.err.println("  liststock");
    System.err.println("   Lists the currently available stock items, quantity and price.");
    System.err.println("  order -item- -quantity-");
    System.err.println("   Orders the given item at the given quantity. Item and quantity are");
    System.err.println("   mandatory.");
  }
}
