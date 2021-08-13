package uk.ac.tees.aad.W9299136.SqlitePersistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Sqlite extends SQLiteOpenHelper {

    private static final String KEY_ID = "id";
    Context context;

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "Login";
    private static final String TABLE_SIGN_INFO = "signin";


    public Sqlite(Context context1) {
        super(context1, DB_NAME, null, DB_VERSION);
        context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_SIGN_INFO + "("
                + "id_auto" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "date" + " TEXT,"
                + "email" + " TEXT " + ")";

        db.execSQL(CREATE_TABLE);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SIGN_INFO);
        onCreate(db);
    }

    public void insertData(String date,  String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("email", email);

        long insert = db.insert(TABLE_SIGN_INFO, null, values);
        db.close();
    }


}
