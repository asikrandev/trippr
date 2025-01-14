package com.asikrandev.trippr.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;

/**
 * Created by jegasmlm on 2/12/2015.
 */
public class TripprSwipe extends View {

    private static final float ANIMATION_SPEED = 200;

    private ArrayList<Bitmap> list;
    private Context context;

    private int item;
    private int swipeDirection;
    private int minW, minH;
    private int width, height;

    private float pos;
    private float xFinger, initTouch;
    private float aspectRatio;

    private boolean fit;
    private boolean square;

    private onSwipeListener swipeListener;

    private Paint mainPaint;
    private Paint nextPaint;

    private ObjectAnimator anim;



    // Container Activity must implement this interface
    public interface onSwipeListener {
        public void onLike(int position);
        public void onLikeAnimationEnd();
        public void onDislike();
        public void onDislikeAnimationEnd();
        public void onFinished();
    }

    public void setOnSwipeListener(onSwipeListener swipeListener){
        this.swipeListener = swipeListener;
    }

    private void init(){
        pos = 0;
        item = 0;
        swipeDirection = 0;
        getSuggestedMinimum();
        mainPaint = new Paint();
        nextPaint = new Paint();

        nextPaint.setAlpha(0);

        fit = false;
    }

    public TripprSwipe(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TripprSwipe(Context context, ArrayList<Bitmap> list, boolean square) {
        super(context);

        this.context = context;
        this.list  = list;
        this.square = square;

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

        minW = maxH;
        minH = maxW;

        aspectRatio = (float)minH/(float)minW;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int desiredWidth = minW;
        int desiredHeight = minH;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }

        if(square) {
            int size = Math.min(width, height);
            setMeasuredDimension(size, size);
        }else {
            setMeasuredDimension(width, height);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        calculateAlphas();

        canvas.drawBitmap(list.get(item), null, calculateImageBoundaries(list.get(item), 0), mainPaint);
        if(item + 1 < list.size()) {
            canvas.drawBitmap(list.get(item + 1), null, calculateImageBoundaries(list.get(item+1), -swipeDirection), nextPaint);
        }
    }

    private RectF calculateImageBoundaries(Bitmap image, int op){
        int h = image.getHeight();
        int w = image.getWidth();

        float left, top, right, bottom;
        if(fit) {
            if (w < h) {
                left = pos + getWidth() * op;
                top = (getHeight() / 2) - ((h * getWidth()) / (2 * w));
                right = left + getWidth();
                bottom = top + (h * getWidth() / w);
            } else {
                left = pos + (getWidth() / 2 - ((w * getHeight()) / (2 * h))) + getWidth() * op;
                top = 0;
                right = left + (w * getHeight() / h);
                bottom = getHeight();
            }
        }else {
            if (w > h) {
                left = pos + getWidth() * op;
                top = (getHeight() / 2) - ((h * getWidth()) / (2 * w));
                right = left + getWidth();
                bottom = top + (h * getWidth() / w);
            } else {
                left = pos + (getWidth() / 2 - ((w * getHeight()) / (2 * h))) + getWidth() * op;
                top = 0;
                right = left + (w * getHeight() / h);
                bottom = getHeight();
            }
        }

        return new RectF(left, top, right, bottom);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch(motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                xFinger = motionEvent.getX();
                initTouch = motionEvent.getX();
                swipeDirection = 0;
                return true;
            case MotionEvent.ACTION_MOVE:
                pos += motionEvent.getX() - xFinger;

                if(motionEvent.getX() > initTouch) swipeDirection = 1;
                else if(motionEvent.getX() < initTouch)  swipeDirection = -1;
                else swipeDirection = 0;

                xFinger = motionEvent.getX();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_UP:
                if( Math.abs(motionEvent.getX() - initTouch) > getWidth()*0.2){
                     if(swipeDirection == 1) like();
                     else dontLike();
                }else {
                    animate(0);
                }
                break;
            default:
                break;
        }
        invalidate();
        return false;
    }

    private void calculateAlphas(){
        if (Math.abs(pos) > getWidth() / 2) {
            mainPaint.setAlpha(0);
            nextPaint.setAlpha(Math.abs(((int) pos) * 255 / getWidth()) - (255/2));
        } else{
            mainPaint.setAlpha(-(int) (Math.abs(pos) * 255 * 2 / getWidth()) + 255);
            nextPaint.setAlpha(0);

        }
    }

    public void setFit(boolean fit){
        this.fit = fit;
        invalidate();
    }

    public float getPos(){
        return pos;
    }

    public void setPos(float pos){
        this.pos = pos;
        invalidate();
    }

    public void restart(){
        item = 0;
    }

    public void dontLike(){
        swipeDirection = -1;
        animate(-1);
        swipeListener.onDislike();
    }

    public void like(){
        swipeDirection = 1;
        animate(1);
        swipeListener.onLike(item);
    }

    public void animate(int op){
        anim = ObjectAnimator.ofFloat(this, "pos", op*getWidth());
        anim.setInterpolator(new LinearInterpolator());

        if(op != 0) {
            anim.setDuration( (int) ( ( Math.abs(pos) * -ANIMATION_SPEED / getWidth() ) + ANIMATION_SPEED ) );
            final int direction = op;
            anim.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if(direction == 1)
                        swipeListener.onLikeAnimationEnd();
                    else
                        swipeListener.onDislikeAnimationEnd();
                        next();
                        pos = 0;
                        swipeDirection = 0;
                    }
            });
        }else{
            anim.setDuration( (int) ( Math.abs(pos) * ANIMATION_SPEED / getWidth() ) );
        }

        anim.start();
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
