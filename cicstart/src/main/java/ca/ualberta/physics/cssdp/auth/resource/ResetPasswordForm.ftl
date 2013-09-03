<html>
<head>
<title>CICSTART Reset Password</title>
</head>
<body>
	<h1>Reset Your CICSTART Password</h1>
	<form action="${action}" method="post">
		<fieldset>	
			<legend>Password Reset Data</legend>
			<table>
				<tr><td><label>Email</label></td><td><input type="text" name="email" readonly size="50" value="${email}"></td></tr>
				<tr><td><label>Token</label></td><td><input type="text" name="token" readonly size="50" value="${token}"></td></tr>
				<tr><td><label>New Password</label></td><td><input type="password" name="newpassword1" size="25"/></td></tr>
				<tr><td><label>Retype Password</label></td><td><input type="password" name="newpassword2" size="25"/></td></tr>
			</table>
			<input type="submit"/>
		</fieldset>
	</form>
</body>


</html>