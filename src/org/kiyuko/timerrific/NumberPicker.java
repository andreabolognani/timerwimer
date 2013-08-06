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

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class NumberPicker extends LinearLayout {

	private android.widget.NumberPicker mNativePicker;
	private com.michaelnovakjr.numberpicker.NumberPicker mCompatPicker;
	private int mMinValue;
	private int mMaxValue;

	/**
	 * Create a NumberPicker.
	 *
	 * @param context
	 * @param attrs
	 */
	public NumberPicker(Context context, AttributeSet attrs) {

		super(context, attrs);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

			// Create a native NumberPicker
			mNativePicker = new android.widget.NumberPicker(context, attrs);
			addView(mNativePicker);

			mCompatPicker = null;
		}
		else {

			// Create a compatibility NumberPicker for legacy Android versions
			mCompatPicker = new com.michaelnovakjr.numberpicker.NumberPicker(context, attrs);
			addView(mCompatPicker);

			mNativePicker = null;
		}

		// Set default values
		setMinValue(0);
		setMaxValue(0);
		setValue(0);
	}

	/**
	 * Set minimum value.
	 *
	 * @param minValue minimum value
	 */
	public void setMinValue(int minValue) {

		mMinValue = minValue;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

			mNativePicker.setMinValue(mMinValue);
		}
		else {

			mCompatPicker.setRange(mMinValue, mMaxValue);
		}
	}

	/**
	 * Get minimum value.
	 *
	 * @return minimum value
	 */
	public int getMinValue() {

		return mMinValue;
	}

	/**
	 * Set maximum value.
	 *
	 * @param maxValue maximum value
	 */
	public void setMaxValue(int maxValue) {

		mMaxValue = maxValue;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

			mNativePicker.setMaxValue(mMaxValue);
		}
		else {

			mCompatPicker.setRange(mMinValue, mMaxValue);
		}
	}

	/**
	 * Get maximum value.
	 *
	 * @return maximum value
	 */
	public int getMaxValue() {

		return mMaxValue;
	}

	/**
	 * Set value.
	 *
	 * @param value new value
	 */
	public void setValue(int value) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

			mNativePicker.setValue(value);
		}
		else{

			mCompatPicker.setCurrent(value);
		}
	}

	/**
	 * Get value.
	 *
	 * @return current value
	 */
	public int getValue() {

		int value;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

			value = mNativePicker.getValue();
		}
		else {

			value = mCompatPicker.getCurrent();
		}

		return value;
	}
}
