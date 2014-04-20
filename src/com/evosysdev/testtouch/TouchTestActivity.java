package com.evosysdev.testtouch;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    private TouchSurfaceView touchView; // the surface view tracking touches
    private boolean fullscreen;
    
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        fullscreen = false;
        
        // set content
        setContentView(R.layout.activity_touch_test);
        
        // grab touch surface
        touchView = (TouchSurfaceView) this.findViewById(R.id.touchView1);
        
        // start with system UI on
        showSystemUI();
    }
    
    /**
     * Resume touch tracing
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        touchView.resume();
        
        if (fullscreen)
            hideSystemUI();
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
                Toast.makeText(this, "Press back to return...",
                        Toast.LENGTH_SHORT).show();
                
                // start in fullscreen mode
                hideSystemUI();
                fullscreen = true;
                return true;
            case R.id.action_showlegend:
                item.setChecked(!item.isChecked());
                touchView.showLegend(item.isChecked()); // reset touches
                return true;
            case R.id.action_reset:
                touchView.reset(); // reset touches
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public void onBackPressed()
    {
        if (fullscreen)
        {
            fullscreen = false;
            showSystemUI();
        }
        else super.onBackPressed();
    }
    
    @SuppressLint("NewApi")
    private void hideSystemUI()
    {
        // immersive mode for those that support it
        int currentAPI = android.os.Build.VERSION.SDK_INT;
        if (currentAPI >= android.os.Build.VERSION_CODES.KITKAT)
        {
            touchView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        // no immersive support
        else if (currentAPI >= android.os.Build.VERSION_CODES.JELLY_BEAN)
        {
            touchView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
        // legacy
        else
        {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }
    
    @SuppressLint("NewApi")
    private void showSystemUI()
    {
        int currentAPI = android.os.Build.VERSION.SDK_INT;
        if (currentAPI >= android.os.Build.VERSION_CODES.JELLY_BEAN)
        {
            touchView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        // legacy
        else
        {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }
}
