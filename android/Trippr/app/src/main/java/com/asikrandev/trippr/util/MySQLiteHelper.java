package com.asikrandev.trippr.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.asikrandev.trippr.util.Image;

import java.util.ArrayList;
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
    private static final String TABLE_IMAGES = "image";

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

    public ArrayList<Image> getAllImages() {
        ArrayList<Image> images = new ArrayList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_IMAGES;
        //String query = "SELECT  * FROM " + TABLE_IMAGES + " ORDER BY RANDOM() LIMIT 10";

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

    public void loadDB(){
        // add Images
        addImage(new Image(1,"img01","peace peaceful lights night city quiet clubs buildings traffic "));
        addImage(new Image(2,"img02","peace peaceful sun light clouds air quiet serenity blue colors sunset float smooth sunlight landscape  "));
        addImage(new Image(3,"img03","work peace wood electronic laptop drink alone yellow table write "));
        addImage(new Image(4,"img04","wood brown hard stuck order old steps  "));
        addImage(new Image(5,"img05","Buildings city landscape colors people metropolis high lakes skyscrapers"));
        addImage(new Image(6,"img06","road sun desert travel pacific quiet long brown "));
        addImage(new Image(7,"img07","light grass quiet colors green sunset sun warm field"));
        addImage(new Image(8,"img08","beach sea pier wood landscape sky beautiful  deep hot sunlight "));
        addImage(new Image(9,"img09","road long mountains travel rough warm"));
        addImage(new Image(10,"img10","rough cars gray hard stuck old brand "));
        addImage(new Image(11,"img11","tools work iron steel wood brown dirt "));
        addImage(new Image(12,"img12","blue purple lights defocus photography party night clubs"));
        addImage(new Image(13,"img13","mess wine drink brands brown box disorder "));
        addImage(new Image(14,"img14","yellow blonde quiet trees park sidewalk people walk peaceful wind"));
        addImage(new Image(15,"img15","snow white park road winter trees silent alone cold"));
        addImage(new Image(16,"img16","city people streets buildings  windows modern old order"));
        addImage(new Image(17,"img17","food salad restaurant eat plate delight dinner lunch meat meal"));
        addImage(new Image(18,"img18","animals landscape horse green field grass peaceful"));
        addImage(new Image(19,"img19","river cascade water jungle mountain high plants trees green white weather "));
        addImage(new Image(20,"img20","wood backyard leaves brown wind "));
        addImage(new Image(21,"img21","menu restaurants food meals fruits table drink order tea"));
        addImage(new Image(22,"img22","road house trees mountain warm sunset west planting farm"));
        addImage(new Image(23,"img23","drink money glass ice cold america liquor bar friend"));
        addImage(new Image(24,"img24","party colors balloons house neighborhood peace kids cake"));
        addImage(new Image(25,"img25","wild tiger yellow look serious jungle zoo animal feline cat"));
        addImage(new Image(26,"img26","peaceful night candles light fire yellow steel wish"));
        addImage(new Image(27,"img27","party club night drink alcohol lights dance music bar"));
        addImage(new Image(28,"img28","sand beach stones brown sun warm"));
        addImage(new Image(29,"img29","desert sand hot sun warm alone dust "));
        addImage(new Image(30,"img30","beach sand water cold clothes blue salt"));
        addImage(new Image(31,"img31","library read book mess study write room music desk"));
        addImage(new Image(32,"img32","train wood trees road river lake mountain forest"));
        addImage(new Image(33,"img33","stairs castle dark up bricks gray alone"));
        addImage(new Image(34,"img34","cars speed numbers drive fuel old classic"));
        addImage(new Image(35,"img35","dj party music club dance electro bass night city people enjoy "));
        addImage(new Image(36,"img36","history legend town kingdom shield keys armour kings fenix"));
        addImage(new Image(37,"img37","egypt pyramid sun hot ocean sea desert landscape"));
        addImage(new Image(38,"img38","mountain clouds sky road trees ground town"));
        addImage(new Image(39,"img39","history old museum city town wonderful beautiful temple structure architecture"));
        addImage(new Image(40,"img40","lake water mountain landscape forest peaceful beautiful sunset sea town"));
        addImage(new Image(41,"img41","snow cold white fog ski weather sports clouds high "));
        addImage(new Image(42,"img42","vegetables healthy fruit juice home house ingredients food"));
        addImage(new Image(43,"img43","food rapid hamburger bread meal meat vegetables cheese delicious flavors"));
    }

}
