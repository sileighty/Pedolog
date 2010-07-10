package jp.spirytus.pedolog.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "PEDOLOG";
	
	private static final int DATABASE_VERSION = 1;
	
	private static final String CREATE_TABLE = "CREATE TABLE PEDOLOG(" +
			"DATE TEXT NOT NULL," +
			"COUNT INTEGER NOT NULL" +
			")";
	
	private static final String DROP_TABLE = "DROP TABLE IF EXISTS PEDOLOG";
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		
		db.execSQL(DROP_TABLE);
		onCreate(db);

	}

}
