/* Timerrific -- Countdown timers for Android devices
 * Copyright (C) 2013  Andrea Bolognani <eof@kiyuko.org>
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

package org.kiyuko.timerrific;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class ContentsFragment extends BaseFragment {

	private SherlockFragmentActivity mActivity;
	private TextView mLabelView;
	private TimerDatabase mDatabase;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view;
		Timer timer;

		super.onCreateView(inflater, container, savedInstanceState);

		view = inflater.inflate(R.layout.fragment_contents, container, false);

		mActivity = getSherlockActivity();
		mLabelView = (TextView) view.findViewById(R.id.label_view);

		mDatabase = new TimerDatabase(mActivity);

		timer = mDatabase.get(getSelectedId());

		if (timer != null && timer.getLabel() != null) {

			mLabelView.setText("Label: " + timer.getLabel());
		}

		return view;
	}

	@Override
	public void onDestroy() {

		mDatabase.close();

		super.onDestroy();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		super.onCreateOptionsMenu(menu, inflater);

		mActivity.getSupportMenuInflater().inflate(R.menu.contents, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Timer timer;

		switch (item.getItemId()) {

			case R.id.action_remove:

				timer = mDatabase.get(getSelectedId());
				Toast.makeText(mActivity, "Selected: " + timer.getLabel(), Toast.LENGTH_SHORT).show();

				return true;

			default:

				return super.onOptionsItemSelected(item);
		}
	}
}
