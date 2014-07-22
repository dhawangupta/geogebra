package geogebra.common.move.ggtapi.operations;

import geogebra.common.main.App;
import geogebra.common.move.ggtapi.events.LogOutEvent;
import geogebra.common.move.ggtapi.events.LoginAttemptEvent;
import geogebra.common.move.ggtapi.events.LoginEvent;
import geogebra.common.move.ggtapi.models.AuthenticationModel;
import geogebra.common.move.ggtapi.models.GeoGebraTubeAPI;
import geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import geogebra.common.move.operations.BaseOperation;
import geogebra.common.move.views.EventRenderable;

/**
 * @author stefan
 * 
 * Operational class for login functionality
 *
 */
public abstract class LogInOperation extends BaseOperation<EventRenderable> {

	@Override
	public AuthenticationModel getModel() {
		return (AuthenticationModel) super.getModel();
	}
	
	/**
	 * @return the user name from the storage
	 */
	public String getUserName() {
		return getModel().getUserName();
	}

	/**
	 * @return boolean indicating that the user is already logged in.
	 */
	public boolean isLoggedIn() {
		return getModel().isLoggedIn();
	}
	
	/**
	 * Reads the stored login token from the storage and sends a request to the API to authorize the token.
	 * On successful login, the user information will be stored in the user object of the authorization model
	 */
	public void performTokenLogin() {
		String token = getModel().getLoginToken();
		if (token != null) {
			App.debug("LTOKEN found in model");
			performTokenLogin(token, true);
		}
	}
	
	/**
	 * Sends a request to the API to authorize the specified token
	 * On successful login, the user information will be stored in the user object of the authorization model
	 * The API call is executed in an own thread to keep the GUI responsive. 
	 * The start of the login attempt will be indicated by sending an {@link LoginAttemptEvent} on the GUI thread.
	 * When the login attempt is finished (successful or not), a {@link LoginEvent} will be sent
	 * 
	 * @param token The Login token to authorize
	 * @param automatic If the login is triggered automatically or by the user. This information will be provided
	 * 					in the Login Event. 
	 */
	public void performTokenLogin(String token, boolean automatic) {
		doPerformTokenLogin(new GeoGebraTubeUser(token), automatic);
	}
	
	public boolean performCookieLogin(String cookie) {
		return doPerformTokenLogin(new GeoGebraTubeUser(null, cookie), true);
	}
	/**
	 * Performs the API call to authorize the token.
	 * @param token the token to authorize
	 * @param automatic If the login is triggered automatically or by the user. This information will be provided
	 * 					in the Login Event. 
	 */
	protected boolean doPerformTokenLogin(GeoGebraTubeUser user , boolean automatic) {
		GeoGebraTubeAPI api = getGeoGebraTubeAPI();
		

		App.debug("Sending call to GeoGebraTube API to authorize the login token...");

		// Trigger an event to signal the login attempt
		onEvent(new LoginAttemptEvent(user));
		

		// Send API request to check if the token is valid
		int result = api.authorizeUser(user);
		if (result == GeoGebraTubeAPI.LOGIN_TOKEN_VALID) {
			
			App.debug("The login token was authorized successfully");

			// Trigger event to signal successful login
			onEvent(new LoginEvent(user, true, automatic));
			return true;
		}
		if (result == GeoGebraTubeAPI.LOGIN_REQUEST_FAILED) {
			App.error("The call to the GeoGebraTubeAPI failed!");
		} else {
			App.debug("The login token is invalid");
		}

		// Trigger event to signal unsuccessful login
		onEvent(new LoginEvent(user, false, automatic));
		return false;
	}
	
	/**
	 * Handle the logout
	 */
	public void performLogOut() {
		onEvent(new LogOutEvent());
	}
	
	/**
	 * @return An instance of the GeoGebraTubeAPI
	 */
	public abstract GeoGebraTubeAPI getGeoGebraTubeAPI();
	

	/**
	 * @param languageCode The code of the current user language. This code will be used as URL parameter
	 * @return The URL to the GeoGebraTube Login page including params for the client identification
	 * and the expiration time.
	 */
	public String getLoginURL(String languageCode) {
		return "http://www.geogebratube.org/user/login" 
				+ "/caller/"+getURLLoginCaller()
				+"/expiration/"+getURLTokenExpirationMinutes()
				+"/clientinfo/"+getURLClientInfo()
				+"/?lang="+languageCode;
	}
	
	/**
	 * @return The name of the caller of the login page in GeoGebraTube.
	 * This is used to show different layouts of the login page. Currently supported callers are: desktop, web, touch
	 */
	protected abstract String getURLLoginCaller();
	
	/**
	 * @return the Expiration time of the login token in minutes.
	 * the default implementation returns 129600 = 90 days. 
	 */
	protected String getURLTokenExpirationMinutes() {
		return "129600"; // = 90 days
	}
	
	/**
	 * @return The client information string for the login token. This String is stored in the GeoGebraTube database.
	 * The returned string must be a valid URL encoded String. (use URLEncoder.encode). 
	 */
	protected abstract String getURLClientInfo();
}
