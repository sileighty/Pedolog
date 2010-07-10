package jp.spirytus.pedolog.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PedologDao {

	protected SQLiteDatabase db;
	
	public PedologDao(SQLiteDatabase db) {
		this.db = db;
	}
	
	public Long insert(Pedolog pedolog) {
		
		ContentValues cv = new ContentValues();
		
		cv.put("DATE", pedolog.getDate());
		cv.put("COUNT", pedolog.getCount());
		
		return db.insert("PEDOLOG", null, cv);
	}

	public List<Pedolog> findAll() {
		
		List<Pedolog> pedologList = new ArrayList<Pedolog>();
		
		Cursor cursor =
			db.query("PEDOLOG", new String[]{"DATE", "COUNT"}, null,null,null,null,"DATE DESC");
		
		while(cursor.moveToNext()){
			
			Pedolog pedolog = new Pedolog();
			
			pedolog.setDate(cursor.getString(0));
			pedolog.setCount(cursor.getInt(1));
			pedologList.add(pedolog);
		}
		return pedologList;
	}
	
	
}
