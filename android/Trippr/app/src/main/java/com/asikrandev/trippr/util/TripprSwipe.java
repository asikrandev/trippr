package com.asikrandev.trippr.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;

/**
 * Created by jegasmlm on 2/12/2015.
 */
public class TripprSwipe extends View {

    private static final float ANIMATION_SPEED = 200;

    private ArrayList<Image> list;
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
    private Bitmap bm;
    private Bitmap nextBm;


    // Container Activity must implement this interface
    public interface onSwipeListener {
        public void onLike(int position);

        public void onLikeAnimationEnd();

        public void onDislike();

        public void onDislikeAnimationEnd();

        public void onFinished();
    }

    public void setOnSwipeListener(onSwipeListener swipeListener) {
        this.swipeListener = swipeListener;
    }

    private void init() {
        pos = 0;
        setItem(0);
        swipeDirection = 0;
        mainPaint = new Paint();
        nextPaint = new Paint();

        nextPaint.setAlpha(0);

        fit = false;
    }

    public TripprSwipe(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TripprSwipe(Context context, ArrayList<Image> list, boolean square) {
        super(context);

        this.context = context;
        this.list = list;
        this.square = square;

        init();
    }

    private void getSuggestedMinimum() {
        int maxH = 0;
        int maxW = 0;

        Bitmap bm;
        for (int i = 0; i < list.size(); i++) {
            Resources res = getResources();

            int resID = res.getIdentifier(list.get(item).getSource(), "drawable", getContext().getPackageName());
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;

            bm = BitmapFactory.decodeResource(res, resID, options);

            int w = bm.getWidth();
            int h = bm.getHeight();

            if (w > maxW) maxW = w;
            if (h > maxH) maxH = h;
        }

        minW = maxH;
        minH = maxW;

        aspectRatio = (float) minH / (float) minW;
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

        if (square) {
            int size = Math.min(width, height);
            setMeasuredDimension(size, size);
        } else {
            setMeasuredDimension(width, height);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        calculateAlphas();

        canvas.drawBitmap(bm, null, calculateImageBoundaries(bm, 0), mainPaint);
        if (item + 1 < list.size()) {
            canvas.drawBitmap(nextBm, null, calculateImageBoundaries(nextBm, -swipeDirection), nextPaint);
        }
    }

    private Bitmap getBitmap(String source) {
        Resources res = getResources();
        int resID = res.getIdentifier(source, "drawable", getContext().getPackageName());
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        return BitmapFactory.decodeResource(res, resID, options);
    }

    private RectF calculateImageBoundaries(Bitmap image, int op) {
        int h = image.getHeight();
        int w = image.getWidth();

        float left, top, right, bottom;
        if (fit) {
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
        } else {
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

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                xFinger = motionEvent.getX();
                initTouch = motionEvent.getX();
                swipeDirection = 0;
                return true;
            case MotionEvent.ACTION_MOVE:
                pos += motionEvent.getX() - xFinger;

                if (motionEvent.getX() > initTouch) swipeDirection = 1;
                else if (motionEvent.getX() < initTouch) swipeDirection = -1;
                else swipeDirection = 0;

                xFinger = motionEvent.getX();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(motionEvent.getX() - initTouch) > getWidth() * 0.2) {
                    if (swipeDirection == 1) like();
                    else dontLike();
                } else {
                    animate(0);
                }
                break;
            default:
                break;
        }
        invalidate();
        return false;
    }

    private void calculateAlphas() {
        if (Math.abs(pos) > getWidth() / 2) {
            mainPaint.setAlpha(0);
            nextPaint.setAlpha(Math.abs(((int) pos) * 255 / getWidth()) - (255 / 2));
        } else {
            mainPaint.setAlpha(-(int) (Math.abs(pos) * 255 * 2 / getWidth()) + 255);
            nextPaint.setAlpha(0);

        }
    }

    public void setFit(boolean fit) {
        this.fit = fit;
        invalidate();
    }

    public float getPos() {
        return pos;
    }

    public void setPos(float pos) {
        this.pos = pos;
        invalidate();
    }

    public void restart() {
        setItem(0);
    }

    private void setItem(int index) {
        item = index;
        if (item + 1 < list.size())
            nextBm = getBitmap(list.get(index + 1).getSource());
        bm = getBitmap(list.get(index).getSource());
    }

    public void dontLike() {
        swipeDirection = -1;
        animate(-1);
        swipeListener.onDislike();
    }

    public void like() {
        swipeDirection = 1;
        animate(1);
        swipeListener.onLike(item);
    }

    public void animate(int op) {
        anim = ObjectAnimator.ofFloat(this, "pos", op * getWidth());
        anim.setInterpolator(new LinearInterpolator());

        if (op != 0) {
            int duration = (int) ((Math.abs(pos) * -ANIMATION_SPEED / getWidth()) + ANIMATION_SPEED);
            anim.setDuration(duration);
            final int direction = op;
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (direction == 1)
                        swipeListener.onLikeAnimationEnd();
                    else
                        swipeListener.onDislikeAnimationEnd();
                    post(new Runnable() {
                        @Override
                        public void run() {
                            next();
                            pos = 0;
                            swipeDirection = 0;
                        }
                    });
//                    next();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            anim.start();
        } else {
            anim.setDuration((int) (Math.abs(pos) * ANIMATION_SPEED / getWidth()));
            anim.start();
        }
    }

    public void next() {
        if (item + 1 < list.size()) {
            setItem(item + 1);
            invalidate();
        } else swipeListener.onFinished();
    }

    public void loadNewBitmapList(ArrayList<Image> list) {
        this.list = list;
    }

    /*public void recycle(){
        for(int i=0; i<list.size(); i++){
            list.get(i).recycle();
        }
        list = null;
    }*/

}
