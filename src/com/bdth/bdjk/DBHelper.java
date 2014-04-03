package com.bdth.bdjk;

import android.content.ContentValues;  
import android.content.Context;  
import android.database.Cursor;  
import android.database.sqlite.SQLiteDatabase;  
import android.database.sqlite.SQLiteOpenHelper;  
  
public class DBHelper extends SQLiteOpenHelper {  
  
    public static final String DB_NAME = "bdjk.db";  
    public static final int DB_VERSION = 1;  
    public static final String TABLE_NAME_USRERLOGIN = "loginuser";  
    public static final String TABLE_NAME_CURUSER = "curuser";  
    public static final String TABLE_NAME_VEHICLE = "vehicles";  
    public static final String CREATE_USERLOGIN = "create table if not exists "+TABLE_NAME_USRERLOGIN+"("  
            + "id integer primary key autoincrement,"
            + "userId integer,"
    		+ "name varchar(20),"  
    		+ "compId integer,"
            + "compName varchar(20))";  
    public static final String CREATE_CURUSER = "create table if not exists "+TABLE_NAME_CURUSER+"(id integer primary key autoincrement,name varchar(20))";
    public static final String CREATE_VEHICLE = "create table if not exists "+TABLE_NAME_VEHICLE+"("
            + "id integer primary key autoincrement,"
    		+ "vehiclesId integer,"
            + "plateNumber varchar(20)," 
    		+ "type varchar(10)," 
    		+ "model varchar(20)," 
    		+ "speedLimit integer,"
    		+ "state integer, "
    		+ "groupId integer,"
    		+ "groupName varchar(40)," 
    		+ "sim varchar(20),userId integer)";
    private SQLiteDatabase db;  
  
    public DBHelper(Context c) {  
        //  
        super(c, DB_NAME, null, DB_VERSION);          
    }
    
    public void init(){
    	this.db = this.getWritableDatabase();      
    	db.execSQL(CREATE_CURUSER);
        db.execSQL(CREATE_USERLOGIN);  
        db.execSQL(CREATE_VEHICLE);
    }
  
    @Override  
    public void onCreate(SQLiteDatabase dbs) {  
        
    }  
  
    public void insert(ContentValues values, String tableName) {  
        db = getWritableDatabase();  
        db.insert(tableName, null, values);  
        db.close();  
    }  
  
    // Return cursor with all columns by tableName  
    public Cursor query(String tableName) {  
        db = getWritableDatabase();  
        Cursor c = db.query(tableName, null, null, null, null, null, null);  
        return c;  
    }  
  
    // Return cursor by SQL string  
    public Cursor rawQuery(String sql, String[] args) {  
        db = getWritableDatabase();  
        Cursor c = db.rawQuery(sql, args);  
        return c;  
    }  
  
    // Execute a single SQL statement(as insert,create,delete)instead of a query  
    public void execSQL(String sql) {  
        db = getWritableDatabase();  
        db.execSQL(sql);  
    }  
  
    // Delete by id  
    public void delUser(int id,String tableName) {  
        if (db == null)  
            db = getWritableDatabase();  
        db.delete(tableName, "userid=?", new String[] { String.valueOf(id) });  
    }  
    
 // Delete by id  
    public void del(int id,String tableName) {  
        if (db == null)  
            db = getWritableDatabase();  
        db.delete(tableName, "id=?", new String[] { String.valueOf(id) });  
    }  
    
    // Delete all  
    public void del(String tableName) {  
        if (db == null)  
            db = getWritableDatabase();  
        db.delete(tableName, null, null);  
    }  
  
    public void close() {  
        if (db != null)  
            db.close();  
    }  
  
    @Override  
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  
    }  
  
}  
