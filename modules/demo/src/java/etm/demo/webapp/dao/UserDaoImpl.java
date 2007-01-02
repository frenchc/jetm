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
package etm.demo.webapp.dao;

import java.util.Hashtable;
import java.util.Map;
import java.util.Date;

/**
 * @author void.fm
 * @version $Revision$
 */
public class UserDaoImpl implements UserDao {

  private Map users;
  private int userId = 1;

  public UserDaoImpl() {
    users = new Hashtable();
    User user = new User();
    user.setFirstName("John");
    user.setLastName("Doe");
    user.setUserName("foo");
    user.setPassword("bar");
    user.setEmail("john.doe@foobar.com");

    create(user);
  }

  public User findUser(String username, String password) {
    try {
      Thread.sleep((long) (Math.random() * 2d));
    } catch (InterruptedException e) {
      // ingored
    }

    User user = (User) users.get(username);
    if (user != null && user.getPassword().equals(password)) {
      return user;
    }

    return null;
  }



  public User create(User user) {
    if (hasUserWithUsername(user.getUserName())) {
      throw new NonUniqueObjectException();
    }
    user.setUserId(getNextId());
    user.setCreateDate(new Date());
    user.setLastModifiedDate(user.getLastModifiedDate());

    try {
      Thread.sleep((long) (Math.random() * 10d));
    } catch (InterruptedException e) {
      // ingored
    }
    users.put(user.getUserName(), user);
    return user;
  }

  public boolean hasUserWithUsername(String aUsername) {
    return users.get(aUsername) != null;
  }

  private int getNextId() {
    synchronized (this) {
      int id = userId;
      userId++;

      return id;
    }
  }
}
