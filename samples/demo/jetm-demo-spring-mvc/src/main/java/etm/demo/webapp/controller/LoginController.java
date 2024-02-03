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
import etm.demo.webapp.service.UserManagementService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author void.fm
 * @version $Revision$
 */
@Controller
@RequestMapping({ "/login.action", "/welcome.action" })
public class LoginController {

  private final UserManagementService userManagementService;

  public LoginController(UserManagementService aUserManagementService) {
    userManagementService = aUserManagementService;
  }

  @RequestMapping(method = RequestMethod.GET)
  public ModelAndView renderForm(
      @ModelAttribute("login") Login login
  ) {
    ModelAndView modelAndView = new ModelAndView("welcome");
    modelAndView.addObject("welcome", login);
    return modelAndView;
  }

  @RequestMapping(method = RequestMethod.POST)
  public ModelAndView processForm(
      @ModelAttribute("login") Login login
  ) {
    User user = userManagementService.authenticate(
        login.getUsername(),
        login.getPassword()
    );

    if (user != null) {
      ModelAndView view = new ModelAndView("private");
      view.addObject("user", user);
      return view;
    } else {
      ModelAndView view = new ModelAndView("welcome");
      view.addObject("login", login);
      return view;
    }
  }
}
