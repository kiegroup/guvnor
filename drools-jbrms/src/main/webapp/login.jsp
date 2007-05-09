<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>JBoss Rules Management System - Login</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<link rel="stylesheet" href="/drools-jbrms/login.css" type="text/css" />
	
<script type="text/javascript">
//<![CDATA[
	function focusit() {
		document.getElementById('log').focus();
	}
	window.onload = focusit;
//]]>
</script>
</head>

<body>
	
	<div id="login">
		<h1>Rules Management System</a></h1>
		<%
			if (request.getParameter("error") != null) {
		%>
			<div id='login_error'><strong>Error</strong>: Wrong username or Password</div>
		<%
			}
		%>
		<form name="loginform" id="loginform" action="/drools-jbrms/j_security_check" method="post">
			<p><label>Username:<br />
			<input type="text" name="j_username" id="log" value="" size="20" tabindex="1" /></label></p>

			<p><label>Password:<br />
			<input type="password" name="j_password" id="pwd" value="" size="20" tabindex="2" /></label></p>

			<p class="submit"><input type="submit" name="submit" id="submit" value="Login" tabindex="4" /> <input type="hidden" name="redirect_to" value="wp-admin/" /></p>
		</form>

		<ul>
			<li><a href="javascript:alert('not implemented')" title="Password Lost and Found">Lost your password?</a></li>
		</ul>
	</div>
</body>
</html>
