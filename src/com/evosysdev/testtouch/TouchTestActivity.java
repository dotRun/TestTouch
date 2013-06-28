package com.evosysdev.testtouch;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Main activity for touch test app
 * @author tajobe
 *
 */
public class TouchTestActivity extends Activity
{
    // the surface view tracking touches
    private TouchSurfaceView touchView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_test);
        
        // grab touch surface
        touchView = (TouchSurfaceView) this.findViewById(R.id.touchView1);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_reset:
                touchView.reset(); // reset touches
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
}
