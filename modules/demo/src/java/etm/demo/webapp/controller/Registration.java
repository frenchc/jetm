package etm.demo.webapp.controller;

import java.io.Serializable;

/**
 *
 * @version $Id$
 * @author void.fm
 *
 */
public class Registration implements Serializable {
  private String firstName;
  private String lastName;
  private String userName;
  private String password;
  private String passwordrepeat;
  private String email;


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

  public String getUserName() {
    return userName;
  }

  public void setUserName(String aUserName) {
    userName = aUserName;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String aPassword) {
    password = aPassword;
  }

  public String getPasswordrepeat() {
    return passwordrepeat;
  }

  public void setPasswordrepeat(String aPasswordrepeat) {
    passwordrepeat = aPasswordrepeat;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String aEmail) {
    email = aEmail;
  }
}
