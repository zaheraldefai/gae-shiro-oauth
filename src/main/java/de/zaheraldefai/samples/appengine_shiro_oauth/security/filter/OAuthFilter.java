package de.zaheraldefai.samples.appengine_shiro_oauth.security.filter;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

import com.google.common.collect.Sets;

import de.zaheraldefai.samples.appengine_shiro_oauth.models.user.User;
import de.zaheraldefai.samples.appengine_shiro_oauth.models.user.UserDAO;
import de.zaheraldefai.samples.appengine_shiro_oauth.security.UserAuthType;
import de.zaheraldefai.samples.appengine_shiro_oauth.security.oauth.OAuthAuthenticationToken;
import de.zaheraldefai.samples.appengine_shiro_oauth.security.oauth.OAuthInfo;
import de.zaheraldefai.samples.appengine_shiro_oauth.security.oauth.provider.BaseOAuthProviderInfo;
import de.zaheraldefai.samples.appengine_shiro_oauth.security.oauth.provider.FacebookAuth;

/**
 * This filter catch not authenticated Request's and try to execute an login through OAuth
 * 
 * Many things learned and copied from follow classes:
 * <ul>
 * <li>{@link org.apache.shiro.web.filter.authc.AuthenticatingFilter}</li>
 * <li>{@link org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter}</li>
 * </ul>
 * which also extend the same class like this one.
 * 
 * Most important Method here is the
 * {@link #onAccessDenied(ServletRequest, ServletResponse) onAccessDenied(...)}.
 * Here magic starts here. Read the JavaDoc of it's overridden Method to
 * understand more.
 * 
 * @author zaheraldefai
 *
 */
public class OAuthFilter extends AuthenticationFilter {

	private static final Logger LOG = Logger.getLogger(OAuthFilter.class
			.getName());

	// Don't need anymore, but think about Google API!
//	private static final String CALLBACK_URL = "http://localhost:8888/_ah/api/myApi/v1/oauth/login";
	private static final String PROVIDER_ATTRIBUTE_NAME = "provider";
	
	// provider access token
	private String accessToken;
	
	
	/* shiro.ini properties */
	
	// URL path, where client can do the login process
	private String loginURLPath;
	private String callBack;
	private static String facebook_apiKey;
	private static String facebook_apiSecret;

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {

		accessToken = WebUtils.getCleanParam(request, "access_token");
		if(WebUtils.getCleanParam(request, "redirectURL") != null){
			callBack = WebUtils.getCleanParam(request, "redirectURL");
			
		}else if(callBack == null || callBack == ""){
			
			if(request.getServerName().contains("localhost")){
				// Example: http + :// + localhost + : + 8888
				callBack = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
				facebook_apiKey = "386123448250133";
				facebook_apiSecret = "6ad60f223d2568f5421619c48baece47";
			}else{
				// Example: http + :// + myapp.appspot.com 
				callBack = request.getScheme() + "://" + request.getServerName() ;
			}
		}
		
        // You can also forward an remembered User/Subject,
        // then you have an UserFilter instead an straight AuthFilter
        if(SecurityUtils.getSubject().isRemembered()){
        	LOG.info("User if remembered");
        	return true;
        }
        
        // Through the specified property in the shiro.ini file, we have an special url path, which is choosed for
        // Login, other url paths  don't go through the login process
        if(loginURLPath != null && loginURLPath != "" && !((HttpServletRequest) request).getRequestURI().startsWith(loginURLPath)){
        	HttpServletResponse httpResponse = WebUtils.toHttp(response);
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Service is for anonyms not acessible");
            return false;
        }
        
        
		try {
			
			if (accessToken != null) {
				
				stepTwo(WebUtils.toHttp(request),WebUtils.toHttp(response));

				// return bei oauth auf keinen fall true zurück! sonst wird das login process erneut gestartet
				// und neue tokens werden erzeugt und merkwürdige sachen passieren dabei.
				//return true;

			} else {

				HttpServletResponse httpResponse = WebUtils.toHttp(response);
	            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No access Token found");
	            return false;
			}

		} catch (Exception e) {
			HttpServletResponse httpResponse = WebUtils.toHttp(response);
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Maybe because of the BasePath: " + callBack);
			LOG.info("IOException: " + e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}


	/**
	 * After the user has successful logged in by the provider, the provider
	 * will callback with an code Attribute (access token). This code should be
	 * verified, to see if the user is who he mean he is. After the verified,
	 * the user will be registered (if he is new) and he will be logged in.
	 * 
	 * @param request
	 * @param response
	 */
	private void stepTwo(HttpServletRequest request, HttpServletResponse response) {
		
		try {
			
			BaseOAuthProviderInfo auth = getProvider(request, callBack);
			OAuthInfo info = auth.getUserInfoWithAccessToken(accessToken);
			
			if (info.isError()) {
				String message = info.getErrorString();
				response.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Couldn't get " + info.getUserAuthType()+ " permission: " + message);
			} else {
				String email = info.getEmail();
				UserDAO dao = new UserDAO();
				User user = dao.findUser(email);
				if (user == null) {
					// Achte auf die Rollen die man hier vergibt!
					user = new User(email, null, Sets.newHashSet("user"),Sets.<String> newHashSet());
					dao.saveUser(user);
				}

				OAuthAuthenticationToken token = 
						new OAuthAuthenticationToken(info.getToken(), info.getUserAuthType(), email, request.getRemoteHost());

				Subject subject = SecurityUtils.getSubject();
				subject.login(token);
				
				
				// TODO RequestURL have no QueryString!
//				String UrlPath = request.getPathInfo();
				String UrlPath = request.getRequestURI().toString();
				LOG.info("URLPathInfo: " + UrlPath);
				
				// revoking the token immediately is (a) safe as the token can't float around anywhere,
				// and (b) lets us re-authenticate and logout properly.
				// Just seems odd as the token is meant to last a long time...
				
				//auth.revokeToken(info.getToken(), request, response, UrlPath);
				//response.sendError(HttpServletResponse.SC_ACCEPTED, "You are logged in. Ignore the error ;)");
				
				response.setStatus(HttpServletResponse.SC_ACCEPTED);
				
				// response.sendRedirect(response.encodeRedirectURL(redirectUrl));

			}
		} catch (Exception e) {
			try {
				response.sendError(
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"Something unexpected went wrong: " + e.getMessage());
			} catch (IOException e1) {
				// TODO Should be handled different
				e1.printStackTrace();
			}
		}

		LOG.info("Social login process ends");
	}

	private static BaseOAuthProviderInfo getProvider(HttpServletRequest request, String callBack)
			throws IllegalArgumentException {

		// This is important for the building of your LOGIN URL
		String provider = WebUtils.getCleanParam(request, PROVIDER_ATTRIBUTE_NAME);
		
		
		if (provider == null) {
			throw new IllegalArgumentException("No provider found");
		}
		
		

		try {
			UserAuthType type = UserAuthType.valueOf(provider);
			switch (type) {
			case FACEBOOK:
				return new FacebookAuth(facebook_apiKey, facebook_apiSecret, callBack + "?"+PROVIDER_ATTRIBUTE_NAME+"=FACEBOOK");
			default:
				LOG.warning("Auth type " + provider + "isn't handled");
				return null;
			}
		} catch (Exception e) {
			LOG.warning("Can't work out the auth type of " + provider);
			return null;
		}
	}

	/**
	 * Don't really need that, but it good to know this trick.
	 * 
	 * static boolean isDevelopmentServer() { 
	 * 	SystemProperty.Environment.Value server = SystemProperty.environment.value();
	 *  return server == SystemProperty.Environment.Value.Development;
	 *  }
	 */
	
	
	
	/*
	 * GETTER & SETTER for Properties
	 */
    
	public String getLoginURLPath() {
		return loginURLPath;
	}

	public void setLoginURLPath(String loginURLPath) {
		this.loginURLPath = loginURLPath;
	}

	public String getCallBack() {
		return callBack;
	}

	public void setCallBack(String callBack) {
		this.callBack = callBack;
	}

	public String getFacebook_apiKey() {
		return facebook_apiKey;
	}

	public void setFacebook_apiKey(String facebook_apiKey) {
		OAuthFilter.facebook_apiKey = facebook_apiKey;
	}

	public String getFacebook_apiSecret() {
		return facebook_apiSecret;
	}

	public void setFacebook_apiSecret(String facebook_apiSecret) {
		OAuthFilter.facebook_apiSecret = facebook_apiSecret;
	}
	
	
}
