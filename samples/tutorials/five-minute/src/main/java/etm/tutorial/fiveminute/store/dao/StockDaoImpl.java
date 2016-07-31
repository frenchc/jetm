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

package etm.tutorial.fiveminute.store.dao;

import etm.tutorial.fiveminute.store.UnknownArticleException;
import etm.tutorial.fiveminute.store.model.Item;
import etm.tutorial.fiveminute.store.model.StockItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default demo DAO implementation.
 *
 * @author void.fm
 * @version $Revision$
 */
public class StockDaoImpl implements StockDao {

  private Map catalog = new HashMap();
  private Map stock = new HashMap();


  public StockDaoImpl() {
    loadInitialStock();
  }


  public boolean addOrder(Item item, int quantity) throws UnknownArticleException {
    StockItem stockItem = (StockItem) stock.get(item.getId());
    if (stockItem != null) {
      if (stockItem.getQuantity() >= quantity) {
        stockItem.decreaseQuantity(quantity);
        if (stockItem.getQuantity() == 0) {
          stock.remove(stockItem.getItem().getId());
        }
        return true;
      } else {
        return false;
      }
    } else {
      throw new UnknownArticleException();
    }
  }


  public List getCurrentStock() {
    return new ArrayList(stock.values());
  }

  public Item getItem(int aItemId) {
    return (Item) catalog.get(aItemId);
  }


  private void loadInitialStock() {
    catalog.put(1, new Item(1, "apples", BigDecimal.valueOf(2.99)));
    catalog.put(2, new Item(2, "oranges", BigDecimal.valueOf(1.49)));
    catalog.put(3, new Item(3, "bananas", BigDecimal.valueOf(1.99)));
    catalog.put(4, new Item(4, "grapes", BigDecimal.valueOf(2.49)));

    stock.put(1, new StockItem((Item) catalog.get(1), 15));
    stock.put(2, new StockItem((Item) catalog.get(2), 5));
    stock.put(3, new StockItem((Item) catalog.get(3), 20));
    stock.put(4, new StockItem((Item) catalog.get(4), 11));
  }


}
