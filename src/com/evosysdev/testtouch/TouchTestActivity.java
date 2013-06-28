package com.evosysdev.testtouch;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Main activity for touch test app
 * 
 * @author tajobe
 * 
 */
public class TouchTestActivity extends Activity
{
    // message to send intent for fullscreen
    private static final String FULLSCREEN_MESSAGE = "FULLSCREEN";
    
    private TouchSurfaceView touchView; // the surface view tracking touches
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        // check for fullscreen flag
        if (this.getIntent().getBooleanExtra(FULLSCREEN_MESSAGE, false))
        {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        
        // set content
        setContentView(R.layout.activity_touch_test);
        
        touchView = (TouchSurfaceView) this.findViewById(R.id.touchView1); // grab touch surface
    }
    
    /**
     * Resume touch tracing
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        touchView.resume();
    }
    
    /**
     * Pause touch tracing
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        touchView.pause();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.touch_test, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle item selection
        switch (item.getItemId())
        {
            case R.id.action_fullscreen:
                // hint at how to exit fullscreen
                Toast.makeText(this, "Press back to return...", Toast.LENGTH_SHORT).show();
                
                // start in fullscreen mode
                Intent fullscreenIntent = new Intent(this, TouchTestActivity.class);
                fullscreenIntent.putExtra(FULLSCREEN_MESSAGE, true);
                startActivity(fullscreenIntent);
                return true;
            case R.id.action_reset:
                touchView.reset(); // reset touches
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
