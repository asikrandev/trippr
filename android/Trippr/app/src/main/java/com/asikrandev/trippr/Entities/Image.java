package com.asikrandev.trippr.Entities;

import android.graphics.Bitmap;

/**
 * Created by ricardovegas on 2/4/15.
 */
public class Image {

    private int id;
    private String src;
    private Bitmap bitmap;
    private String[] tags;

    public Image() {};

    public Image(int id, String src, String tags) {
        this.id = id;
        this.src = src;
        this.tags = new String[1];
        this.tags[0] = tags;
        bitmap = null;
    }

    public Image(int id, String src, String[] tags) {
        this.id = id;
        this.src = src;
        this.tags = tags;
        bitmap = null;
    }

    public Image(int id, String src, Bitmap bitmap, String[] tags) {
        this.id = id;
        this.src = src;
        this.bitmap = bitmap;
        this.tags = tags;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }


    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }
}

