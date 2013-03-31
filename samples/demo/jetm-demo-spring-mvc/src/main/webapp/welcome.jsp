<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
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
  <div id="header">JETM Spring MVC Online Demo</div>
  <div id="subheader">Runtime performance monitoring made easy</div>
  <div id="main">
    <h3>JETM Spring MVC Online Demo - Welcome</h3>

    <div style="float: left; margin-right: 40px; margin-bottom: 20px;">
      <form action="login.action" method="post">
        <table>
          <spring:bind path="login.username">
            <tr>
              <td>Login</td>
              <td><input name="username" type="text" size="10" value="${status.value}"/></td>
            </tr>
          </spring:bind>
          <spring:bind path="login.password">
            <tr>
              <td>Password</td>
              <td><input name="password" type="password" size="10"/></td>
            </tr>
          </spring:bind>
          <tr>
            <td colspan="2" align="center"><input type="SUBMIT" value="Login"/></td>
          </tr>

        </table>
      </form>
      <p align="center"><a href="showRegistration.action">Register now!</a></p>
    </div>

    <p>
      This is a simple web application that demonstrates JETM performance monitoring.
    </p>

    <p>
      It records and aggregates request execution times. You can always access these
      performance statistics using the link 'Monitoring console' in the bottom menu.
    </p>

    <p>
      You should now start to collect execution times. Either login to
      access the private demo space. Or request a registration form and submit it.
      The registration data are stored in memory only. If you don't like to register
      use username "foo" and password "bar" to login or click
      <a href="#" onclick="submit(); return false;">here</a> without entering login credentials at all.
    </p>

    <p style="clear: left">
      This demo uses Springframework MVC to demonstrate declarative performance monitoring.
      Demo Sources are available as <a href="jetm-demo-src.tar.gz">tar.gz</a>,
      <a href="jetm-demo-src.zip">zip</a> or via <a href="http://jetm.void.fm/svn.html">subversion respository</a>.
    </p>

  </div>

  <div style="visibility: hidden;">
    <form action="login.action" method="post" name="loginForm">
      <input type="text" name="username" value="foo"/>
      <input type="text" name="password" value="bar"/>

    </form>
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
<!-- Last modified  $Date: 2006/10/18 07:54:42 $ -->
</html>