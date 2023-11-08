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
import etm.demo.webapp.service.UserExistsException;
import etm.demo.webapp.service.UserManagementService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import javax.validation.Valid;

/**
 * @author void.fm
 * @version $Revision$
 */
@Controller
@RequestMapping({ "/showRegistration.action", "/registration.action" })
public class RegistrationController {

  private final UserManagementService userManagementService;
  private final MessagingService messagingService;
  private final Validator validator;

  public RegistrationController(
      UserManagementService aUserManagementService,
      MessagingService aMessagingService,
      @Qualifier("registrationValidator") Validator registrationValidator
  ) {
    userManagementService = aUserManagementService;
    messagingService = aMessagingService;
    validator = registrationValidator;
  }

  @InitBinder("registration")
  public void initBinder(WebDataBinder binder) {
    binder.setValidator(validator);
  }

  @RequestMapping(method = RequestMethod.GET)
  public ModelAndView renderForm(
      @ModelAttribute("registration") Registration registration
  ) {
    ModelAndView modelAndView = new ModelAndView("registrationForm");
    modelAndView.addObject("registration", registration);
    return modelAndView;
  }

  @RequestMapping(method = RequestMethod.POST)
  public ModelAndView processForm(
      @Valid @ModelAttribute("registration") Registration registration,
      BindingResult result
  ) {
    if (result.hasErrors()) {
      return new ModelAndView("registrationForm");
    }

    try {
      User user = userManagementService.create(
          registration.getFirstName(),
          registration.getLastName(),
          registration.getUserName(),
          registration.getPassword(),
          registration.getEmail()
      );

      messagingService.sendMail(user, "registration");

      ModelAndView modelAndView = new ModelAndView("private");
      modelAndView.addObject("user", user);
      return modelAndView;
    } catch (UserExistsException e) {
      String errorMessage = "Username " + registration.getUserName() + " already exists.";
      ModelAndView modelAndView = new ModelAndView("registrationForm");
      modelAndView.addObject("registration", registration);
      modelAndView.addObject("errorMessage", errorMessage);
      return modelAndView;
    }
  }
}
