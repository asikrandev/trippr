package com.asikrandev.trippr.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.asikrandev.trippr.R;

import java.util.ArrayList;

/**
 * Created by jegasmlm on 2/12/2015.
 */
public class TripprSwipe extends View {

    private ArrayList<Bitmap> list;
    private Context context;

    private int minW, minH;
    private int width, height;

    private float pos;
    private float xFinger, initTouch;
    private int item;

    private float hRatio;

    onSwipeListener swipeListener;

    // Container Activity must implement this interface
    public interface onSwipeListener {
        public void onLike(int position);
        public void onFinished();
    }

    public void setOnSwipeListener(onSwipeListener swipeListener){
        this.swipeListener = swipeListener;
    }

    private void init(){
        pos = 0;
        item = 0;
        getSuggestedMinimum();
    }

    public TripprSwipe(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TripprSwipe(Context context, ArrayList<Bitmap> list) {
        super(context);

        this.context = context;
        this.list  = list;

        init();
    }

    private void getSuggestedMinimum(){
        int maxH = 0;
        int maxW = 0;

        for(int i = 0; i < list.size(); i++){
            int w = list.get(i).getWidth();
            int h = list.get(i).getHeight();

            if(w > maxW) maxW = w;
            if(h > maxH) maxH = h;
        }

        minW = maxW;
        minH = maxH;

        Log.d("trippr", minH+ " / " + minW);

        hRatio = (float)minH/(float)minW;

        Log.d("trippr", ""+hRatio);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Try for a width based on our minimum
        int minw = getPaddingLeft() + getPaddingRight() + minW;
        width = resolveSizeAndState(minw, widthMeasureSpec, 1);

        // get as big as it can
        height = resolveSizeAndState((int)(width*hRatio), heightMeasureSpec, 1);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        RectF mainImage = new RectF(
                pos,
                (getHeight()/2) - ((list.get(item).getHeight() * getWidth()/list.get(item).getWidth())/2),
                pos + getWidth(),
                (getHeight()/2) +((list.get(item).getHeight() * getWidth()/list.get(item).getWidth())/2)
        );

        canvas.drawBitmap(list.get(item), null, mainImage, null);
        if(item + 1 < list.size()) {

            RectF nextImageRight = new RectF(
                    pos + getWidth(),
                    (getHeight()/2) - ((list.get(item+1).getHeight() * getWidth()/list.get(item+1).getWidth())/2),
                    pos + 2*getWidth(),
                    (getHeight()/2) + ((list.get(item+1).getHeight() * getWidth()/list.get(item+1).getWidth())/2)
            );

            RectF nextImageLeft = new RectF(
                    pos - getWidth(),
                    (getHeight()/2) - ((list.get(item+1).getHeight() * getWidth()/list.get(item+1).getWidth())/2),
                    pos,
                    (getHeight()/2) +((list.get(item+1).getHeight() * getWidth()/list.get(item+1).getWidth())/2)
            );

            canvas.drawBitmap(list.get(item + 1), null, nextImageRight, null);
            canvas.drawBitmap(list.get(item + 1), null, nextImageLeft, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch(motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                xFinger = motionEvent.getX();
                initTouch = motionEvent.getX();
                return true;
            case MotionEvent.ACTION_MOVE:
                pos += motionEvent.getX() - xFinger;
                xFinger = motionEvent.getX();
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.d("trippr", "CANCEL");
                break;
            case MotionEvent.ACTION_UP:
                if( Math.abs(motionEvent.getX() - initTouch) > getWidth()*0.5){
                     if(motionEvent.getX() > initTouch) like();
                     next();
                }
                pos = 0;
                break;
            default:
                break;
        }
        invalidate();
        return false;
    }

    public void restart(){
        item = 0;
    }

    public void like(){
        swipeListener.onLike(item);
    }

    public void next(){
        if(item + 1 < list.size()) {
            item++;
            invalidate();
        }
        else swipeListener.onFinished();
    }

    public void loadNewBitmapList(ArrayList<Bitmap> list){
        this.list  = list;
    }

    public void recycle(){
        for(int i=0; i<list.size(); i++){
            list.get(i).recycle();
        }
        list = null;
    }

}
