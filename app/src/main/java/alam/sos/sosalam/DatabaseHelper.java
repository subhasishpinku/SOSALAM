package alam.sos.sosalam;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper  extends SQLiteOpenHelper {
    public static final String DB_NAME = "SOS";
    private static final int DB_VERSION = 1;
    public static final String TABLE_IMEITABLE = "imeitable";
    public static final String TABLE_IMEIID = "imeiid";
    public static final String TABLE_IMEI1 = "imei1";
    public static final String TABLE_IMEI2 ="imei2";
    //////////////////////////////////////////////////////////
    public static final String TABLE_FLAG = "tableflag";
    public static final String TABLE_FLAGID = "tableflagid";
    public static final String TABLE_FLAGG = "tableflagg";
    public DatabaseHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String flagdate = "CREATE TABLE " + TABLE_IMEITABLE
                + "(" + TABLE_IMEIID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TABLE_IMEI1 + " VARCHAR, "
                + TABLE_IMEI2 + " VARCHAR);";
        db.execSQL(flagdate);
        String flag = "CREATE TABLE " + TABLE_FLAG
                + "(" + TABLE_FLAGID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TABLE_FLAGG + " VARCHAR);";
        db.execSQL(flag);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String flag = "DROP TABLE IF EXISTS TABLE_IMEITABLE";
        db.execSQL(flag);
        String flagg = "DROP TABLE IF EXISTS TABLE_FLAG";
        db.execSQL(flagg);
    }

    public boolean IMEIINSERT(String imei1, String imei2){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLE_IMEI1, imei1);
        contentValues.put(TABLE_IMEI2,imei2);
        db.insert(TABLE_IMEITABLE, null, contentValues);
        db.close();
        return true;
    }
    public boolean FLAGTABLEUPDATE(String imeiid,String imei1, String imei2) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLE_IMEIID, imeiid);
        contentValues.put(TABLE_IMEI1, imei1);
        contentValues.put(TABLE_IMEI2, imei2);
        db.update(TABLE_IMEITABLE, contentValues, "imeiid = ?",new String[] { imeiid });
        db.close();
        return true;
    }
    public Cursor getFlagData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_IMEITABLE,null);
        return res;
    }
    public boolean FLAG(String tableflagg){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLE_FLAGG, tableflagg);
        db.insert(TABLE_FLAG, null, contentValues);
        db.close();
        return true;
    }
    public boolean FLAGUPDATE(String tableflagid,String tableflagg) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLE_FLAGID, tableflagid);
        contentValues.put(TABLE_FLAGG, tableflagg);
        db.update(TABLE_FLAG, contentValues, "tableflagid = ?",new String[] { tableflagid });
        db.close();
        return true;
    }
    public Cursor getFlag() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_FLAG,null);
        return res;
    }
}
