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
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.kiyuko.timerwimer;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.HashMap;

public class TimerViewModel extends AndroidViewModel {

    private TimerRepository mRepository;
    private TimerRunner mRunner;

    private LiveData<HashMap<Integer, TimerInfo>> mAllTimerInfo;
    private LiveData<HashMap<Integer, TimerState>> mAllTimerState;

    public TimerViewModel(Application application) {
        super(application);

        mRepository = TimerRepository.getRepository(application);
        mAllTimerInfo = mRepository.getAllTimerInfo();

        mRunner = TimerRunner.getRunner(mAllTimerInfo);
        mAllTimerState = mRunner.getAllTimerState();
    }

    public LiveData<HashMap<Integer, TimerInfo>> getAllTimerInfo() {
        return mAllTimerInfo;
    }

    public LiveData<HashMap<Integer, TimerState>> getAllTimerState() {
        return mAllTimerState;
    }

    public void insert(TimerInfo info) {
        mRepository.insert(info);
    }

    public void update(TimerInfo info) {
        mRepository.update(info);
    }

    public void delete(TimerInfo info) {
        mRepository.delete(info);
    }
}
