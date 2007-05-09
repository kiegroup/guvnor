<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>WordPress &rsaquo; Login</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<link rel="stylesheet" href="http://fmeyer.org/wp-admin/wp-admin.css" type="text/css" />
	<script type="text/javascript">
	function focusit() {
		document.getElementById('log').focus();
	}
	window.onload = focusit;
	</script>
</head>
<body>

<div id="login">

<h1><a href="http://wordpress.org/">WordPress</a></h1>

<form name="loginform" id="loginform" action="wp-login.php" method="post">
<p><label>Username:<br /><input type="text" name="log" id="log" value="" size="20" tabindex="1" /></label></p>
<p><label>Password:<br /> <input type="password" name="pwd" id="pwd" value="" size="20" tabindex="2" /></label></p>
<p>
  <label><input name="rememberme" type="checkbox" id="rememberme" value="forever" tabindex="3" /> 
  Remember me</label></p>
<p class="submit">
	<input type="submit" name="submit" id="submit" value="Login &raquo;" tabindex="4" />

	<input type="hidden" name="redirect_to" value="wp-admin/" />
</p>
</form>
<ul>
	<li><a href="http://fmeyer.org/" title="Are you lost?">&laquo; Back to blog</a></li>
	<li><a href="http://fmeyer.org/wp-login.php?action=lostpassword" title="Password Lost and Found">Lost your password?</a></li>
</ul>
</div>

</body>
</html>

