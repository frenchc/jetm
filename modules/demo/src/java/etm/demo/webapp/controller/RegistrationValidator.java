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

package etm.demo.webapp.controller;

import org.springframework.validation.Validator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.util.StringUtils;
import etm.demo.webapp.service.UserManagementService;

/**
 * @author void.fm
 * @version $Id$
 */
public class RegistrationValidator implements Validator {

  private UserManagementService userManagementService;


  public RegistrationValidator(UserManagementService aUserManagementService) {
    userManagementService = aUserManagementService;
  }

  public boolean supports(Class aClass) {
    return aClass.equals(Registration.class);
  }

  public void validate(Object object, Errors errors) {
    Registration registration = (Registration) object;
    ValidationUtils.rejectIfEmpty(errors, "firstName", "missing", null, "Firstname may not be empty.");
    ValidationUtils.rejectIfEmpty(errors, "lastName", "missing", null, "Lastname may not be empty.");
    ValidationUtils.rejectIfEmpty(errors, "email", "missing", null, "Email may not be empty.");
    ValidationUtils.rejectIfEmpty(errors, "userName", "missing", null, "Username may not be empty.");
    ValidationUtils.rejectIfEmpty(errors, "password", "missing", null, "Password may not be empty.");
    ValidationUtils.rejectIfEmpty(errors, "passwordrepeat", "missing", null, "Repeat may not be empty.");

    if (errors.getErrorCount() > 0) {
      errors.reject("registration", "Fields may not be empty.");
    }

    if (errors.getFieldErrorCount("userName") == 0 && !userManagementService.isUnusedUserName(registration.getUserName()))
    {
      errors.reject("registration", "Username already in use.");
      errors.rejectValue("userName", "username.already.taken", null, "Username already in use.");
    }

    if (errors.getFieldErrorCount("password") == 0 &&
      errors.getFieldErrorCount("passwordrepeat") == 0 &&
      !registration.getPassword().equals(registration.getPasswordrepeat())) {
      errors.reject("registration", "Passwords do not match.");

      registration.setPassword(null);
      registration.setPasswordrepeat(null);
      errors.rejectValue("password", "nonmatching", null, "Passwords do not match.");
      errors.rejectValue("passwordrepeat", "nonmatching", null, "Passwords do not match.");
    }
  }
}
