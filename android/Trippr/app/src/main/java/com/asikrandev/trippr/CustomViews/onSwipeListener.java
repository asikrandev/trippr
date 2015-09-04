package com.asikrandev.trippr.CustomViews;

/**
 * Created by jegasmlm on 8/17/2015.
 */
public interface OnSwipeListener {
    void onLike(int position);
    void onLikeAnimationEnd();
    void onDislike();
    void onDislikeAnimationEnd();
    void onFinished();
}