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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class NavigationFragment extends BaseListFragment implements DatabaseListener {

	private BaseFragmentActivity mActivity;
	private ListView mListView;
	private DatabaseAdapter mAdapter;

	@Override
	public void onAttach(Activity activity) {

		super.onAttach(activity);

		mActivity = (BaseFragmentActivity) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);

		// Display data taken from the database
		mAdapter = new DatabaseAdapter(mActivity);
		setListAdapter(mAdapter);

		return inflater.inflate(R.layout.fragment_navigation, container, false);
	}

	@Override
	public void onResume() {

		int position;

		super.onResume();

		mListView = getListView();

		position = getSelectedPosition();

		if (position >= 0) {

			// Restore visual feedback
			mListView.setItemChecked(position, true);

			if (position < mListView.getFirstVisiblePosition() || position > mListView.getLastVisiblePosition()) {

				// Restore scroll position
				mListView.setSelection(position);
			}
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		super.onCreateOptionsMenu(menu, inflater);

		mActivity.getSupportMenuInflater().inflate(R.menu.navigation, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

			case R.id.action_add:

				return mActivity.onOptionsItemSelected(item);

			default:

				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		int oldPosition;
		int newPosition;

		super.onListItemClick(l, v, position, id);

		oldPosition = getSelectedPosition();
		newPosition = position;

		setSelectedPosition(position);

		// Give visual feedback
		mListView.setItemChecked(position, true);

		try {

			// Notify the activity
			((SelectionListener) mActivity).onSelectionChanged(oldPosition, newPosition);
		}
		catch (ClassCastException e) {

			throw new ClassCastException(mActivity.getClass().getName()
					+ " must implement SelectionChangeListener");
		}
	}

	@Override
	public void onItemAdded(int position) {

		// Refresh data
		mAdapter.notifyDataSetChanged();

		// Update scroll position and visual feedback
		mListView.setItemChecked(position, true);
		mListView.setSelection(position);
	}

	@Override
	public void onItemRemoved(int position) {

		// Refresh data
		mAdapter.notifyDataSetChanged();

		// Give visual feedback
		mListView.setItemChecked(position, true);

		if (position < mListView.getFirstVisiblePosition() || position > mListView.getLastVisiblePosition()) {

			// Update scroll position
			mListView.setSelection(position);
		}
	}

	@Override
	public void onItemModified(int position) {

		// Refresh data
		mAdapter.notifyDataSetChanged();
	}
}