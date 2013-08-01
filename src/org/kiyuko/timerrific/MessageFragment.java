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

public class MessageFragment extends BaseFragment {

	private BaseFragmentActivity mActivity;
	private TextView mMessageView;
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

		super.onCreateView(inflater, container, savedInstanceState);

		mDatabase = new TimerDatabase(mActivity);

		// Display a single message if no timer is selected
		view = inflater.inflate(R.layout.fragment_message, container, false);

		mMessageView = (TextView) view.findViewById(R.id.message_view);

		if (mDatabase.isEmpty()) {

			// No timers
			mMessageView.setText(R.string.no_timers);
		}
		else {

			// No timer selected
			mMessageView.setText(R.string.no_timer_selected);
		}

		return view;
	}

	@Override
	public void onDestroy() {

		mDatabase.close();

		super.onDestroy();
	}
}
