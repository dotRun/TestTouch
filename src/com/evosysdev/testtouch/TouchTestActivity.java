package com.evosysdev.testtouch;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

/**
 * Main activity for touch test app
 * @author taylor
 *
 */
public class TouchTestActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_test);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.touch_test, menu);
        return true;
    }
    
}
