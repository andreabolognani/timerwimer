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

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import java.util.List;

public class EditActivity extends AppCompatActivity {

    private TimerViewModel mViewModel;

    private int mTimerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent intent = getIntent();
        mTimerId = intent.getIntExtra(MainActivity.EXTRA_TIMER_ID, -1);

        mViewModel = ViewModelProviders.of(this).get(TimerViewModel.class);
        mViewModel.getAllTimerInfo().observe(this, new Observer<List<TimerInfo>>() {
            @Override
            public void onChanged(@Nullable List<TimerInfo> infos) {
                updateInterface(infos);
            }
        });
    }

    private void updateInterface(List<TimerInfo> infos) {
        for (int i = 0; i < infos.size(); i++) {
            TimerInfo info = infos.get(i);

            if (info.getId() == mTimerId) {
                EditText labelEditText = findViewById(R.id.labelEditText);
                labelEditText.setText(info.getLabel());
            }
        }
    }
}
