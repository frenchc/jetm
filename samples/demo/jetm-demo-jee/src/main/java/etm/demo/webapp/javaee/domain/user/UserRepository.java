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

package etm.demo.webapp.javaee.domain.user;

import etm.demo.webapp.javaee.core.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 *
 * @version $Revision: 372 $
 * @author void.fm
 *
 */

@Repository
public class UserRepository implements Serializable {

  private Map<String, User> users;
  private int userId = 1;


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
    if (user.getUserName().startsWith("TESTUSER")) {
      // do not store locally for load test
      user.setUserId(-1);
      user.setCreateDate(new Date());
      user.setLastModifiedDate(user.getCreateDate());
      return user;
    }
    if (hasUserWithUsername(user.getUserName())) {
      throw new NonUniqueUserNameException(user.getUserName());
    }
    user.setUserId(getNextId());
    user.setCreateDate(new Date());
    user.setLastModifiedDate(user.getCreateDate());

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


  @PostConstruct
  public void init() {
    users = new ConcurrentHashMap<String, User>();
    User user = new User();
    user.setFirstName("John");
    user.setLastName("Doe");
    user.setUserName("foo");
    user.setPassword("bar");
    user.setEmail("john.doe@foobar.com");

    create(user);
  }


  protected int getNextId() {
    synchronized (this) {
      int id = userId;
      userId++;

      return id;
    }
  }
}
