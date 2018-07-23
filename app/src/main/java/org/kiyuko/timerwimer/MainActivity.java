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

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_TIMER_ID = "org.kiyuko.timerwimer.TIMER_ID";

    private ScrollView scrollView = null;
    private LinearLayout linearLayout = null;

    private TimerViewModel viewModel = null;
    private LayoutInflater mInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        scrollView = findViewById(R.id.scrollView);
        linearLayout = findViewById(R.id.linearLayout);

        viewModel = ViewModelProviders.of(this).get(TimerViewModel.class);
        viewModel.getAllTimerInfo().observe(this, new Observer<List<TimerInfo>>() {
            @Override
            public void onChanged(@Nullable List<TimerInfo> infos) {
                updateInterface(infos);
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
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
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

                viewModel.delete(info);
                break;
            }
        }
    }

    private void addViewFor(TimerInfo info) {
        View timerView = mInflater.inflate(R.layout.view_timer, null);
        timerView.setId(info.getId());

        TextView time = timerView.findViewById(R.id.time);
        time.setText(String.format("%06d", info.getTargetTime()));

        TextView label = timerView.findViewById(R.id.label);
        label.setText(info.getLabel());

        MaterialButton editButton = timerView.findViewById(R.id.editButton);
        editButton.setOnClickListener(this);

        MaterialButton deleteButton = timerView.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(this);

        linearLayout.addView(timerView);
    }

    private void updateInterface(List<TimerInfo> infos) {
        linearLayout.removeAllViews();
        for (int i = 0; i < infos.size(); i++) {
            addViewFor(infos.get(i));
        }
    }
}
