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
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.List;

public class TimerRepository {

    private TimerDao mDao;
    private LiveData<List<TimerInfo>> mSourceTimerInfo;
    private MutableLiveData<HashMap<Integer, TimerInfo>> mAllTimerInfo;

    public TimerRepository(Application application) {
        mAllTimerInfo = new MutableLiveData<>();

        mDao = TimerDatabase.getDatabase(application).getDao();
        mSourceTimerInfo = mDao.getAllTimerInfo();
        mSourceTimerInfo.observeForever(new Observer<List<TimerInfo>>() {
            @Override
            public void onChanged(@Nullable List<TimerInfo> allTimerInfo) {
                HashMap<Integer, TimerInfo> temp = new HashMap<>();

                for (TimerInfo info: allTimerInfo) {
                    temp.put(info.getId(), info);
                }

                mAllTimerInfo.postValue(temp);
            }
        });
    }

    public LiveData<HashMap<Integer, TimerInfo>> getAllTimerInfo() {
        return mAllTimerInfo;
    }

    public void insert(TimerInfo info) {
        new AsyncInsertTask(mDao).execute(info);
    }

    public void update(TimerInfo info) {
        new AsyncUpdateTask(mDao).execute(info);
    }

    public void delete(TimerInfo info) {
        new AsyncDeleteTask(mDao).execute(info);
    }

    private static class AsyncInsertTask extends AsyncTask<TimerInfo, Void, Void> {

        private TimerDao mDao;

        public AsyncInsertTask(TimerDao dao) {
            mDao = dao;
        }

        @Override
        protected Void doInBackground(TimerInfo... args) {
            mDao.insert(args[0]);
            return null;
        }
    }

    private static class AsyncUpdateTask extends AsyncTask<TimerInfo, Void, Void> {

        private TimerDao mDao;

        public AsyncUpdateTask(TimerDao dao) {
            mDao = dao;
        }

        @Override
        protected Void doInBackground(TimerInfo... args) {
            mDao.update(args[0]);
            return null;
        }
    }

    private static class AsyncDeleteTask extends AsyncTask<TimerInfo, Void, Void> {

        private TimerDao mDao;

        public AsyncDeleteTask(TimerDao dao) {
            mDao = dao;
        }

        @Override
        protected Void doInBackground(TimerInfo... args) {
            mDao.delete(args[0].getId());
            return null;
        }
    }
}
