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

package etm.tutorial.fiveminute.server.dao;

import etm.tutorial.fiveminute.server.Item;
import etm.tutorial.fiveminute.server.StockItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Default demo DAO implementation.
 *
 * @author void.fm
 * @version $Revision$
 */
public class OrderAgentDaoImpl implements OrderAgentDao {

  private List stock = new ArrayList();


  public OrderAgentDaoImpl() {
    stock.add(new StockItem(new Item(1, "apples", new BigDecimal(2.99)), 15));
    stock.add(new StockItem(new Item(2, "oranges", new BigDecimal(1.49)), 10));
    stock.add(new StockItem(new Item(3, "bananas", new BigDecimal(1.99)), 2));
    stock.add(new StockItem(new Item(4, "grapes", new BigDecimal(2.49)), 10));
  }


  public boolean isAvailable(int item, int quantity) {
    for (int i = 0; i < stock.size(); i++) {
      StockItem stockItem = (StockItem) stock.get(i);
      if (stockItem.getItem().getId() == item && stockItem.getQuantity() >= quantity) {
        return true;
      }
    }
    return false;
  }

  public Item addOrder(int item, int quantity) {
    for (int i = 0; i < stock.size(); i++) {
      StockItem stockItem = (StockItem) stock.get(i);
      if (stockItem.getItem().getId() == item && stockItem.getQuantity() >= quantity) {
        stockItem.decreaseQuantity(quantity);
        return stockItem.getItem();
      }
    }
    return null;
  }


  public List getCurrentStock() {
    List list = new ArrayList();
    for (int i = 0; i < stock.size(); i++) {
      StockItem stockItem = (StockItem) stock.get(i);
      if (stockItem.getQuantity() > 0) {
        list.add(stockItem);
      }
    }
    return Collections.unmodifiableList(list);
  }
}
