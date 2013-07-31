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

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class NavigationFragment extends BaseListFragment {

	private BaseFragmentActivity mActivity;
	private TimerDatabase mDatabase;
	private CursorAdapter mAdapter;

	@Override
	public void onAttach(Activity activity) {

		super.onAttach(activity);

		mActivity = (BaseFragmentActivity) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);

		mDatabase = new TimerDatabase(mActivity);

		// Display data taken from the database
		mAdapter = new SimpleCursorAdapter(mActivity,
				android.R.layout.simple_list_item_1,
				null,
				new String[] { TimerDatabase.COLUMN_TIMER_LABEL },
				new int[] { android.R.id.text1 });
		setListAdapter(mAdapter);

		return inflater.inflate(R.layout.fragment_navigation, container, false);
	}

	@Override
	public void onResume() {

		super.onResume();

		// Refresh the data every time the fragment is displayed
		mAdapter.changeCursor(mDatabase.getAllRowsCursor());
	}

	@Override
	public void onDestroy() {

		// Release the database connection
		mDatabase.close();

		super.onDestroy();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		super.onCreateOptionsMenu(menu, inflater);

		mActivity.getSupportMenuInflater().inflate(R.menu.navigation, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Timer timer;

		switch (item.getItemId()) {

			case R.id.action_add:

				timer = mDatabase.get(getSelectionId());

				Toast.makeText(mActivity, "Selected: " + timer.getLabel(), Toast.LENGTH_SHORT).show();

				return true;

			default:

				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);

		setSelectionId(id);

		mActivity.onSelectionChanged();
	}
}
