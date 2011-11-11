package org.ch.calendar.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicHeader;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;


/**
 * 
 * @author Lorensius W. L. T <lorenz@londatiga.net>
 *
 */
public class CalendarConnector {

    public static final String CLIENT_ID     = "960151117831.apps.googleusercontent.com";
    public static final String CLIENT_SECRET = "1fBedz_8UAKElxNrXJjWZJxC";
    
    private static final String OAUTH_BEGIN         = "https://accounts.google.com/o/oauth2/auth";
    private static final String OAUTH_REDIRECT_URI  = "urn:ietf:wg:oauth:2.0:oob";
    private static final String OAUTH_SCOPE         = "https://www.google.com/calendar/feeds/";
    private static final String OAUTH_RESPONSE_TYPE = "code";
    
    private static final String TAG = "CalendarConnector";
    
    
    private static final String URL_OAUTH = OAUTH_BEGIN + "?client_id=" + CLIENT_ID + 
                                                         "&redirect_uri=" + OAUTH_REDIRECT_URI +
                                                         "&scope=" + OAUTH_SCOPE +
                                                         "&response_type=" + OAUTH_RESPONSE_TYPE;
    
    
    private CalendarSession            mCalendarSession;
    private CalendarConnectorListener  mListener;
    private Context                    mContext;
    
    private ProgressDialog             mProgressDialog;
    private CalendarAuthenticateDialog mAuthDialog;
    
    public CalendarConnector(Context context) {
        Log.v(TAG, "CalendarConnector");
        
        mContext = context;
        mCalendarSession = new CalendarSession(context);
        
        // binds dialog result
        CalendarAuthenticateDialogListener listener = new CalendarAuthenticateDialogListener() {
            
            public void onError(String error) {
                if (mListener != null)
                   mListener.onFail("Authorization failed");                
            }
            
            public void onComplete(String authorizationCode) {
                mCalendarSession.storeAuthorizationCode(authorizationCode);
                try {
                    getEvents();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (mListener != null)
                    mListener.onSuccess(); 
                
            }
        }; 
                
        Log.v(TAG, " url = " + URL_OAUTH);
        mAuthDialog = new CalendarAuthenticateDialog(context, URL_OAUTH, listener);
        
        mProgressDialog = new ProgressDialog(context);
        
        mProgressDialog.setCancelable(false);
    }   
   
	
	public boolean hasAccessToken() throws ClientProtocolException, IOException, JSONException {
		return (mCalendarSession.getAccessToken() == null) ? false : true;
	}
	
	public void setListener(CalendarConnectorListener listener) {
		mListener = listener;
	}
	
	public void authorize() {
		mAuthDialog.show();
	}
	
	public void getEvents() throws IOException, JSONException {
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
       final String url = "https://www.google.com/calendar/feeds/default/allcalendars/full?alt=jsonc";

       HttpGet httpPost = new HttpGet(url);
       
       httpPost.setHeader("Authorization","Bearer " + mCalendarSession.getAccessToken());
       
       HttpResponse response = httpClient.execute(httpPost);
       HttpEntity   entity   = response.getEntity();
        
        
        Log.d(TAG,"Response code is " + response.getStatusLine().getStatusCode());
        
        String response1 = streamToString(entity.getContent()); 
	    
        Log.d(TAG, "HTML response = " + response1);
	}
	
	static public String streamToString(InputStream is) throws IOException {
        String str  = "";
       
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;
           
            try {
                BufferedReader reader  = new BufferedReader(new InputStreamReader(is));
               
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
               
                reader.close();
            } finally {
                is.close();
            }
           
            str = sb.toString();
        }
       
        return str;
    }
}