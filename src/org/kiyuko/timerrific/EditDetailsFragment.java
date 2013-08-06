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

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.michaelnovakjr.numberpicker.NumberPicker;
import com.michaelnovakjr.numberpicker.NumberPicker.OnChangedListener;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class EditDetailsFragment extends BaseFragment {

	private BaseFragmentActivity mActivity;
	private EditText mLabelEdit;
	private NumberPicker mMinutesPicker;
	private NumberPicker mSecondsPicker;
	private TextWatcher mTextWatcher;
	private OnChangedListener mOnChangedListener;
	private TimerDatabase mDatabase;
	private long mTimerId;

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
		mTimerId = getSelectionId();

		view = inflater.inflate(R.layout.fragment_edit_details, container, false);

		mLabelEdit = (EditText) view.findViewById(R.id.label_edit);
		mMinutesPicker = (NumberPicker) view.findViewById(R.id.minutes_picker);
		mSecondsPicker = (NumberPicker) view.findViewById(R.id.seconds_picker);

		mMinutesPicker.setRange(0, 99);
		mSecondsPicker.setRange(0, 59);

		timer = mDatabase.get(mTimerId);

		if (timer != null) {

			mLabelEdit.setText(timer.getLabel());
			mMinutesPicker.setCurrent(0);
			mSecondsPicker.setCurrent(timer.getTargetTime());
		}

		// Create text watcher
		mTextWatcher = new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {

				EditDetailsFragment.this.save();
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
		};

		mOnChangedListener = new OnChangedListener() {

			@Override
			public void onChanged(NumberPicker picker, int oldVal, int newVal) {

				EditDetailsFragment.this.save();
			}
		};

		return view;
	}

	@Override
	public void onResume() {

		super.onResume();

		// Add listeners
		mLabelEdit.addTextChangedListener(mTextWatcher);
		mSecondsPicker.setOnChangeListener(mOnChangedListener);
	}

	@Override
	public void onPause() {

		// Remove listeners
		mLabelEdit.removeTextChangedListener(mTextWatcher);
		mSecondsPicker.setOnChangeListener(null);

		super.onPause();
	}

	@Override
	public void onDestroy() {

		mDatabase.close();

		super.onDestroy();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		super.onCreateOptionsMenu(menu, inflater);

		mActivity.getSupportMenuInflater().inflate(R.menu.edit_details, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

			case R.id.action_accept:

				return mActivity.onOptionsItemSelected(item);

			default:

				return super.onOptionsItemSelected(item);
		}
	}

	private void save() {

		Timer timer;
		int targetTime;

		timer = mDatabase.get(mTimerId);

		if (timer == null) {

			return;
		}

		// Retrieve new label
		timer.setLabel(mLabelEdit.getText().toString());

		targetTime = mSecondsPicker.getCurrent();

		if (targetTime > 0) {

			// Set new target time if valid
			timer.setTargetTime(targetTime);
		}

		mDatabase.put(timer);

		try {

			// Notify activity
			((DatabaseListener) mActivity).onItemModified(mTimerId);
		}
		catch (ClassCastException e) {

			throw new ClassCastException(mActivity.getClass().getName()
					+ " must implement DatabaseListener");
		}
	}
}
