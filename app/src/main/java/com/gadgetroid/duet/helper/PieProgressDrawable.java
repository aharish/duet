package com.gadgetroid.duet.helper;

/**
 * Created by gadgetroid on 04/03/18.
 */

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

/**
 * A PieProgressDrawable does this:
 * <a href="http://stackoverflow.com/questions/12458476/how-to-create-circular-progress-barpie-chart-like-indicator-android">Circular Progress Bar Android</a>
 */


public class PieProgressDrawable extends Drawable {

    Paint mPaint;
    RectF mBoundsF;
    RectF mInnerBoundsF;
    final float START_ANGLE = 0.f;
    float mDrawTo;
    int mColor;
    float borderWidth;

    public PieProgressDrawable() {
        super();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    /**
     * Set the border width.
     * @param widthDp in dip for the pie border
     */
    public void setBorderWidth(float widthDp, DisplayMetrics dm) {
        borderWidth = widthDp * dm.density;
        mPaint.setStrokeWidth(borderWidth);
    }

    /**
     * @param color you want the pie to be drawn in
     */
    public void setColor(int color) {
        mColor = color;
        mPaint.setColor(color);
    }

    @Override
    public void draw(Canvas canvas) {
        mBoundsF = mInnerBoundsF = new RectF(getBounds());
        // Rotate the canvas around the center of the pie by 90 degrees
        // counter clockwise so the pie stars at 12 o'clock.
        canvas.rotate(-90f, getBounds().centerX(), getBounds().centerY());
        mBoundsF.inset(4,4);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawOval(mBoundsF, mPaint);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(6f);
        mInnerBoundsF.inset(8f, 8f);
        mPaint.setColor(Color.TRANSPARENT);
        canvas.drawOval(mInnerBoundsF, mPaint);
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawArc(mInnerBoundsF, START_ANGLE, mDrawTo, true, mPaint);

        // Draw inner oval and text on top of the pie (or add any other
        // decorations such as a stroke) here..
        // Don't forget to rotate the canvas back if you plan to add text!
        // ...
    }

    @Override
    protected boolean onLevelChange(int level) {
        final float drawTo = START_ANGLE + ((float)360*level)/100f;
        boolean update = drawTo != mDrawTo;
        mDrawTo = drawTo;
        return update;
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        //noinspection WrongConstant
        return mPaint.getAlpha();
    }
}