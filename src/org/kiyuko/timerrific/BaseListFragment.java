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

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockListFragment;

public class BaseListFragment extends SherlockListFragment {

	private static final String PREFERENCES_FILE = "preferences";
	private static final String KEY_SELECTION_ID = "selection_id";

	public void setSelectionId(long selectedId) {

		SharedPreferences preferences;

		preferences = getSherlockActivity().getSharedPreferences(PREFERENCES_FILE, SherlockActivity.MODE_PRIVATE);

		preferences.edit()
			.putLong(KEY_SELECTION_ID, selectedId)
		.commit();
	}

	public long getSelectionId() {

		SharedPreferences preferences;

		preferences = getSherlockActivity().getSharedPreferences(PREFERENCES_FILE, SherlockActivity.MODE_PRIVATE);

		return preferences.getLong(KEY_SELECTION_ID, Timer.INVALID_ID);
	}
}
