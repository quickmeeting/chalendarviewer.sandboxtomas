package org.ch.calendar.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import org.calendar.json.R;
import org.calendar.json.R.layout;
import org.ch.calendar.service.CalendarConnector;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class Home extends Activity {
    
    CalendarConnector cc;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        
        cc = new CalendarConnector(this);
        
        Button connectBtn            = (Button) findViewById(R.id.button1);
        connectBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                cc.authorize();
            }
        });  
        
        connectBtn = (Button) findViewById(R.id.button2);
          connectBtn.setOnClickListener(new OnClickListener() {
              public void onClick(View v) {
                  try {
                    cc.getEvents();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
              }
          });  
    }
    
   
}