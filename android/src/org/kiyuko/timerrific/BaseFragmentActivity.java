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

import android.content.SharedPreferences;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class BaseFragmentActivity extends SherlockFragmentActivity {

	private static final String PREFERENCES_FILE = "preferences";
	private static final String KEY_SELECTED_POSITION = "selected_position";

	public void setSelectedPosition(int selectedPosition) {

		SharedPreferences preferences;

		preferences = getSharedPreferences(PREFERENCES_FILE, MODE_PRIVATE);

		preferences.edit()
			.putInt(KEY_SELECTED_POSITION, selectedPosition)
		.commit();
	}

	public int getSelectedPosition() {

		SharedPreferences preferences;

		preferences = getSharedPreferences(PREFERENCES_FILE, MODE_PRIVATE);

		return preferences.getInt(KEY_SELECTED_POSITION, -1);
	}
}
