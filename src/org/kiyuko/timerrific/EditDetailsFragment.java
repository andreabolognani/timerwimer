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
import android.widget.EditText;

public class EditDetailsFragment extends BaseFragment {

	private BaseFragmentActivity mActivity;
	private EditText mLabelEdit;
	private EditText mTargetTimeEdit;
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

		mDatabase = new TimerDatabase(mActivity);

		view = inflater.inflate(R.layout.fragment_edit_details, container, false);

		mLabelEdit = (EditText) view.findViewById(R.id.label_edit);
		mTargetTimeEdit = (EditText) view.findViewById(R.id.target_time_edit);

		timer = mDatabase.get(getSelectionId());

		if (timer != null) {

			mLabelEdit.setText(timer.getLabel());
			mTargetTimeEdit.setText("" + timer.getTargetTime());
		}

		return view;
	}

	@Override
	public void onDestroy() {

		mDatabase.close();

		super.onDestroy();
	}
}
