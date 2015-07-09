

/** google global namespace for Google projects. */
var com = com || {};
com.tutorials = com.tutorials || {};
com.tutorials.gaeshiro = com.tutorials.gaeshiro || {};
com.tutorials.gaeshiro.webclient = com.tutorials.gaeshiro.webclient || {};
com.tutorials.gaeshiro.webclient.authapi = com.tutorials.gaeshiro.webclient.authapi || {};
com.tutorials.gaeshiro.webclient.someapi = com.tutorials.gaeshiro.webclient.someapi || {};

// This is the API-ROOT
com.tutorials.gaeshiro.webclient.baseAPIPath = 'http://localhost:8888/_ah/api';


/**
 * Enables the button callbacks in the UI.
 */
com.tutorials.gaeshiro.webclient.someapi.enableButtons = function() {
	
  var getApi_Button = document.querySelector('#getApi_Button');
  getApi_Button.addEventListener('click', function(e) {
	  
	  // Here happens the real Api Call
	  // IMPORTANT: this is an generated method for the API Call's
	  var request = gapi.client.someApi.getApi({'someData':'magic_value'});
	  
//	  // Here happens the real Api Call
//	  // Not generated Method!
//	  var request = gapi.client.request({
//          'path': com.tutorials.gaeshiro.webclient.baseAPIPath + '/someApi/v1/getApi',
//          'method': 'GET'
//      });
	  
	  request.then(function(resp) {
			document.getElementById('status').innerHTML = 'Status: ' + resp.status;
			if(resp.result){
				document.getElementById('message').innerHTML = "see Result in Console";
				console.log(resp.result);
			}else{
				document.getElementById('message').innerHTML = "";
			}
		}, function(reason) {
			document.getElementById('status').innerHTML = 'Status: ' + reason.status;
			document.getElementById('message').innerHTML = 'Error: ' + reason.statusText;
		});
  });
  
  document.querySelector('#getApiWithParameter_Button').addEventListener('click', function(e) {
	  
	  // Here happens the real Api Call
	  // IMPORTANT: this is an generated method for the API Call's
	  var request = gapi.client.someApi.getApiWithParameter({'someData':'magic_value'});
	  
//	  // Here happens the real Api Call
//	  // Not generated Method!
//	  var request = gapi.client.request({
//          'path': com.tutorials.gaeshiro.webclient.baseAPIPath + '/someApi/v1/getApiWithParameter',
//          'method': 'GET',
//          'params' : {'someData':'magic_value'}
//      });
	  
	  request.then(function(resp) {
			document.getElementById('status').innerHTML = 'Status: ' + resp.status;
			if(resp.result){
				document.getElementById('message').innerHTML = "see Result in Console";
				console.log(resp.result);
			}else{
				document.getElementById('message').innerHTML = "";
			}
		}, function(reason) {
			document.getElementById('status').innerHTML = 'Status: ' + reason.status;
			document.getElementById('message').innerHTML = 'Error: ' + reason.statusText;
		});
  });
  
  document.querySelector('#getApiWithResult_Button').addEventListener('click', function(e) {

	  // Here happens the real Api Call
	  // IMPORTANT: this is an generated method for the API Call's
	  var request = gapi.client.someApi.getApiWithResult();
	  
//	  // Here happens the real Api Call
//	  // Not generated Method!
//	  var request = gapi.client.request({
//          'path': com.tutorials.gaeshiro.webclient.baseAPIPath + '/someApi/v1/getApiWithResult',
//          'method': 'GET'
//      });
	  
	  request.then(function(resp) {
			document.getElementById('status').innerHTML = 'Status: ' + resp.status;
			if(resp.result){
				document.getElementById('message').innerHTML = "see Result in Console";
				console.log(resp.result);
			}else{
				document.getElementById('message').innerHTML = "";
			}
		}, function(reason) {
			document.getElementById('status').innerHTML = 'Status: ' + reason.status;
			document.getElementById('message').innerHTML = 'Error: ' + reason.statusText;
		});
  });
};

com.tutorials.gaeshiro.webclient.authapi.enableButtons = function() {
	
	  document.querySelector('#logout_Button').addEventListener('click', function(e) {
		  
		  // Here happens the real Api Call
		  // IMPORTANT: this is an generated method for the API Call's
		  var request = gapi.client.authApi.logout();
		  
		  request.then(function(resp) {// works fine
			  console.log(resp.result);
		  		// loads the page new
		  		window.location.href = window.location.href;
			  
			}, function(reason) {// failure happens
				document.getElementById('status').innerHTML = 'Status: ' + reason.status;
				document.getElementById('message').innerHTML = 'Error: ' + reason.statusText;
			});
	  });
	  
}


var loginStepTwo = function(provider, access_token) {
	
	var request = gapi.client.authApi.login_oauth({'provider':provider,'access_token':access_token});
	
	
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
	     google : '618801986276-qnvtd40d95k4imm2qd2bq5uov0v39lu8.apps.googleusercontent.com',
	     facebook : '386123448250133'
	},{
		 redirect_uri: 'http://localhost:8888/',
		 scope : 'email'
	});

function login( network ) {
	hello( network ).login().then(function (auth) {
		var access_token = auth.authResponse.access_token;

		switch (network) {
		case 'facebook':loginStepTwo('FACEBOOK', access_token);
			break;
		case 'google':loginStepTwo('GOOGLE', access_token);
			break;
		default: console.log("FALSE provider");
			break;
		}
	});
}


/**
 * Initializes the application.
 * 
 * <br><br>
 * WARNING: The Init functions should be in the end of the js file or it will process an error!
 */
com.tutorials.gaeshiro.webclient.init = function() {
	
	// load the API's
	gapi.client.load('authApi', 'v1', function() {
		
		// here starts the magic
		com.tutorials.gaeshiro.webclient.authapi.enableButtons();
		
	}, com.tutorials.gaeshiro.webclient.baseAPIPath);
	
	// load the API's
	gapi.client.load('someApi', 'v1', function() {
		
		// here starts the magic
		com.tutorials.gaeshiro.webclient.someapi.enableButtons();
		
		// This is the API-ROOT
	}, com.tutorials.gaeshiro.webclient.baseAPIPath);

};

