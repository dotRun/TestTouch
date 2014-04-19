package com.evosysdev.testtouch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Paint.Style;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Simple View to draw touches
 * 
 * @author tajobe
 * 
 */
public class TouchSurfaceView extends SurfaceView implements Runnable
{
    private static final int HISTORY_LEN, FRAME_TIME;
    private static final String TAG;
    
    static
    {
        HISTORY_LEN = 10000; // number of touch points to keep in history
        FRAME_TIME = 20; // length of time(ms) for a frame(between draws)
        TAG = "Drawing"; // log tag/thread name
    }
    
    private Touch[] touches; // array of recorded touches
    private int i, // current touch index
            colors[]; // array of colors for drawing
    private boolean showLegend; // should we display color legend
    private volatile boolean updated; // have there been touches since last draw
    
    private Thread drawing; // drawing thread
    private SurfaceHolder surface; // drawing surface
    private Paint paint; // paint to avoid constantly reallocating paint objects
    
    /**
     * Construct TouchView
     * 
     * @param context
     *            context of this view
     * @param attrs
     *            attributes of view
     */
    public TouchSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        
        // create touch history array
        touches = new Touch[HISTORY_LEN];
        
        // init colors array(need non-null)
        colors = new int[0];
        
        // default legend to on
        showLegend = true;
        
        // create paint
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Style.FILL_AND_STROKE);
        paint.setTextSize(20);
        
        // get surface holder
        surface = this.getHolder();
    }
    
    /**
     * Resume drawing
     */
    public void resume()
    {
        // only start drawing if we aren't already
        if (drawing == null)
        {
            updated = true;
            drawing = new Thread(this, TAG);
            drawing.start();
        }
    }
    
    /**
     * Pause drawing
     */
    public void pause()
    {
        // stop if we are drawing
        if (drawing != null)
        {
            // interrupt drawing thread
            drawing.interrupt();
            
            try
            {
                drawing.join(); // wait for thread to die
                drawing = null; // nullify for later
            }
            catch (InterruptedException e)
            {
                Log.w(TAG, "Error pausing thread!", e);
            }
        }
    }
    
    /**
     * Run drawing thread
     */
    @Override
    public void run()
    {
        Canvas c = null;
        long time = 0, took = 0;
        
        try
        {
            while (!drawing.isInterrupted())
            {
                time = SystemClock.currentThreadTimeMillis(); // frame start
                                                              // time
                
                // ensure we have a valid surface to draw to and avoid
                // unnecessary
                // draws by only drawing if something has changed
                if (surface.getSurface().isValid() && updated)
                {
                    c = surface.lockCanvas(); // grab canvas
                    drawTouches(c); // draw
                    updated = false;
                    surface.unlockCanvasAndPost(c); // inform we are done with
                                                    // canvas
                }
                
                took = SystemClock.currentThreadTimeMillis() - time; // length
                                                                     // of frame
                
                // took less time than expected for frame, sleep remaining time
                if (took < FRAME_TIME) Thread.sleep(FRAME_TIME - took);
            }
        }
        catch (InterruptedException e)
        {
            // expected on pause if in sleep
        }
    }
    
    /**
     * Draw touches
     * 
     * View draw callback called when invalidated
     * 
     * @param c
     *            canvas to draw on
     */
    private void drawTouches(Canvas c)
    {
        // clear canvas
        c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        
        // draw color legend
        if (showLegend)
        {
            float labelHeight = 35;
            float labelsTop = (c.getHeight() / 2)
                    - ((labelHeight * colors.length) / 2);
            paint.setStrokeWidth(0);
            for (int i = 0; i < colors.length; i++)
            {
                float y = labelsTop + i * labelHeight;
                paint.setColor(colors[i]);
                c.drawRect(10, y, 40, y + 30, paint);
                paint.setColor(Color.WHITE);
                c.drawText("F" + (i + 1), 45, y + 30, paint);
            }
        }
        
        // draw touches
        paint.setStrokeWidth(2);
        for (Touch touch : touches)
        {
            // have not filled past here, skip rest of loop
            if (touch == null) break;
            
            paint.setColor(colors[touch.index]);
            c.drawPoint(touch.x, touch.y, paint);
        }
    }
    
    /**
     * Handle touch event and add touch point
     */
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int pointers = event.getPointerCount();
        
        // update pointers/colors for number of touches
        updatePointerColors(pointers);
        
        // we only care about move events and currently handling up to 5 fingers
        if (event.getActionMasked() == MotionEvent.ACTION_MOVE)
        {
            // go through each pointer in the event
            for (int pointer = 0; pointer < pointers; pointer++)
            {
                // get and add history since last touch event
                for (int g = 0; g < event.getHistorySize(); g++)
                {
                    addTouch(pointer,
                            event.getHistoricalX(pointer, g),
                            event.getHistoricalY(pointer, g),
                            event.getHistoricalPressure(pointer, g));
                }
                
                // add pointer touch event
                addTouch(pointer,
                        event.getX(pointer),
                        event.getY(pointer),
                        event.getPressure(pointer));
            }
        }
        updated = true;
        
        return true;
    }
    
    /**
     * Update colors when we have more pointers
     * 
     * @param pointers
     *            number of pointers on event
     */
    private void updatePointerColors(int pointers)
    {
        if (colors.length < pointers)
        {
            colors = new int[pointers];
            
            // hsv "heatmap" colors for touches
            float[] hsv =
            {
                    0, 1, 1
            };
            float step = 300f / pointers;
            
            // create "heatmap"
            for (int i = 0; i < pointers; i++)
            {
                hsv[0] = 60 + step * i;
                colors[i] = Color.HSVToColor(hsv);
            }
        }
    }
    
    /**
     * Record a touch
     * 
     * @param index
     *            touch index(pointer)
     * @param x
     *            x coord of touch
     * @param y
     *            y coord of touch
     * @param pressure
     *            pressure of touch
     */
    private void addTouch(int index, float x, float y, float pressure)
    {
        // add touch and increment index
        touches[i++] = new Touch(index, x, y, pressure);
        
        // wrap around if we have reached the end of our array
        if (i == touches.length) i = 0;
    }
    
    /**
     * Reset touches
     */
    public void reset()
    {
        touches = new Touch[HISTORY_LEN]; // reset touches
        i = 0; // reset index
        colors = new int[0]; // reset colors
        updated = true; // inform we have updated
    }
    
    /**
     * Set if we should be showing the legend or not
     * 
     * @param showLegend
     *            should legend be shown
     */
    public void showLegend(boolean showLegend)
    {
        this.showLegend = showLegend;
        updated = true;
    }
    
    /**
     * Hold touch data
     * 
     * @author taylor
     * 
     */
    class Touch
    {
        int index;
        float x, y, pressure;
        
        /**
         * Construct touch data
         * 
         * @param index
         *            index of touch(pointer)
         * @param x
         *            x coord of touch
         * @param y
         *            y coord of touch
         * @param pressure
         *            pressure of touch
         */
        Touch(int index, float x, float y, float pressure)
        {
            this.index = index;
            this.x = x;
            this.y = y;
            this.pressure = pressure;
        }
    }
}
