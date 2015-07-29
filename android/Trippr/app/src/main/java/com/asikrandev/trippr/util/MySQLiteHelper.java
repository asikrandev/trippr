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
    public static final String DATABASE_NAME = "trippr";

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
        addImage(new Image(1,"img01","lights night city clubs buildings traffic "));
        addImage(new Image(2,"img02","work technology computer asia internet "));
        addImage(new Image(3,"img03","metropolis city downtown sunny people high skyscrapers hassle "));
        addImage(new Image(4,"img04","wood park hard study old steps wooden bench museum art "));
        addImage(new Image(5,"img05","play game ball glove baseball sport stadium teams "));
        addImage(new Image(6,"img06","road sun desert travel pacific quiet long brown drive car rental "));
        addImage(new Image(7,"img07","woods sunny park grass quiet sunset field cold eco"));
        addImage(new Image(8,"img08","beach near sea pier beautiful warm walk "));
        addImage(new Image(9,"img09","road mountains car drive"));
        addImage(new Image(10,"img10","cars old sleek retro oldschool "));
        addImage(new Image(11,"img11","tools work iron steel wood "));
        addImage(new Image(12,"img12","party night clubs drinking gay"));
        addImage(new Image(13,"img13","mess wine drink brands brown box disorder "));
        addImage(new Image(14,"img14","wine cork plantation aged old retro drink tasting "));
        addImage(new Image(15,"img15","park road winter trees cold calm"));
        addImage(new Image(16,"img16","city skyscrapers metropolis nyc stores buy"));
        addImage(new Image(17,"img17","salad restaurant pasta luxury expensive"));
        addImage(new Image(18,"img18","animals zoo peaceful big open"));
        addImage(new Image(19,"img19","waterfall trees nature park flora wild rafting "));
        addImage(new Image(20,"img20","home house autumn cold leaves damp nature calm"));
        addImage(new Image(21,"img21","breakfast tea restaurant nice food juice eggs"));
        addImage(new Image(22,"img22","road drive car fast open calm nature america"));
        addImage(new Image(23,"img23","drink money glass ice cold america liquor bar "));
        addImage(new Image(24,"img24","party balloons house neighborhood peace kids children"));
        addImage(new Image(25,"img25","tiger lion zoo wildnerness forest jungle"));
        addImage(new Image(26,"img26","candles peaceful festival religion calm"));
        addImage(new Image(27,"img27","drinks party club nighttime locals"));
        addImage(new Image(28,"img28","sand beach sunny warm coast relax"));
        addImage(new Image(29,"img29","desert hot summer sand camels "));
        addImage(new Image(30,"img30","clothes sand waves relax women shore"));
        addImage(new Image(31,"img31","library read book study room music desk smart"));
        addImage(new Image(32,"img32","train wood trees road river lake mountain forest"));
        addImage(new Image(33,"img33","stairs castle dark bricks cold old age alone"));
        addImage(new Image(34,"img34","cars speed drive fuel old classic modern"));
        addImage(new Image(35,"img35","party music club dance electro bass night city modern "));
        addImage(new Image(36,"img36","history legend town kingdom shield armour kings "));
        addImage(new Image(37,"img37","egypt pyramid sun hot ocean sea desert landscape"));
        addImage(new Image(38,"img38","mountain clouds sky road trees ground town"));
        addImage(new Image(39,"img39","history old museum city town wonderful beautiful temple structure architecture"));
        addImage(new Image(40,"img40","lake water mountain landscape forest peaceful beautiful sunset sea town"));
        addImage(new Image(41,"img41","snow cold white fog ski weather sports clouds high "));
        addImage(new Image(42,"img42","vegetables healthy fruit juice home house ingredients food"));
        addImage(new Image(43,"img43","food rapid hamburger bread meal meat vegetables cheese delicious flavors"));
        addImage(new Image(44,"img44","order stuff personality trip men modern shopping"));
        addImage(new Image(45,"img45","shoes women walk dress gala fancy modern cloths white"));
        addImage(new Image(46,"img46","fork eat food dark party friends quiet alone restaurant"));
        addImage(new Image(47,"img47","computer electronics technology order build mechanics "));
        addImage(new Image(48,"img48","city old cars luxury street avenue walk"));
        addImage(new Image(49,"img49","door house old sidewalk historical"));
        addImage(new Image(50,"img50","music old disc play dance enjoy classic"));
        addImage(new Image(51,"img51","table coffee restaurant quiet tea enjoy share"));
        addImage(new Image(52,"img52","sign rescue alarm warning path guide tower light"));
        addImage(new Image(53,"img53","party lights night fun dance movement people photograph"));
        addImage(new Image(54,"img54","coffee calm internet asia alone enjoy hot"));
        addImage(new Image(55,"img55","party festival park pier ocean people walk sun hot heat vacation shops sea"));
        addImage(new Image(56,"img56","game football team players field soccer stadium tournament "));
        addImage(new Image(57,"img57","blue serenity calm movement slow peaceful sea medusa nature fish ocean "));
        addImage(new Image(58,"img58","woods snow trees silent alone green quiet fog natural scary"));
        addImage(new Image(59,"img59","mountain sky landscape green high clouds natural"));
        addImage(new Image(60,"img60","old history bridge alone religion castle statue"));
        addImage(new Image(61,"img61","sky stars space blue beautiful lights night"));
        addImage(new Image(62,"img62","pool big water cold sea storm"));
        addImage(new Image(63,"img63","shop cloths spend gift shirts t-shirts store shopping sale boutique"));
        addImage(new Image(64,"img64","photograph camera picture awesome job hobie legend classic"));
        addImage(new Image(65,"img65","garden natural small plants green"));
        addImage(new Image(66,"img66","music home studio work decoration room computer"));
        addImage(new Image(67,"img67","station train travel trip wait modern future movement speed day outside"));
        addImage(new Image(68,"img68","city center main street modern living drive movement"));
        addImage(new Image(69,"img69","fruit orange tree natural fresh juice"));
        addImage(new Image(70,"img70","travel adventure alone colors mountain cold walk wild"));
        addImage(new Image(71,"img71","field plants flowers beautiful sunlight serenity peaceful"));
        addImage(new Image(72,"img72","japan house modern quiet decoration"));
        addImage(new Image(73,"img73","motel stay travel sleep rest trip road old classic"));
        addImage(new Image(74,"img74","concert play band music loud people party enjoy dance jump sing"));
        addImage(new Image(75,"img75","library book read quiet big pages information"));
        addImage(new Image(76,"img76","city metropolis building architecture skyscraper modern"));
        addImage(new Image(77,"img77","city modern metropolis, lake river buildings big large"));
        addImage(new Image(78,"img78","hair animal woods wild danger scary big lethal beard brave"));
        addImage(new Image(79,"img79","coffee hotel reservation payment ring doorbell reception bar"));
        addImage(new Image(80,"img80","coffee amount original small grains brown"));
        addImage(new Image(81,"img81","move transportation tram train rail people public"));
        addImage(new Image(82,"img82","movies film logo actors legend legacy Hollywood cinema"));
        addImage(new Image(83,"img83","city town bicycle center walk sidewalk peaceful"));
        addImage(new Image(84,"img84","globe air fly float colors high"));
        addImage(new Image(85,"img85","restaurant bar people food drinks enjoy talk"));
        addImage(new Image(86,"img86","traffic cars town dark"));
        addImage(new Image(87,"img87","taxi city cars traffic street"));
        addImage(new Image(88,"img88","coffee woman peaceful quiet read"));
        addImage(new Image(89,"img89","plane airplane flight travel airport baggage transport"));
        addImage(new Image(90,"img90","train transportation rail move travel"));
        addImage(new Image(91,"img91","time clock brand ring alarm money hour minutes"));
        addImage(new Image(92,"img92","speed highway cars race lines hurry"));
        addImage(new Image(93,"img93","beach cold sea terror rain sand waves"));
        addImage(new Image(94,"img94","station mall center people shopping stores tickets"));
        addImage(new Image(95,"img95","screens big people center legend legacy stores brands night"));
        addImage(new Image(96,"img96","sun town road street quiet"));
        addImage(new Image(97,"img97","river bridge town quiet peaceful reflections restaurants"));
        addImage(new Image(98,"img98","bridge lights night metropolis large long lake river cross"));
        addImage(new Image(99,"img99","travel sun beach bus surf palm adventure hot"));
        addImage(new Image(100,"img100","wild danger animal leopard cat run brave"));
        addImage(new Image(101,"img101","play game control player games video fun enjoy"));
        addImage(new Image(102,"img102","boat navigate sea lake ship"));
        addImage(new Image(103,"img103","field farm sunlight house grass natural"));
        addImage(new Image(104,"img104","tree woods wood cut firewood"));
        addImage(new Image(105,"img105","train rail travel road mountain"));
        addImage(new Image(106,"img106","neighborhood houses quiet home street trees shadow"));
        addImage(new Image(107,"img107","glass read work computer brand quiet silent"));
        addImage(new Image(108,"img108","beach waves ocean water salt big alone"));
        addImage(new Image(109,"img109","mountain cold high town travel climb"));
        addImage(new Image(110,"img110","train rail travel move"));
        addImage(new Image(111,"img111","desert adventure hot foots drive wild crazy"));
        addImage(new Image(112,"img112","field flowers flower tree trees grass"));
        addImage(new Image(113,"img113","castle legend legacy history field kingdom"));
        addImage(new Image(114,"img114","coffee restaurant food eat friends quiet"));
        addImage(new Image(115,"img115","natural water leave green fresh"));
        addImage(new Image(116,"img116","town houses history old rain city"));
        addImage(new Image(117,"img117","rest field yellow brand cloth comfortable happy"));
        addImage(new Image(118,"img118","wood hole view scape trap inside"));
        addImage(new Image(119,"img119","rocks beach waves hard difficult breakwaters"));
        addImage(new Image(120,"img120","rain city inside trap cold tall high"));
        addImage(new Image(121,"img121","dog dogs grass green brown ear animals zoo "));
        addImage(new Image(122,"img122","rocks rock hard stone grass sky"));
        addImage(new Image(123,"img123","leave leaves yellow floor street sidewalk"));
        addImage(new Image(124,"img124","coffee tea hot cold sit restaurant quiet"));
        addImage(new Image(125,"img125","restaurant food gastronomic gourmet eat table"));
        addImage(new Image(126,"img126","cell cellphone phone call message technology speakers"));
        addImage(new Image(127,"img127","concert music loud band fun enjoy public stage"));
        addImage(new Image(128,"img128","skate sport park play sit drums punk"));
        addImage(new Image(129,"img129","sand foot step human walk beach serenity sun"));
        addImage(new Image(130,"img130","beach rail train travel move bay"));
        addImage(new Image(131,"img131","city lake river water buildings modern big"));
        addImage(new Image(132,"img132","house lake nature mountains trees alone boat"));
        addImage(new Image(133,"img133","castle legend legacy big history kingdom"));
        addImage(new Image(134,"img134","guitar music strings brand play band melody"));
        addImage(new Image(135,"img135","bridge construction architecture big tall high old"));
        addImage(new Image(136,"img136","concert loud music smoke drinks party fun"));
        addImage(new Image(137,"img137","stars sky lights night space concert party festivals"));
        addImage(new Image(138,"img138","boat beach lake water pure nature alone quiet"));
        addImage(new Image(139,"img139","history city modern new lake bridge lights"));
        addImage(new Image(140,"img140","field trees desert house landscape beautiful capitol building"));


    }

}
