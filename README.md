gae-shiro-oauth
==================
An example Project which shows how to get Security to an AppEngine Project. This example shows how a User can login through OAuth to provide an Authentication with social Accounts (currently just **Facebook** is supported).

Big thanks to [cilogi](https://github.com/cilogi). There is a many work from his [gaeShiro](https://github.com/cilogi/gaeshiro) Example.

### Follow imporatant Components are included:   

- [App Engine][1] *v1.9.22* (Java) with [Endpoints][8] Skeleton
- These on top with Maven Skeleton
- Apache Shiro *v1.2.3* (The Security Framework)
- [Objectify][7] *v5.1.5* instead of the v4.x which are used standard by the init AppEngine project.
- [Scribe][10] *v1.3.7* is the library that can handle with OAuth Authentication for a many Provider like *Facebook*, *Google*, etc.


### Class & File Descriptions

- `webapp.WEB-INF.shiro.ini`  
Here is the configuration about the authentication like which Filter should be used, through which class can the Shiro get the Auth-Data (Realms), ACL (Access Control List), custom Filter, ....

- `com.tutorials.gaeshiro.AuthAPI` & `com.tutorials.gaeshiro.SomeAPI`  
The Api's (Endpoints). Just to test if the user can use the Api's if he is successfull logged in.

- `com.tutorials.gaeshiro.security.oauth.OAuthRealm`  
The Class which show the Shiro Framework, how to get the User Information to authenticate him

- `com.tutorials.gaeshiro.security.MemcacheManager` & `com.tutorials.gaeshiro.security.Memcache`  
To cash the user Information in the AppEngine Cash Service for the Shiro authentication mechanismus

- `com.tutorials.gaeshiro.security.filter.OAuthFilter`  
This Filter, can filter all Requests if needed (see shiro.ini). If the User is not Authenticated, then it will catch him and check if the User/Subject is maybe remembered. If not then it checks if he try to login (through a specified login URL, see shiro.ini property). If nothing of this, then he got an *401* response. See *onAccessDenied* Method for more info.

- `com.tutorials.gaeshiro.security.oauth.*`  
Here are the classes which handle with the oauth Authentication for the Social Login's for *Facebook*, *Google*, etc. In `com.tutorials.gaeshiro.security.oauth.provider.*` are the Classes which are needed for the specific Provider. So for example, if you want to include an Twitter login, then just implement the methods, that are implemented in the other provider classes too. Have a look at the `com.tutorials.gaeshiro.security.oauth.OAuthInfo` class, because there stands, what for Information you get from the Providers (like *email*). Also look at the Provider classes, if you want more then just the *email*, because at the moment this is the only user information, that we request from the providers.

- `com.tutorials.gaeshiro.models.*`  
Models (for persistance). They are not just for the Security! They can also be used for other things. The DAO classes are there to handle with queries for there respective classes.

- `webapp.clientTest.jsp`  
here is an simply Client that use some of the API's to show the workflow of login, logout and register. Have a look to the login and what parameters it gives the `com.tutorials.gaeshiro.security.filter.OAuthFilter`!  
There is two ways how it prevent an login. One way how to do it from an client (at the first step and then verify the access token from the server!) and one way how to do it from the server, which is good for websites (where it works whith the authorization works). Look at this stackoverflow [link][11], where I create a Question which explains this ways.
Also you should know, that we don't use the usually way to call the API's because it is to complicated with the normal JS Endpoints way how to call them. But we use like the Endpoints way also the [Google API Client Library for JavaScript][9].

- - `webapp.clientTest2.jsp` & `webapp.js.base.js`  
A second and little more complex client example


##### Resources

[Google AppEngine (Java)][1]  
[Google AppEngine (Java) - Quick start Video][2]  
[Appache Siro][3]  
[GAEShiro - Example Project with Appengine and Shiro][4]  
[Shiro Tutorial 1][5]  
[Shiro Tutorial 2][6]  
[Objectify][7]  
[AppEngine Endpoints][8]  
[Google API Client Library for JavaScript][9]  
[Scribe: Library to handle Proiver Authentication][10]  
[Stackoverflow: oauth cooperation between client and backend server using scribe-java and hellojs][11]

[1]: https://cloud.google.com/appengine/docs/java/ 
[2]: https://www.youtube.com/watch?v=RRVJdOPiVoo 
[3]: http://shiro.apache.org/
[4]: http://shiro.apache.org/
[5]: http://www.javacodegeeks.com/2012/05/apache-shiro-part-1-basics.html 
[6]: http://blog.xebia.com/2011/04/18/apache-shiro/
[7]: https://github.com/objectify/objectify/wiki
[8]: https://developers.google.com/appengine/docs/java/endpoints/
[9]: https://developers.google.com/api-client-library/javascript/dev/dev_jscript
[10]: https://github.com/fernandezpablo85/scribe-java/releases
[11]: http://stackoverflow.com/questions/30980905/oauth-cooperation-between-client-and-backend-server-using-scribe-java-and-helloj
