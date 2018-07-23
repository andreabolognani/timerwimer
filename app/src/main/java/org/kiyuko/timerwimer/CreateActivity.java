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

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

public class CreateActivity extends AppCompatActivity {

    private EditText mLabelEditText;
    private EditText mTargetTimeEditText;
    private TimerViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        mLabelEditText = findViewById(R.id.labelEditText);
        mTargetTimeEditText = findViewById(R.id.targetTimeEditText);

        mViewModel = ViewModelProviders.of(this).get(TimerViewModel.class);
    }

    private void updateModel() {
        TimerInfo info = new TimerInfo();

        info.setLabel(mLabelEditText.getText().toString());
        info.setTargetTime(Integer.parseInt(mTargetTimeEditText.getText().toString()));

        mViewModel.insert(info);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_create, menu);
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
