package com.asikrandev.trippr.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jegasmlm on 8/17/2015.
 */
public class ImageLoader extends AsyncTask<String, Void, Bitmap> {

    private ImageLoaderListener imageLoaderListener;

    public void setImageLoaderListener(ImageLoaderListener imageLoaderListener) {
        this.imageLoaderListener = imageLoaderListener;
    }

    @Override
    protected void onPreExecute() {

    }

    protected Bitmap doInBackground(String... args) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(args[0]);
            HttpURLConnection connection  = (HttpURLConnection) url.openConnection();

            InputStream is = connection.getInputStream();
//            final BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            bitmap =  BitmapFactory.decodeStream(is, null, options);
            bitmap =  BitmapFactory.decodeStream(is);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    protected void onPostExecute(Bitmap image) {
        if (imageLoaderListener != null) imageLoaderListener.onResult(image);
    }

    public interface ImageLoaderListener {
        void onResult(Bitmap bitmap);
    }
}
