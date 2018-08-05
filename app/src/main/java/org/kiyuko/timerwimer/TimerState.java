/* Timer Wimer - Countdown timers for your device
 * Copyright (C) 2013-2018  Andrea Bolognani <eof@kiyuko.org>
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
 * with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.kiyuko.timerwimer;

public class TimerState {

    public enum Status {
        STOPPED,
        RUNNING,
        PAUSED,
        FINISHED;
    }

    private int mId;
    private int mTargetTime;

    private Status mStatus;
    private int mCurrentTime;

    public TimerState() {
        mStatus = Status.STOPPED;
    }

    public void action() {
        switch (mStatus) {
            case STOPPED: {
                mStatus = Status.RUNNING;
                mCurrentTime--;
                break;
            }
            case RUNNING: {
                mStatus = Status.PAUSED;
                break;
            }
            case PAUSED: {
                mStatus = Status.RUNNING;
                mCurrentTime--;
                break;
            }
            case FINISHED: {
                mStatus = Status.STOPPED;
                mCurrentTime = mTargetTime;
                break;
            }
        }

        if (mCurrentTime <= 0) {
            mStatus = Status.FINISHED;
            mCurrentTime = 0;
        }
    }

    public int getActionLabel() {
        switch (mStatus) {
            case STOPPED: {
                return R.string.action_start;
            }
            case RUNNING: {
                return R.string.action_pause;
            }
            case PAUSED: {
                return R.string.action_resume;
            }
            case FINISHED: {
                return R.string.action_reset;
            }
        }

        return R.string.error;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getCurrentTime() {
        return mCurrentTime;
    }

    public void setTargetTime(int targetTime) {
        mTargetTime = targetTime;

        if (mStatus == Status.STOPPED) {
            mCurrentTime = targetTime;
        }
    }

    @Override
    public String toString() {
        return String.format("{id=%d, %status=%s, targetTime=%d, currentTime=%d}", mId, mStatus, mTargetTime, mCurrentTime);
    }
}
