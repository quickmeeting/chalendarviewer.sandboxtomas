package org.ch.calendar.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

/**
 * Manage access token and user name. Uses shared preferences to store access token
 * and user name.
 * 
 * @author Lorensius W. L T <lorenz@londatiga.net>
 *
 */
public class CalendarSession {
	private SharedPreferences sharedPref;
	private Editor editor;
	
	private static final String SHARED = "Calendar_Preferences";
	private static final String AUTHORIZATION_CODE = "authorization_code";
	private static final String ACCESS_TOKEN = "access_token";
	
	private static final String TAG = "CalendarSession";
	
	public CalendarSession(Context context) {
		sharedPref = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
		editor = sharedPref.edit();
	}
	
	/**
	 * Save access token and user name
	 * 
	 * @param authCode Authorization Code

	 */
	public void storeAuthorizationCode(String authCode) {
	    Log.d(TAG,"storeAccessToken: " + authCode);
		editor.putString(AUTHORIZATION_CODE, authCode);		
		editor.commit();
	}
	
	/**
	 * Reset access token and user name
	 */
	public void resetAccessToken() {
		editor.putString(ACCESS_TOKEN, null);
		editor.commit();
	}
	
		
	/**
	 * Get access token
	 * 
	 * @return Access token
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws JSONException 
	 */
	public String getAccessToken() throws ClientProtocolException, IOException, JSONException {
		String authCode = sharedPref.getString(AUTHORIZATION_CODE, null);
		
		HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

        DefaultHttpClient client = new DefaultHttpClient();

        SchemeRegistry registry = new SchemeRegistry();
        SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
        socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
        registry.register(new Scheme("https", socketFactory, 443));
        SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
        
        
        DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());

        // Set verifier     
        HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

       // Example send http request
       final String url = "https://accounts.google.com/o/oauth2/token";

       HttpPost httpPost = new HttpPost(url);
       
       // Add your data
       List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
       nameValuePairs.add(new BasicNameValuePair("client_id", CalendarConnector.CLIENT_ID));
       nameValuePairs.add(new BasicNameValuePair("client_secret", CalendarConnector.CLIENT_SECRET));
       nameValuePairs.add(new BasicNameValuePair("code", authCode));
       nameValuePairs.add(new BasicNameValuePair("redirect_uri", "urn:ietf:wg:oauth:2.0:oob"));
       nameValuePairs.add(new BasicNameValuePair("grant_type", "authorization_code"));
       
       httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

       // Execute HTTP Post Request
       HttpResponse response = httpClient.execute(httpPost);
       HttpEntity   entity   = response.getEntity();
        
        
        Log.d(TAG,"Response code is " + response.getStatusLine().getStatusCode());
        
        String response1 = CalendarConnector.streamToString(entity.getContent()); 
        
        Log.d(TAG, "HTML response = " + response1);
        
        JSONObject jsonObj     = (JSONObject) new JSONTokener(response1).nextValue();
        return jsonObj.getString("access_token");        
		
	}
}