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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;

public class EditActivity extends AppCompatActivity implements TextView.OnEditorActionListener {

    private InputMethodManager mInputMethodManager;
    private EditText mLabelEditText;
    private EditText mTargetTimeEditText;
    private TimerViewModel mViewModel;
    private int mTimerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        mInputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

        mLabelEditText = findViewById(R.id.labelEditText);
        mTargetTimeEditText = findViewById(R.id.targetTimeEditText);

        Intent intent = getIntent();
        mTimerId = intent.getIntExtra(MainActivity.EXTRA_TIMER_ID, -1);

        mViewModel = ViewModelProviders.of(this).get(TimerViewModel.class);
        mViewModel.getAllTimerInfo().observe(this, new Observer<HashMap<Integer, TimerInfo>>() {
            @Override
            public void onChanged(@Nullable HashMap<Integer, TimerInfo> allTimerInfo) {
                updateInterface(allTimerInfo);
            }
        });

        mLabelEditText.setOnEditorActionListener(this);
    }

    private void updateInterface(HashMap<Integer, TimerInfo> allTimerInfo) {
        if (allTimerInfo.containsKey(mTimerId)) {
            TimerInfo info = allTimerInfo.get(mTimerId);

            mLabelEditText.setText(info.getLabel());
            mTargetTimeEditText.setText(String.format("%d", info.getTargetTime() / 1000));
        }
    }

    private void updateModel() {
        TimerInfo info = new TimerInfo();

        info.setId(mTimerId);
        info.setLabel(mLabelEditText.getText().toString());
        info.setTargetTime(Integer.parseInt(mTargetTimeEditText.getText().toString()) * 1000);

        mViewModel.update(info);
    }

    @Override
    public boolean onEditorAction(TextView textView, int action, KeyEvent keyEvent) {
        if (action == EditorInfo.IME_ACTION_DONE) {
            mInputMethodManager.hideSoftInputFromWindow(mLabelEditText.getWindowToken(), 0);
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.confirm:
                updateModel();
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
