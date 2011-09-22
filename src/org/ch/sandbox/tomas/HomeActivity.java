package org.ch.sandbox.tomas;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Main Activity for this test 
 *
 */
public class HomeActivity extends Activity {
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Set up the URL and the object that will handle the connection:
        URL feedUrl;
        try {
            feedUrl = new URL("http://www.google.com/calendar/feeds/optaresolutions.com_2d3933313331343032383836%40resource.calendar.google.com/private/full");
            CalendarService myService = new CalendarService("exampleCo-exampleApp-12");
            myService.setUserCredentials("tdias@optaresolutions.com", "guilbert1012"); 
    
            // Send the request and receive the response:
            CalendarEventFeed myFeed = myService.getFeed(feedUrl, CalendarEventFeed.class);
            
            Log.i("tomas", "My feed is "+ myFeed.getEntries().size());            
        } catch (AuthenticationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ServiceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}