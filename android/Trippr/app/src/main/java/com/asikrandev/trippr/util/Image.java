package com.asikrandev.trippr.util;

/**
 * Created by ricardovegas on 2/4/15.
 */
public class Image {

    private int id;
    private String source;
    private String tags;

    public Image() {};

    public Image(int id, String source, String tags) {
        this.id = id;
        this.source = source;
        this.tags = tags;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}

