<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" type="text/css" href="css/bootstrap.css">
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>QueuePeek RP</title>
</head>
<body>

	<!-- <form action="loginController" method="post">
		Enter username :<input type="text" name="username"> <br>
		Enter password :<input type="password" name="password"><br>
		<input type="submit" value="Login">
	</form> -->
	<h1>Welcome to QueuePeek!</h1>
	<h3>Sign In</h3>
	<form class="form-horizontal" action="loginController" method="post">
		<fieldset>
			<div class="form-group">
				<label for="inputEmail" class="col-lg-2 control-label">Email</label>
				<div class="col-lg-10">
					<input type="text" class="form-control" id="inputEmail"
						name="username">
				</div>
			</div>
			<div class="form-group">
				<label for="inputPassword" class="col-lg-2 control-label">Password</label>
				<div class="col-lg-10">
					<input type="password" class="form-control" id="inputPassword"
						name="password">
					<div class="form-group">
						<div class="col-lg-10 col-lg-offset-2 center-block">
							<br></br>
							<button type="reset" class="btn btn-default">Cancel</button>
							<button type="submit" class="btn btn-primary">Submit</button>
						</div>
					</div>
				</div>
			</div>
		</fieldset>
	</form>
	<br></br>
	<h3>OR</h3>
	<br></br>
	<form id="OpenIdRegistrationForm" id="form" action="VerifyServlet"
		method="POST">
		<h3>Sign In with OpenID</h3>
		<br /> <label for="inputEmail" class="col-lg-2 control-label">Your
			Open ID</label>
		<div class="col-lg-10 ">
			<input type="text" class="form-control" id="openId"
				name="openidvalue">
			<button type="submit" class="btn btn-primary">Submit</button>
		</div>
	</form>
</body>
</html>