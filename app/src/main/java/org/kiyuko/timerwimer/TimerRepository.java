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
import android.arch.lifecycle.Observer;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.List;

public class TimerRepository {

    private static TimerRepository INSTANCE;

    private TimerDao mDao;
    private LiveDataWrapper mAllTimerInfo;

    public static TimerRepository getRepository(Application application) {
        if (INSTANCE == null) {
            synchronized (TimerRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TimerRepository(application);
                }
            }
        }
        return INSTANCE;
    }

    private TimerRepository(Application application) {
        mDao = TimerDatabase.getDatabase(application).getDao();
        mAllTimerInfo = new LiveDataWrapper(mDao.getAllTimerInfo());
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

    private class LiveDataWrapper extends LiveData<HashMap<Integer, TimerInfo>> implements Observer<List<TimerInfo>> {

        private LiveData<List<TimerInfo>> mSourceLiveData;

        public LiveDataWrapper(LiveData<List<TimerInfo>> sourceLiveData) {
            mSourceLiveData = sourceLiveData;
            mSourceLiveData.observeForever(this);
        }

        @Override
        public void onChanged(@Nullable List<TimerInfo> sourceData) {
            HashMap<Integer, TimerInfo> temp = new HashMap<>();

            for (TimerInfo info: sourceData) {
                temp.put(info.getId(), info);
            }

            postValue(temp);
        }
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
