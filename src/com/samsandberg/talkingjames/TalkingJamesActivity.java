package com.samsandberg.talkingjames;

import android.app.Activity;
import android.os.Bundle;

public class TalkingJamesActivity extends Activity {
	
	protected Talking talking;
	protected Scene scene;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
        talking	= new Talking(this);
        
        scene = new Scene(this, talking);
        setContentView(scene);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	talking.startTalking();
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	talking.stopTalking();
    }
}