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

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_TIMER_ID = "org.kiyuko.timerwimer.TIMER_ID";

    private TimerViewModel mViewModel;

    private ScrollView mScrollView;
    private LinearLayout mLinearLayout;

    private LayoutInflater mInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mScrollView = findViewById(R.id.scrollView);
        mLinearLayout = findViewById(R.id.linearLayout);

        mViewModel = ViewModelProviders.of(this).get(TimerViewModel.class);
        mViewModel.getAllTimerInfo().observe(this, new Observer<HashMap<Integer, TimerInfo>>() {
            @Override
            public void onChanged(@Nullable HashMap<Integer, TimerInfo> allTimerInfo) {
                updateInterfaceInfo(allTimerInfo);
            }
        });
        mViewModel.getAllTimerState().observe(this, new Observer<HashMap<Integer, TimerState>>() {
            @Override
            public void onChanged(@Nullable HashMap<Integer, TimerState> allTimerState) {
                updateInterfaceState(allTimerState);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.create:
                Intent intent = new Intent(this, CreateActivity.class);
                startActivity(intent);
                /*
                mScrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
                */
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.editButton: {
                Intent intent = new Intent(this, EditActivity.class);

                View timerView = (View) view.getParent();
                intent.putExtra(EXTRA_TIMER_ID, timerView.getId());

                startActivity(intent);
                break;
            }
            case R.id.deleteButton: {
                View timerView = (View) view.getParent();

                TimerInfo info = new TimerInfo();
                info.setId(timerView.getId());

                mViewModel.delete(info);
                break;
            }
            case R.id.actionButton: {
                View timerView = (View) view.getParent();

                TimerState state = new TimerState();
                state.setId(timerView.getId());

                mViewModel.action(state);
                break;
            }
        }
    }

    private void addViewFor(TimerInfo info) {
        View timerView = mInflater.inflate(R.layout.view_timer, null);
        timerView.setId(info.getId());

        TextView time = timerView.findViewById(R.id.time);
        time.setText("000000");

        TextView label = timerView.findViewById(R.id.label);
        label.setText(info.getLabel());

        MaterialButton editButton = timerView.findViewById(R.id.editButton);
        editButton.setOnClickListener(this);

        MaterialButton deleteButton = timerView.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(this);

        MaterialButton actionButton = timerView.findViewById(R.id.actionButton);
        actionButton.setOnClickListener(this);

        mLinearLayout.addView(timerView);
    }

    private void updateViewFor(TimerState state) {
        View timerView = mLinearLayout.findViewById(state.getId());

        if (timerView == null) {
            return;
        }

        TextView time = timerView.findViewById(R.id.time);
        time.setText(String.format("%06d", state.getCurrentTime()));
    }

    private void updateInterfaceInfo(HashMap<Integer, TimerInfo> allTimerInfo) {
        mLinearLayout.removeAllViews();

        for (TimerInfo info: allTimerInfo.values()) {
            addViewFor(info);
        }
    }

    private void updateInterfaceState(HashMap<Integer, TimerState> allTimerState) {
        for (TimerState state: allTimerState.values()) {
            updateViewFor(state);
        }
    }
}
