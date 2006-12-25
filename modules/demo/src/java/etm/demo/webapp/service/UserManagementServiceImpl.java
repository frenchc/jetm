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
package etm.demo.webapp.service;

import etm.demo.webapp.dao.User;
import etm.demo.webapp.dao.UserDao;
import etm.demo.webapp.dao.NonUniqueObjectException;

/**
 * @author void.fm
 * @version $Revision$
 */
public class UserManagementServiceImpl implements UserManagementService {
  private UserDao userDao;

  public UserManagementServiceImpl(UserDao aDao) {
    userDao = aDao;
  }

  public User authenticate(String username, String password) {
    return userDao.findUser(username, password);
  }

  public User create(String firstName, String lastname, String username, String password, String email) {
    User user = new User();
    user.setFirstName(firstName);
    user.setLastName(lastname);
    user.setUserName(username);
    user.setPassword(password);
    user.setEmail(email);
    try {
      return userDao.create(user);
    } catch (NonUniqueObjectException e) {
      throw new UserExistsException();
    }
  }

  public boolean isUnusedUserName(String userName) {
    return !userDao.hasUserWithUsername(userName);
  }
}
