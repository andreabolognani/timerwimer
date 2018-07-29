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
    private int mId;
    private int mCurrentTime;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getCurrentTime() {
        return mCurrentTime;
    }

    public void setCurrentTime(int currentTime) {
        mCurrentTime = currentTime;
    }

    @Override
    public String toString() {
        return String.format("{id=%d, currentTime=%d}", mId, mCurrentTime);
    }
}
