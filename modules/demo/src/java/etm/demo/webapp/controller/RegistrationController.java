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

package etm.demo.webapp.controller;

import etm.demo.webapp.dao.User;
import etm.demo.webapp.service.MessagingService;
import etm.demo.webapp.service.UserManagementService;
import etm.demo.webapp.service.UserExistsException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.validation.BindException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author void.fm
 * @version $Revision$
 */
public class RegistrationController extends SimpleFormController {

  private UserManagementService userManagementService;
  private MessagingService messagingService;

  public RegistrationController(UserManagementService aUserManagementService, MessagingService aMessagingService) {
    userManagementService = aUserManagementService;
    messagingService = aMessagingService;
  }


  protected ModelAndView onSubmit(Object object) throws Exception {
    Registration registration = (Registration) object;
    try {
      User user = userManagementService.create(
        registration.getFirstName(), registration.getLastName(),
        registration.getUserName(), registration.getPassword(),
        registration.getEmail());

      messagingService.sendMail(user, "registration");

      ModelAndView view = new ModelAndView("private");
      view.addObject("user", user);
      return view;
    } catch (UserExistsException e) {
      ModelAndView modelAndView = new ModelAndView("registrationForm");
      modelAndView.addObject("registration", registration);
      modelAndView.addObject("errorMessage", "Username " + registration.getUserName() + " already exists.");
      return modelAndView;
    }
  }


  protected Object formBackingObject(HttpServletRequest httpServletRequest) throws Exception {
    return new Registration();
  }
}
