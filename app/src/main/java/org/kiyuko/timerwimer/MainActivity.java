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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    public static final String EXTRA_TIMER_ID = "org.kiyuko.timerwimer.TIMER_ID";

    private TimerViewModel mViewModel;

    private ScrollView mScrollView;
    private LinearLayout mLinearLayout;

    private LayoutInflater mInflater;

    private int mCurrentId;

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
        View timerView = (View) view.getParent();
        mCurrentId = timerView.getId();

        switch (view.getId()) {
            case R.id.menuButton: {
                PopupMenu popup = new PopupMenu(this, view);

                popup.setOnMenuItemClickListener(this);

                popup.inflate(R.menu.menu_timer);
                popup.show();

                break;
            }
            case R.id.actionButton: {
                TimerState state = new TimerState();
                state.setId(mCurrentId);

                mViewModel.action(state);
                break;
            }
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.edit: {
                Intent intent = new Intent(this, EditActivity.class);

                intent.putExtra(EXTRA_TIMER_ID, mCurrentId);

                startActivity(intent);
                return true;
            }
            case R.id.delete: {
                TimerInfo info = new TimerInfo();
                info.setId(mCurrentId);

                mViewModel.delete(info);
                return true;
            }
            default: {
                return false;
            }
        }
    }

    private void addViewFor(TimerInfo info) {
        View timerView = mInflater.inflate(R.layout.view_timer, null);
        timerView.setId(info.getId());

        TextView time = timerView.findViewById(R.id.time);
        time.setText("00:00");

        TextView label = timerView.findViewById(R.id.label);
        label.setText(info.getLabel());

        ImageButton menuButton = timerView.findViewById(R.id.menuButton);
        menuButton.setOnClickListener(this);

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
        MaterialButton actionButton = (MaterialButton) timerView.findViewById(R.id.actionButton);

        int currentTime = state.getCurrentTime();
        int milliseconds = currentTime % 1000;
        currentTime = (currentTime - milliseconds) / 1000;
        int seconds = currentTime % 60;
        currentTime = (currentTime - seconds) / 60;
        int minutes = currentTime;

        if (milliseconds > 0) {
            seconds++;
        }

        time.setText(String.format("%02d:%02d", minutes, seconds));

        actionButton.setText(state.getActionLabel());
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
