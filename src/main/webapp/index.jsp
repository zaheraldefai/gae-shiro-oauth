<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title>App Engine & Shiro with OAuth</title>

<script type="text/javascript" src="bower_components/hello/dist/hello.all.js"></script>
<script src="https://apis.google.com/js/client.js"></script>




<script>
	

	var arr = (window.location.href).split("/");
	var basePath = arr[0] + "//" + arr[2];
	var baseAPIPath = basePath+'/_ah/api';

	var facebook_apiKey;
	if(basePath.includes("localhost")){
		facebook_apiKey = "386123448250133";
	}else{
		facebook_apiKey = "1621297594774983";
	}
	

	var logout = function() {
	
			// Here happens the real Api Call
			var request = gapi.client.request({
				'path' : baseAPIPath + '/authApi/v1/logout',
				'method' : 'GET'
			});
	
			request.then(function(resp) {
				console.log(resp.result);
				// load the page new to get a other view (See Shiro JSP support!)
				location.reload();
			}, function(reason) {
				console.log('Error: ' + reason.result.error.message);
			});
		}
	
	var loginStepTwo = function(provider, access_token) {
		
		console.log("s1");
		var path = baseAPIPath + '/authApi/v1/login/oauth';
		
		// Here happens the real Api Call
		var request = gapi.client.request({
			'path' : path,
			'method' : 'GET',
			params: {'provider':provider, 'access_token':access_token}
		});
		
		
		//var request = gapi.client.authApi.login_oauth({'provider':provider,'access_token':access_token, 'redirectURL':null});
		
		
		request.then(function(resp) {
			console.log(resp.result);
			console.log(resp.status);
			// load the page new to get a other view (See Shiro JSP support!)
			location.reload();
		}, function(reason) {
			console.log('Error: ' + reason.result.error.message);
		});
	}
	
	// This is just for the client-side login!
	hello.init({
		     facebook : facebook_apiKey
		},{
			 redirect_uri: basePath+'/index.jsp',
			 scope : 'email'
		});
	
	function login( network ) {
		hello( network ).login().then(function (auth) {
			var access_token = auth.authResponse.access_token;

			switch (network) {
			case 'facebook':loginStepTwo('FACEBOOK', access_token);
				break;
			default: console.log("FALSE provider");
				break;
			}
		});
	}
	
	// Just for the gapi.client library! See clientTest.jsp
	/*
	function init() {
		
		// load the API's
		gapi.client.load('authApi', 'v1', function() {
			
			// here starts the magic
			com.tutorials.gaeshiro.webclient.authapi.enableButtons();
			
		}, baseAPIPath);
	}
	*/
</script>

</head>
<body>
	<h1>App Engine & Shiro with OAuth</h1>
	<p>
		The Buttons are the way how to login with the first step from the client <br>
		and the links the way how to do both steps from the server!
	</p>

	<pre></pre>

	<shiro:guest>
		<h2>Login with Social Provider</h2>
		<br>
		<button onclick="login('facebook')">Facebook login with HelloJS</button>
	</shiro:guest>

	<p id="status"></p>
	<p id="error"></p>

	<shiro:user>
	    <h2>Welcome!</h2>
	    <p>Look at your Cookies. You have maybe an rememberMe (if wanted!) and you are authenticated</p>
	    <br>
	    <button id="logout_Button" onclick="logout()">Logout</button>
	</shiro:user>

	<br><br>

	<h3>More</h3>
	<p>
		After you registered a user, you can check it from the <a href="./_ah/admin" target="_blank">Admin Console</a> <br>
		where the content in the DB is presented under Datastore Viewer!
	</p>
	<p>
		To check more API's, go to the <a href="./_ah/api/explorer" target="_blank">API Explorer</a>
	</p>
	<p>
		Here is also a  <a href="./clientTest2.jsp">client example Nr.2</a>, which is using some other API's <br>
		to check if the Auth is working right.
	</p>

	<div id="copyright" style="  float: right;text-align: center;color: #896516;padding-top: 15px;">
		© Zaher Aldefai 2015
	</div>
</body>
</html>