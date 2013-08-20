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

import org.kiyuko.timerrific.NumberPicker.OnValueChangeListener;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

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
	private TextWatcher mTextChangedListener;
	private OnValueChangeListener mOnValueChangedListener;
	private TimerDatabase mDatabase;
	private int mTimerPosition;

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
		mTimerPosition = getSelectedPosition();

		view = inflater.inflate(R.layout.fragment_edit_details, container, false);

		mLabelEdit = (EditText) view.findViewById(R.id.label_edit);
		mMinutesPicker = (NumberPicker) view.findViewById(R.id.minutes_picker);
		mSecondsPicker = (NumberPicker) view.findViewById(R.id.seconds_picker);

		mMinutesPicker.setMinValue(0);
		mMinutesPicker.setMaxValue(99);
		mSecondsPicker.setMinValue(0);
		mSecondsPicker.setMaxValue(59);

		timer = mDatabase.get(mTimerPosition);

		if (timer != null) {

			mLabelEdit.setText(timer.getLabel());
			mMinutesPicker.setValue(timer.getTargetTimeMinutes());
			mSecondsPicker.setValue(timer.getTargetTimeSeconds());
		}

		// Create text watcher
		mTextChangedListener = new TextWatcher() {

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

		mOnValueChangedListener = new OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldValue, int newValue) {

				EditDetailsFragment.this.save();
			}
		};

		return view;
	}

	@Override
	public void onResume() {

		super.onResume();

		// Add listeners
		mLabelEdit.addTextChangedListener(mTextChangedListener);
		mMinutesPicker.setOnValueChangedListener(mOnValueChangedListener);
		mSecondsPicker.setOnValueChangedListener(mOnValueChangedListener);
	}

	@Override
	public void onPause() {

		// Remove listeners
		mLabelEdit.removeTextChangedListener(mTextChangedListener);
		mMinutesPicker.setOnValueChangedListener(null);
		mSecondsPicker.setOnValueChangedListener(null);

		super.onPause();
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

		timer = mDatabase.get(mTimerPosition);

		if (timer == null) {

			return;
		}

		// Set new label
		timer.setLabel(mLabelEdit.getText().toString());

		// Set new target time
		timer.setTargetTime(mMinutesPicker.getValue(), mSecondsPicker.getValue());

		mDatabase.set(mTimerPosition, timer);

		try {

			// Notify activity
			((DatabaseListener) mActivity).onItemModified(mTimerPosition);
		}
		catch (ClassCastException e) {

			throw new ClassCastException(mActivity.getClass().getName()
					+ " must implement DatabaseListener");
		}
	}
}
