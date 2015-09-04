package com.asikrandev.trippr.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.asikrandev.trippr.Entities.Image;
import com.asikrandev.trippr.Entities.PicsResponse;
import com.google.gson.Gson;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by jegasmlm on 9/2/2015.
 */
public class PicsHelper extends AsyncTask<String, Void, ArrayList<Image>> {

    private static int indexCounter = 0;
    private final Context context;
    private ArrayList<Image> images;
    private PicsResponse picsResponse;
    private ArrayList<PicsResponse.ImageResponse> randomPics;
    public PicsHelperListener picsHelperListener;

    public void setPicsHelperListener(PicsHelperListener picsHelperListener) {
        this.picsHelperListener = picsHelperListener;
    }

    public PicsHelper(Context context) {
        this.context = context;
    }

    private ArrayList<Image> saveNewPics(PicsResponse picsResponse) {

        ArrayList< PicsResponse.ImageResponse > subSet = (ArrayList< PicsResponse.ImageResponse >) picsResponse.pics.clone();
        Collections.shuffle( subSet );
        Random random = new Random();
        int roundCount = random.nextInt( ( 10 - 4 ) + 1 ) + 5;
        randomPics = new ArrayList<>( subSet.subList( 0, roundCount ) );

        ArrayList<Image> images = new ArrayList<>();
        int i = 0;
        for (i=0; i< randomPics.size(); i++) {
            String FILENAME = "pic_"+ (i);

            Bitmap bitmap = null;
            try {
                URL url = new URL(randomPics.get(i).src);
                HttpURLConnection connection  = (HttpURLConnection) url.openConnection();
                InputStream is = connection.getInputStream();
                bitmap =  BitmapFactory.decodeStream(is);

                String tags[] = randomPics.get(i).tags.split(" ");
                images.add(new Image(i, "pic_" + (i), bitmap, tags));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        indexCounter = i;

        return images;
    }

    @Override
    protected ArrayList<Image> doInBackground(String... params) {
        Gson gson = new Gson();
        picsResponse = gson.fromJson(params[0], PicsResponse.class);
        return saveNewPics(picsResponse);
    }

    @Override
    protected void onPostExecute(ArrayList<Image> images) {
        if(picsHelperListener != null) picsHelperListener.onResult(images);
    }

    public interface PicsHelperListener {
        void onResult(ArrayList<Image> images);
    }
}
