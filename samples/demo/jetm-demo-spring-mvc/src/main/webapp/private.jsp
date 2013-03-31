<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" lang="en_US" xml:lang="en_US">
<head>
  <meta http-equiv="content-type" content="text/html; charset=ISO-8859-1"/>
  <title>Java(TM) Execution Time Measurement Library</title>
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
    <h3>JETM Spring MVC Online Demo - Member login</h3>

    <p>
      Hi
      <b>
        <c:out value="${requestScope.user.firstName}"/>
        <c:out value="${requestScope.user.lastName}"/>
      </b>,
    </p>

    <p>
      this is the somewhat private space of the demo. Your registration data:
    </p>
    <table border="0" cellspacing="5">
      <tr>
        <td>Firstname</td>
        <td>
          <c:out value="${requestScope.user.firstName}"/>
        </td>
      </tr>
      <tr>
        <td>Lastname</td>
        <td>
          <c:out value="${requestScope.user.lastName}"/>
        </td>
      </tr>
      <tr>
        <td>User login</td>
        <td>
          <c:out value="${requestScope.user.userName}"/>
        </td>
      </tr>
      <tr>
        <td>E-mail Address</td>
        <td>
          <c:out value="${requestScope.user.email}"/>
        </td>
      </tr>
      <tr>
        <td>Registration date</td>
        <td>
          <c:out value="${requestScope.user.createDate}"/>
        </td>
      </tr>
    </table>

    <p>
      Since this is all you can do in the demo you better look at the
      <a href="#" onclick="openConsole(); return false;">Performance Monitoring Console</a></p>
      to see the aggregated statistics of the demo.
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