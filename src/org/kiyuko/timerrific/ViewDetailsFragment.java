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

public class ViewDetailsFragment extends BaseFragment {

	private BaseFragmentActivity mActivity;
	private TextView mRemainingTimeView;
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
		Timer timer;

		super.onCreateView(inflater, container, savedInstanceState);

		mDatabase = TimerDatabase.getInstance(mActivity);

		// Display timer details
		view = inflater.inflate(R.layout.fragment_view_details, container, false);

		mRemainingTimeView = (TextView) view.findViewById(R.id.remaining_time_view);

		timer = mDatabase.get(getSelectedPosition());

		if (timer != null) {

			mRemainingTimeView.setText("" + timer.getRemainingTimeMinutes() + " : " + timer.getRemainingTimeSeconds());
		}

		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		super.onCreateOptionsMenu(menu, inflater);

		mActivity.getSupportMenuInflater().inflate(R.menu.view_details, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

			case R.id.action_remove:

				return mActivity.onOptionsItemSelected(item);

			default:

				return super.onOptionsItemSelected(item);
		}
	}
}
