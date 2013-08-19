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

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DatabaseAdapter extends SimpleCursorAdapter {

	private static final String PREFERENCES_FILE = "preferences";
	private static final String KEY_SELECTION_ID = "selection_id";

	private LayoutInflater mInflater;
	private Cursor mCursor;

	public DatabaseAdapter(Context context, Cursor c) {

		super(context,
			R.layout.timer,
			c,
			new String[] { TimerDatabase.COLUMN_TIMER_LABEL },
			new int[] { R.id.label });

		mInflater = LayoutInflater.from(context);
		mCursor = c;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view;
		TextView labelView;
		String label;
		long id;

		if (convertView == null) {

			view = mInflater.inflate(R.layout.timer, null);
		}
		else {

			view = convertView;
		}

		if (!mCursor.isClosed()) {

			mCursor.moveToFirst();
			mCursor.move(position);

			id = mCursor.getLong(mCursor.getColumnIndex(TimerDatabase.COLUMN_TIMER_ID));
			label = mCursor.getString(mCursor.getColumnIndex(TimerDatabase.COLUMN_TIMER_LABEL));

			labelView = (TextView) view.findViewById(R.id.label);
			labelView.setText(label);

			if (id == getSelectionId()) {

				view.setBackgroundColor(mContext.getResources().getColor(R.color.holo_blue_light));
			}
			else {

				view.setBackgroundColor(Color.TRANSPARENT);
			}
		}

		return view;
	}

	@Override
	public void changeCursor(Cursor cursor) {

		super.changeCursor(cursor);

		mCursor = cursor;
	}

	public void setSelectionId(long selectedId) {

		SharedPreferences preferences;

		preferences = mContext.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);

		preferences.edit()
			.putLong(KEY_SELECTION_ID, selectedId)
		.commit();
	}

	public long getSelectionId() {

		SharedPreferences preferences;

		preferences = mContext.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);

		return preferences.getLong(KEY_SELECTION_ID, Timer.INVALID_ID);
	}
}
