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
import android.support.v4.widget.SlidingPaneLayout;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;

public class MainActivity extends SherlockFragmentActivity {

	private SlidingPaneLayout mSlidingPane;
	private NavigationFragment mNavigationFragment;
	private ContentsFragment mContentsFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		mSlidingPane = (SlidingPaneLayout) findViewById(R.id.sliding_pane);
		mNavigationFragment = new NavigationFragment();
		mContentsFragment = new ContentsFragment();

		getSupportFragmentManager().beginTransaction()
			.replace(R.id.navigation_fragment, mNavigationFragment)
			.replace(R.id.contents_fragment, mContentsFragment)
		.commit();

		// Make sure the navigation is shown on startup
		mSlidingPane.openPane();

		mNavigationFragment.setHasOptionsMenu(true);
		mContentsFragment.setHasOptionsMenu(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		super.onCreateOptionsMenu(menu);

		getSupportMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

}
