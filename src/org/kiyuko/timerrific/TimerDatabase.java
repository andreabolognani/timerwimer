/* Timerrific -- Countdown timers for Android devices
 * Copyright (C) 2013  Andrea Bolognani <eof@kiyuko.org>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.kiyuko.timerrific;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TimerDatabase {

	private static final String DATABASE_NAME = "timerrific";
	private static final int DATABASE_VERSION = 1;

	public static final String TABLE_TIMER = "timer";
	public static final String COLUMN_TIMER_ID = "_id";
	public static final String COLUMN_TIMER_LABEL = "label";
	public static final String COLUMN_TIMER_TARGET_TIME = "target_time";

	private static final String[] PROJECTION = new String[] { COLUMN_TIMER_ID, COLUMN_TIMER_LABEL, COLUMN_TIMER_TARGET_TIME };
	private static final String SELECTION = "_id = ?";

	private class Helper extends SQLiteOpenHelper {


		public Helper(Context context) {

			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			// Create a table to store items
	        db.execSQL("CREATE TABLE " + TABLE_TIMER +
	                " ( " +
	                COLUMN_TIMER_ID + " INTEGER PRIMARY KEY, " +
	                COLUMN_TIMER_LABEL + " TEXT, " +
	                COLUMN_TIMER_TARGET_TIME + " INTEGER " +
	                " );");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
	}

	private static TimerDatabase sInstance;

	private Helper mHelper;
	private ArrayList<Timer> mCache;

	private TimerDatabase(Context context) {

		mHelper = new Helper(context);
		readCache();
	}

	public static TimerDatabase getInstance(Context context) {

		if (sInstance == null && context != null) {

			sInstance = new TimerDatabase(context);
		}

		return sInstance;
	}

	private void readCache() {

		SQLiteDatabase db;
		Cursor cursor;
		Timer timer;
		long id;
		String label;
		int targetTime;

		mCache = new ArrayList<Timer>();

		db = mHelper.getReadableDatabase();

		cursor = db.query(TABLE_TIMER,
				new String[] { COLUMN_TIMER_ID, COLUMN_TIMER_LABEL, COLUMN_TIMER_TARGET_TIME },
				"_id > " + Timer.INVALID_ID,
				null,
				null,
				null,
				COLUMN_TIMER_ID);

		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {

			id = cursor.getLong(0);
			label = cursor.getString(1);
			targetTime = cursor.getInt(2);

			timer = new Timer(id, label, targetTime);

			android.util.Log.i(getClass().getName(), "R " + timer);

			mCache.add(timer);

			cursor.move(1);
		}
	}

	private void writeCache() {

		SQLiteDatabase db;
		ContentValues values;
		Timer timer;

		db = mHelper.getWritableDatabase();

		if (db == null) {
			return;
		}

		db.delete(TABLE_TIMER,
				null,
				null);

		for (int i = 0; i < mCache.size(); i++) {

			timer = mCache.get(i);

			android.util.Log.i(getClass().getName(), "W " + timer);

			values = new ContentValues();
			values.put(COLUMN_TIMER_ID, timer.getId());
			values.put(COLUMN_TIMER_LABEL, timer.getLabel());
			values.put(COLUMN_TIMER_TARGET_TIME, timer.getTargetTime());

			// Add a new timer
			db.insert(TABLE_TIMER,
					null,
					values);
		}
	}

	public int size() {

		return mCache.size();
	}

	public Timer get(int position) {

		Timer timer;

		if (position >= 0 && position < mCache.size()) {

			timer = mCache.get(position);
		}
		else {

			timer = null;
		}

		return timer;
	}

	public void add(Timer timer) {

		mCache.add(timer);

		writeCache();
	}

	public void set(int position, Timer timer) {

		if (position >= 0 && position < mCache.size()) {

			mCache.set(position, timer);

			writeCache();
		}
	}

	public void remove(int position) {

		mCache.remove(position);

		writeCache();
	}

	public long getLowestId() {

		SQLiteDatabase db;
		Cursor cursor;
		long id;

		db = mHelper.getReadableDatabase();

		if (db == null) {
			return Timer.INVALID_ID;
		}

		cursor = db.query(TABLE_TIMER,
				new String[] { COLUMN_TIMER_ID },
				"_id > " + Timer.INVALID_ID,
				null,
				null,
				null,
				COLUMN_TIMER_ID);

		if (!cursor.moveToFirst()) {

			cursor.close();

			return Timer.INVALID_ID;
		}

		id = cursor.getLong(cursor.getColumnIndex(COLUMN_TIMER_ID));

		return id;
	}

	public long getHighestId() {

		SQLiteDatabase db;
		Cursor cursor;
		long id;

		db = mHelper.getReadableDatabase();

		if (db == null) {
			return Timer.INVALID_ID;
		}

		cursor = db.query(TABLE_TIMER,
				new String[] { COLUMN_TIMER_ID },
				"_id > " + Timer.INVALID_ID,
				null,
				null,
				null,
				COLUMN_TIMER_ID);

		if (!cursor.moveToLast()) {

			cursor.close();

			return Timer.INVALID_ID;
		}

		id = cursor.getLong(cursor.getColumnIndex(COLUMN_TIMER_ID));

		return id;
	}

	public long newId() {

		return getHighestId() + 1;
	}

	public boolean isEmpty() {

		return getHighestId() == Timer.INVALID_ID;
	}

	public Cursor getAllRowsCursor() {

		SQLiteDatabase db;
		Cursor cursor;

		db = mHelper.getReadableDatabase();

		if (db == null) {
			return null;
		}

		cursor = db.query(TABLE_TIMER,
				PROJECTION,
				"_id >= 0",
				null,
				null,
				null,
				COLUMN_TIMER_ID);

		return cursor;
	}

	public void put(Timer timer) {

		SQLiteDatabase db;
		ContentValues values;

		if (timer == null) {
			return;
		}

		db = mHelper.getWritableDatabase();

		if (db == null) {
			return;
		}

		values = new ContentValues();
		values.put(COLUMN_TIMER_ID, timer.getId());
		values.put(COLUMN_TIMER_LABEL, timer.getLabel());
		values.put(COLUMN_TIMER_TARGET_TIME, timer.getTargetTime());

		if (get(timer.getId()) != null) {

			// Update an existing timer
			db.update(TABLE_TIMER,
					values,
					SELECTION,
					new String[] { "" + timer.getId() });
		}
		else {

			// Add a new timer
			db.insert(TABLE_TIMER,
					null,
					values);
		}
	}

	public Timer get(long id) {

		SQLiteDatabase db;
		Cursor cursor;
		Timer timer;

		db = mHelper.getReadableDatabase();

		if (db == null) {
			return null;
		}

		// Look for an existing timer with the correct id
		cursor = db.query(TABLE_TIMER,
				PROJECTION,
				SELECTION,
				new String[] { "" + id },
				null,
				null,
				null);

		if (!cursor.moveToFirst()) {

			cursor.close();

			return null;
		}

		timer = new Timer(id,
				cursor.getString(cursor.getColumnIndex(COLUMN_TIMER_LABEL)),
				cursor.getInt(cursor.getColumnIndex(COLUMN_TIMER_TARGET_TIME)));

		return timer;
	}

	public void remove(long id) {

		SQLiteDatabase db;

		db = mHelper.getWritableDatabase();

		if (db == null) {
			return;
		}

		db.delete(TABLE_TIMER,
				SELECTION,
				new String[] { "" + id });
	}
}
