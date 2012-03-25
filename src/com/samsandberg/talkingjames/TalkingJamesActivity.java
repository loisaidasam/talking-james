package com.samsandberg.talkingjames;

import android.app.Activity;
import android.os.Bundle;

public class TalkingJamesActivity extends Activity {
	
	protected Scene scene;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        scene = new Scene(this);
        setContentView(scene);
    }
}