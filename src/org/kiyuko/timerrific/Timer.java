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

public class Timer {

	private String mLabel;
	private int mTargetTime;
	private int mRemainingTime;

	public Timer() {

		mLabel = "";
		mTargetTime = 0;
		mRemainingTime = 0;
	}

	public Timer(String label, int targetTime) {

		mLabel = label;
		mTargetTime = targetTime;
		mRemainingTime = mTargetTime;
	}

	public void setLabel(String label) {

		mLabel = label;
	}

	public String getLabel() {

		return mLabel;
	}

	/**
	 * Set target time.
	 *
	 * @param targetTime target time in seconds
	 */
	public void setTargetTime(int targetTime) {

		mTargetTime = targetTime;
		mRemainingTime = mTargetTime;
	}

	/**
	 * Set target time using separate components for minutes and seconds.
	 *
	 * @param targetTimeMinutes minutes only
	 * @param targetTimeSeconds seconds only
	 */
	public void setTargetTime(int targetTimeMinutes, int targetTimeSeconds) {

		if (targetTimeMinutes < 0 || targetTimeSeconds < 0) {

			return;
		}

		mTargetTime = (targetTimeMinutes * 60) + targetTimeSeconds;
		mRemainingTime = mTargetTime;
	}

	/**
	 * Get target time.
	 *
	 * @return target time in seconds
	 */
	public int getTargetTime() {

		return mTargetTime;
	}

	/**
	 * Get minutes component of target time.
	 *
	 * @return minutes only
	 */
	public int getTargetTimeMinutes() {

		return (mTargetTime - mTargetTime % 60) / 60;
	}

	/**
	 * Get seconds component of target time.
	 *
	 * @return seconds only (between 0 and 59)
	 */
	public int getTargetTimeSeconds() {

		return mTargetTime % 60;
	}

	/**
	 * Set remaining time.
	 *
	 * @param remainingTime remaining time in seconds
	 */
	public void setRemainingTime(int remainingTime) {

		mRemainingTime = remainingTime;

		if (mRemainingTime > mTargetTime) {

			mRemainingTime = mTargetTime;
		}
		else if (mRemainingTime < 0) {

			mRemainingTime = 0;
		}
	}

	/**
	 * Get remaining time.
	 *
	 * @return remaining time in seconds
	 */
	public int getRemainingTime() {

		return mRemainingTime;
	}

	/**
	 * Get minutes component of remaining time.
	 *
	 * @return minutes only
	 */
	public int getRemainingTimeMinutes() {

		return (mRemainingTime - mRemainingTime % 60) / 60;
	}

	/**
	 * Get seconds component of remaining time.
	 *
	 * @return seconds only (between 0 and 59)
	 */
	public int getRemainingTimeSeconds() {

		return mRemainingTime % 60;
	}

	@Override
	public String toString() {

		return "(\"" + mLabel + "\" " + mTargetTime + ")";
	}
}
