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

import etm.demo.webapp.javaee.core.qualifier.Registered;
import etm.demo.webapp.javaee.core.stereotype.Service;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.io.Serializable;

/**
 *
 *
 * @version $Revision: 372 $
 * @author void.fm
 *
 */
@Service
public class UserManagementService implements Serializable {

  private UserRepository userRepository;
  private Event<User> eventSource;

  protected UserManagementService() {
  }


  @Inject
  public UserManagementService(UserRepository aUserRepository, @Registered Event<User> aUserSource) {
    userRepository = aUserRepository;
    eventSource = aUserSource;
  }

  public User authenticate(String username, String password) {
    return userRepository.findUser(username, password);
  }

  public User create(String firstName, String lastname, String username, String password, String email) {
    User user = new User();
    user.setFirstName(firstName);
    user.setLastName(lastname);
    user.setUserName(username);
    user.setPassword(password);
    user.setEmail(email);

    User newUser = userRepository.create(user);
    eventSource.fire(newUser);
    return newUser;
  }

  public boolean isUnusedUserName(String userName) {
    return !userRepository.hasUserWithUsername(userName);
  }

}
