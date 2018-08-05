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

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class TimerRunner extends TimerTask {

    private static TimerRunner INSTANCE;

    private LiveDataWrapper mAllTimerState;
    private Timer mTimer;

    public static TimerRunner getRunner(LiveData<HashMap<Integer, TimerInfo>> allTimerInfo) {
        if (INSTANCE == null) {
            synchronized (TimerRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TimerRunner(allTimerInfo);
                }
            }
        }
        return INSTANCE;
    }

    private TimerRunner(LiveData<HashMap<Integer, TimerInfo>> allTimerInfo) {
        mAllTimerState = new LiveDataWrapper(allTimerInfo);

        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(this, 0, 1000);
    }

    public LiveData<HashMap<Integer, TimerState>> getAllTimerState() {
        return mAllTimerState;
    }

    public void action(TimerState state) {
        state = mAllTimerState.getValue().get(state.getId());

        if (state == null) {
            return;
        }

        switch (state.getStatus()) {
            case STOPPED: {
                state.setStatus(TimerState.Status.RUNNING);
                break;
            }
            case RUNNING: {
                state.setStatus(TimerState.Status.PAUSED);
                break;
            }
            case PAUSED: {
                state.setStatus(TimerState.Status.RUNNING);
                break;
            }
            case FINISHED: {
                state.setStatus(TimerState.Status.STOPPED);
                state.setCurrentTime(state.getTargetTime());
                break;
            }
        }

        mAllTimerState.post();
    }

    @Override
    public void run() {
        HashMap<Integer, TimerState> allTimerState = mAllTimerState.getValue();

        if (allTimerState == null) {
            return;
        }

        for (TimerState state: allTimerState.values()) {
            if (state.getStatus() == TimerState.Status.RUNNING) {
                int currentTime = state.getCurrentTime();

                currentTime--;
                state.setCurrentTime(currentTime);

                if (currentTime <= 0) {
                    state.setStatus(TimerState.Status.FINISHED);
                    state.setCurrentTime(0);
                }
            }
        }

        mAllTimerState.post();
    }

    private class LiveDataWrapper extends LiveData<HashMap<Integer, TimerState>> implements Observer<HashMap<Integer, TimerInfo>> {

        private LiveData<HashMap<Integer, TimerInfo>> mSourceLiveData;
        private HashMap<Integer, TimerState> mAllTimerState;

        public LiveDataWrapper(LiveData<HashMap<Integer, TimerInfo>> sourceLiveData) {
            mAllTimerState = new HashMap<>();
            mSourceLiveData = sourceLiveData;
        }

        public void post() {
            postValue(mAllTimerState);
        }

        @Override
        protected void onActive() {
            mSourceLiveData.observeForever(this);
        }

        @Override
        protected void onInactive() {
            mSourceLiveData.removeObserver(this);
        }

        @Override
        public void onChanged(@Nullable HashMap<Integer, TimerInfo> allTimerInfo) {
            Set<Integer> infoSet = allTimerInfo.keySet();
            Set<Integer> stateSet = mAllTimerState.keySet();

            Set<Integer> removeSet = new HashSet<>(stateSet);
            removeSet.removeAll(infoSet);

            for (Integer id: removeSet) {
                mAllTimerState.remove(id);
            }

            Set<Integer> addSet = new HashSet<>(infoSet);
            addSet.removeAll(stateSet);

            for (Integer id: addSet) {
                mAllTimerState.put(id, new TimerState());
            }

            for (TimerInfo info: allTimerInfo.values()) {
                TimerState state = mAllTimerState.get(info.getId());

                if (state == null) {
                    continue;
                }

                state.setId(info.getId());
                state.setTargetTime(info.getTargetTime());
            }

            post();
        }
    }
}
