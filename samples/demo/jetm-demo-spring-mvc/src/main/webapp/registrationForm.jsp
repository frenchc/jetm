<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" lang="en_US" xml:lang="en_US">
<head>
  <meta http-equiv="content-type" content="text/html; charset=ISO-8859-1"/>
  <title>JETM Web Demo</title>
  <link rel="stylesheet" type="text/css" href="default.css"/>
  <script type="text/javascript">
    function submit() {
      document.forms['loginForm'].submit();
    }

    function openConsole() {
      window.open('performance/index', 'console', 'width=900,left=0,top=0,scrollbars=true');
    }
  </script>
</head>

<body>
<div id="content">
<div id="header">JETM Web Demo</div>
<div id="subheader">Runtime performance monitoring made easy</div>
<div id="main">
<h3>JETM Online Demo - Registration page</h3>

<p>
  This page allows you to register to the web demo. All registation data are just collected
  for demonstration purposes. They will actually be stored in memory and thrown away at
  restart. If you don't like to register use "foo"/"bar" for username/password or click
  <a href="#" onclick="submit(); return false;">here</a> to login directly.
</p>

<div align="center">


<form action="registration.action" method="post">
<table>
<tr>
  <td></td>
  <td>
    <font color="red">
      <c:out value="${requestScope.errorMessage}"/>
    </font>
    <spring:bind path="registration">
      <font color="red">
        <c:forEach items="${status.errorMessages}" var="error">
          <c:out value="${error}"/>
          <br/>
        </c:forEach>
      </font>
    </spring:bind>
  </td>
</tr>
<spring:bind path="registration.firstName">

  <tr>

    <td>
      <c:choose>
        <c:when test="${status.error}"><font color="red">Firstname</font></c:when>
        <c:otherwise>Firstname</c:otherwise>

      </c:choose>
    </td>
    <td>
      <input name="firstName" type="text" size="25" value="<c:out value='${status.displayValue}' />"/>
    </td>
  </tr>
</spring:bind>

<spring:bind path="registration.lastName">

  <tr>
    <td>
      <c:choose>
        <c:when test="${status.error}"><font color="red">Lastname</font></c:when>
        <c:otherwise>Lastname</c:otherwise>
      </c:choose>
    </td>
    <td><input name="lastName" type="text" size="25" value="<c:out value='${status.displayValue}' />"/></td>
  </tr>
</spring:bind>
<spring:bind path="registration.email">

  <tr>
    <td>
      <c:choose>
        <c:when test="${status.error}"><font color="red">E-Mail</font></c:when>
        <c:otherwise>E-Mail</c:otherwise>
      </c:choose>
    </td>
    <td><input name="email" type="text" size="25" value="<c:out value="${status.displayValue}" />"/>
    </td>
  </tr>
</spring:bind>

<spring:bind path="registration.userName">

  <tr>
    <td>
      <c:choose>
        <c:when test="${status.error}"><font color="red">Login</font></c:when>
        <c:otherwise>Login</c:otherwise>
      </c:choose>
    </td>
    <td><input name="<c:out value="${status.expression}" />" type="text" size="25"
               value="<c:out value="${status.displayValue}" />"/>
    </td>
  </tr>
</spring:bind>
<spring:bind path="registration.password">

  <tr>
    <td>
      <c:choose>
        <c:when test="${status.error}"><font color="red">Password</font></c:when>
        <c:otherwise>Password</c:otherwise>
      </c:choose>
    </td>
    <td><input name="password" type="password" size="25" value="<c:out value="${status.value}" />"/>
    </td>
  </tr>
</spring:bind>
<spring:bind path="registration.passwordrepeat">

  <tr>
    <td>
      <c:choose>
        <c:when test="${status.error}"><font color="red">Password repeat</font></c:when>
        <c:otherwise>Password repeat</c:otherwise>
      </c:choose>
    </td>
    <td><input name="passwordrepeat" type="password" size="25" value="<c:out value="${status.value}" />"/>
    </td>
  </tr>
</spring:bind>

<tr>
  <td></td>
  <td><input type="submit"/></td>
</tr>
</table>
</form>
</div>

<div style="visibility: hidden;">
  <form action="login.action" method="post" name="loginForm">
    <input type="text" name="username" value="foo"/>
    <input type="text" name="password" value="bar"/>

  </form>
</div>

</div>

<div id="menu">
  <a href="welcome.action">Demo Home</a>
  |
  <a href="#" onclick="openConsole(); return false;">Monitoring Console</a>
  |
  <a href="http://jetm.void.fm">JETM Home</a>
</div>
</div>

</body>
<!-- Last modified  $Date: 2006/10/06 21:38:12 $ -->
</html>