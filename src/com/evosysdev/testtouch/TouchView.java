package com.evosysdev.testtouch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Simple View to draw touches
 * 
 * @author taylor
 * 
 */
public class TouchView extends View
{
    private static final int[] TOUCH_COLORS =
    { Color.WHITE, Color.BLUE + 0x222200, Color.GREEN + 0x220022, Color.YELLOW + 0x22, Color.RED + 0x2222 };
    private static final int HISTORY_LEN = 5000;
    
    private Touch[] touches; // array of recorded touches
    private int i; // current touch index
    private Paint paint; // paint to avoid constantly reallocating paint objects
    
    /**
     * Construct TouchView
     * 
     * @param context
     *            context of this view
     * @param attrs
     *            attributes of view
     */
    public TouchView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        touches = new Touch[HISTORY_LEN];
        paint = new Paint();
        paint.setStyle(Style.FILL_AND_STROKE);
        paint.setTextSize(20);
    }
    
    /**
     * Draw touches
     * 
     * View draw callback called when invalidated
     * 
     * @param c
     *            canvas to draw on
     */
    @Override
    public void onDraw(Canvas c)
    {
        // draw finger labels
        int labelsTop = c.getHeight() - 30;
        int labelsBot = c.getHeight() - 10;
        float labelSpacing = 35 + (paint.getTextSize() * paint.getTextScaleX() * 3);
        for (int i = 0; i < TOUCH_COLORS.length; i++)
        {
            float spacing = i * labelSpacing;
            paint.setColor(TOUCH_COLORS[i]);
            c.drawRect(10 + spacing, labelsTop, 30 + spacing, labelsBot, paint);
            paint.setColor(Color.WHITE);
            c.drawText("F " + (i + 1), 35 + spacing, labelsBot, paint);
        }
        
        // draw touches
        for (Touch touch : touches)
        {
            // have not filled past here, skip rest of loop
            if (touch == null)
                break;
            
            paint.setColor(TOUCH_COLORS[touch.index]);
            paint.setStrokeWidth((float) Math.log(touch.pressure * 20f));
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
        
        // only handle up to 5 pointers
        if (pointers > 5)
            pointers = 5;
        
        // we only care about move events and currently handling up to 5 fingers
        if (event.getActionMasked() == MotionEvent.ACTION_MOVE)
        {
            // go through each pointer in the event
            for (int pointer = 0; pointer < pointers; pointer++)
            {
                // get and add history since last touch event
                for (int g = 0; g < event.getHistorySize(); g++)
                {
                    addTouch(pointer, event.getHistoricalX(pointer, g),
                            event.getHistoricalY(pointer, g),
                            event.getHistoricalPressure(pointer, g));
                }
                
                // add pointer touch event
                addTouch(pointer, event.getX(pointer), event.getY(pointer),
                        event.getPressure(pointer));
            }
        }
        
        return true;
    }
    
    /**
     * Record a touch
     * 
     * @param x
     *            x coord of touch
     * @param y
     *            y coord of touch
     * @param pressure
     *            pressure of touch
     */
    public void addTouch(int index, float x, float y, float pressure)
    {
        // add touch and increment index
        touches[i++] = new Touch(index, x, y, pressure);
        
        // wrap around if we have reached the end of our array
        if (i == touches.length)
            i = 0;
        
        this.invalidate(); // invalidate view to force a redraw
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
         *            index of touch
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
