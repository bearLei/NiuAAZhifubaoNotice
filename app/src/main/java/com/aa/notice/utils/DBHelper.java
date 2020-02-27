package com.aa.notice.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{
	public DBHelper(Context context) {  
        super(context, "trade.db", null, 1);  
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS qrcode" + 
				"(_id INTEGER PRIMARY KEY AUTOINCREMENT, money varchar, mark varchar, type varchar, payurl varchar, dt varchar)");
		db.execSQL("CREATE TABLE IF NOT EXISTS payorder" + 
				"(_id INTEGER PRIMARY KEY AUTOINCREMENT, money varchar, mark varchar, type varchar, tradeno varchar, dt varchar, result varchar, time integer)");
		db.execSQL("CREATE TABLE IF NOT EXISTS tradeno" + 
				"(_id INTEGER PRIMARY KEY AUTOINCREMENT, tradeno varchar, status varchar)");
		db.execSQL("CREATE TABLE IF NOT EXISTS config" + 
				"(_id INTEGER PRIMARY KEY AUTOINCREMENT, name varchar, value varchar)");
	}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}  
}
