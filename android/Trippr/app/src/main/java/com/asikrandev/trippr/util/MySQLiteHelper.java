package com.asikrandev.trippr.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.asikrandev.trippr.util.Image;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ricardovegas on 2/4/15.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "trippr";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        String CREATE_BOOK_TABLE = "CREATE TABLE image ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "source TEXT, "+
                "tags TEXT )";

        // create books table
        db.execSQL(CREATE_BOOK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS image");

        // create fresh books table
        this.onCreate(db);
    }

    //---------------------------------------------------------------------
    //          OPERATIONS FOR TABLE (addImage, delete, update, getImage)
    //---------------------------------------------------------------------

    //Image table name
    private static final String TABLE_IMAGES = "images";

    //Images Table Columns name
    private static final String KEY_ID = "id";
    private static final String KEY_SOURCE = "source";
    private static final String KEY_TAGS = "tags";

    private static final String[] COLUMNS = {KEY_ID,KEY_SOURCE,KEY_TAGS};

    //Add Class
    public void addImage(Image image){
        //For logging
        Log.d("addImage", image.toString());

        //1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        //2. create ContentValues fo add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_SOURCE, image.getSource());
        values.put(KEY_TAGS, image.getTags());

        //3. insert
        db.insert (TABLE_IMAGES, //table
                    null, //nullColumnHack
                    values); // key/value -> keys = column names / value

        //4. close
        db.close();
    }

    public Image getImage(int id){

        //1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        //2. build query
        Cursor cursor =
                db.query(TABLE_IMAGES, //a. table
                COLUMNS, //b. column names
                "id = ?", //c. selections
                new String[] {String.valueOf(id)}, // d. selections
                null,
                null,
                null,
                null);

        //3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        //4. build image object
        Image image = new Image();
        image.setId(Integer.parseInt(cursor.getString(0)));
        image.setSource(cursor.getString(1));
        image.setTags(cursor.getString(2));

        //Log
        Log.d("getImage("+id+")", image.toString());

        //5. return image
        return image;
    }

    public List<Image> getAllImages() {
        List<Image> images = new LinkedList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_IMAGES;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        Image image = null;
        if (cursor.moveToFirst()) {
            do {
                image = new Image();
                image.setId(Integer.parseInt(cursor.getString(0)));
                image.setSource(cursor.getString(1));
                image.setTags(cursor.getString(2));

                // Add book to books
                images.add(image);
            } while (cursor.moveToNext());
        }

        Log.d("getAllImages()", images.toString());

        // return books
        return images;
    }

}
