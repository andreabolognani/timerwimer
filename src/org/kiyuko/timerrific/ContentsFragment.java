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
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class ContentsFragment extends BaseFragment {

	private BaseFragmentActivity mActivity;
	private TextView mCurrentTimeView;
	private TimerDatabase mDatabase;

	@Override
	public void onAttach(Activity activity) {

		super.onAttach(activity);

		mActivity = (BaseFragmentActivity) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view;
		TextView messageView;
		Timer timer;

		super.onCreateView(inflater, container, savedInstanceState);

		mDatabase = new TimerDatabase(mActivity);

		if (mDatabase.isEmpty() || getSelectionId() == Timer.INVALID_ID) {

			// Display a single message if no timer is selected
			view = inflater.inflate(R.layout.fragment_contents_message, container, false);

			messageView = (TextView) view.findViewById(R.id.message_view);

			if (mDatabase.isEmpty()) {

				// No timers
				messageView.setText(R.string.no_timers);
			}
			else {

				// No timer selected
				messageView.setText(R.string.no_timer_selected);
			}
		}
		else {

			// Display timer details
			view = inflater.inflate(R.layout.fragment_contents, container, false);

			mCurrentTimeView = (TextView) view.findViewById(R.id.current_time_view);

			timer = mDatabase.get(getSelectionId());

			if (timer != null) {

				mCurrentTimeView.setText("Current time: " + timer.getTargetTime());
			}
		}

		return view;
	}

	@Override
	public void onDestroy() {

		mDatabase.close();

		super.onDestroy();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		super.onCreateOptionsMenu(menu, inflater);

		if (getSelectionId() != Timer.INVALID_ID) {

			// Valid selection: add items to options menu
			mActivity.getSupportMenuInflater().inflate(R.menu.contents, menu);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

			case R.id.action_remove:

				// Remove selected timer from the database
				mDatabase.remove(getSelectionId());

				// Invalidate selection
				setSelectionId(Timer.INVALID_ID);

				mActivity.onSelectionChanged();

				return true;

			default:

				return super.onOptionsItemSelected(item);
		}
	}
}
