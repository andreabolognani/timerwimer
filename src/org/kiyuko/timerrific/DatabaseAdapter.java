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
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DatabaseAdapter extends BaseAdapter {

	private static final String PREFERENCES_FILE = "preferences";
	private static final String KEY_SELECTED_POSITION = "selected_position";

	private Context mContext;
	private TimerDatabase mDatabase;
	private LayoutInflater mInflater;

	public DatabaseAdapter(Context context) {

		super();

		mContext = context;
		mDatabase = TimerDatabase.getInstance(context);
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {

		return mDatabase.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view;
		TextView labelView;
		TextView remainingTimeView;
		Timer timer;

		if (convertView == null) {

			view = mInflater.inflate(R.layout.timer, null);
		}
		else {

			view = convertView;
		}

		timer = mDatabase.get(position);

		labelView = (TextView) view.findViewById(R.id.label);
		remainingTimeView = (TextView) view.findViewById(R.id.remaining_time);

		labelView.setText(timer.getLabel());
		remainingTimeView.setText("" + timer.getRemainingTimeMinutes() + ":" + timer.getRemainingTimeSeconds());

		if (position == getSelectedPosition()) {

			view.setBackgroundColor(mContext.getResources().getColor(R.color.holo_blue_light));
		}
		else {

			view.setBackgroundColor(Color.TRANSPARENT);
		}

		return view;
	}

	@Override
	public Object getItem(int position) {

		return mDatabase.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	public void setSelectedPosition(int selectedPosition) {

		SharedPreferences preferences;

		preferences = mContext.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);

		preferences.edit()
			.putInt(KEY_SELECTED_POSITION, selectedPosition)
		.commit();
	}

	public int getSelectedPosition() {

		SharedPreferences preferences;

		preferences = mContext.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);

		return preferences.getInt(KEY_SELECTED_POSITION, -1);
	}
}
