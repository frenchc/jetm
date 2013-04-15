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

package etm.demo.webapp.javaee.web.registration;

import etm.demo.webapp.javaee.domain.user.UserManagementService;
import etm.demo.webapp.javaee.web.core.Outcome;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 *
 * @version $Revision: 372 $
 * @author void.fm
 *
 */
@Named
@RequestScoped
public class RegistrationBean {

  @Inject
  private UserManagementService service;


  private String firstName;
  private String lastName;
  private String eMail;
  private String userName;
  private String password;
  private String passwordRepeat;

  public String geteMail() {
    return eMail;
  }

  public void seteMail(String aEMail) {
    eMail = aEMail;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String aFirstName) {
    firstName = aFirstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String aLastName) {
    lastName = aLastName;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String aPassword) {
    password = aPassword;
  }

  public String getPasswordRepeat() {
    return passwordRepeat;
  }

  public void setPasswordRepeat(String aPasswordRepeat) {
    passwordRepeat = aPasswordRepeat;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String aUserName) {
    userName = aUserName;
  }

  public Outcome register() {
    service.create(firstName, lastName, userName, password, eMail);

    return Outcome.SUCCESS;
  }
}
