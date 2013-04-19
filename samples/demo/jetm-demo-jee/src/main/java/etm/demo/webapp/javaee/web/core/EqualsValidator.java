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

package etm.demo.webapp.javaee.web.core;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * @author void.fm
 * @version $Revision: 372 $
 */
@FacesValidator("equalsValidator")
public class EqualsValidator implements Validator {

  public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    UIInput thisComponent = (UIInput) component;
    UIInput compareToComponent = (UIInput) component.getAttributes().get("compareTo");
    HtmlOutputLabel compareToLabel = (HtmlOutputLabel) component.getAttributes().get("compareToLabel");
    HtmlOutputLabel thisLabel = (HtmlOutputLabel) component.getAttributes().get("thisLabel");


    if (compareToComponent != null) {
      if (compareToComponent.isValid()) {
        Object thisValue = value;
        Object compareToValue = compareToComponent.getValue();
        if (thisValue == null || !thisValue.equals(compareToValue)) {
          String thisLabelText = thisComponent.getClientId();
          String compareToLabelText = compareToComponent.getClientId();
          if (compareToLabel != null) {
            compareToLabelText = (String) compareToLabel.getValue();
          }
          if (thisLabel != null) {
            thisLabelText = (String) thisLabel.getValue();
          }

          throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
            compareToLabelText + " does not match " + thisLabelText,
            null));

        }
      }
    }
  }


}
