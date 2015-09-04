package com.asikrandev.trippr.Entities;

import java.util.ArrayList;

/**
 * Created by jegasmlm on 8/27/2015.
 */
public class PicsResponse {
    public double date;
    public ArrayList<ImageResponse> pics;

    public class ImageResponse {
        public String src;
        public String tags;
    }
}
