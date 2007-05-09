<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>JBoss Rules Management System - Login</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<link rel="stylesheet" href="/drools-jbrms/login.css" type="text/css" />
</script>
</head>

<body onload="document.forms[0].elements[0].focus()">
  <table border="0" width="100%" align="center">
  
  <tr><td align="center">
  <form name="login" method="post" action="/drools-jbrms/j_security_check">
    <table>
      <tr><td style="height: 125px " colspan="2">
       <%
			if (request.getParameter("error") != null) {
		%>
			<div id='login_error'><strong>Error</strong>: Wrong username or Password</div>
		<%
			}
		%>
      
      </td></tr>
      <tr>
      	<td colspan="2" align="center"><img src="/drools-jbrms/drools_logo.gif" border="0" /></td>
      </tr>
      <tr><td style="height: 15px"></td></tr>
      <tr>

        <td>Login:</td>
        <td><input type="text" name="j_username" /></td>
      </tr>
      <tr>
        <td>Password:</td>
        <td><input type="password" name="j_password" /></td>
      </tr>
      <tr>

        <td colspan="2" align="center"><input type="submit" value="Login" /></td>
      </tr>
    </table>
  </form>
  </td></tr></table>
  </body>
</html>
