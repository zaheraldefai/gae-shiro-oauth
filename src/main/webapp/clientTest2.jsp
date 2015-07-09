<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Couplers</title>
<script async src="/js/base.js"></script>
<script>
    function init() {
    	com.tutorials.gaeshiro.webclient.init();
    }
</script>

<script type="text/javascript" src="bower_components/hello/dist/hello.all.js"></script>
<script src="https://apis.google.com/js/client.js?onload=init"></script>

</head>
<body>
<h1>Client Example Nr.2</h1>
<p>
	After you logged in, you can try the getApi..._Button's, which call some API's<br>
	where can just be called, if you are authenticated or remembered!.<br>
	Otherwise, there come an Error message.
</p>

	<shiro:guest>
		<h2>Login with Social Provider</h2>
		<br>
		<button onclick="login('facebook')">Facebook login with HelloJS</button>
		<br>
		<button onclick="login('google')">Google login with HelloJS</button>
		
	</shiro:guest>

	<shiro:user>
	    <h2>Welcome!</h2>
	    <p>Look at your Cookies. You have maybe an rememberMe (if wanted!) and you are authenticated</p>
	    <br>
	    <button id="logout_Button" onclick="logout()">Logout</button>
	</shiro:user>

	<br><br>

<button id="getApi_Button">getApi_Button</button><br>
<button id="getApiWithParameter_Button">getApiWithParameter_Button</button><br>
<button id="getApiWithResult_Button">getApiWithResult_Button</button><br>


<br>
	<p id="status"></p>
	<p id="message"></p>
<br><br>

<a href="./">Back</a> to client example Nr.1
</body>
</html>