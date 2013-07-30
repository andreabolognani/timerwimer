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

import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends SherlockFragmentActivity implements ViewTreeObserver.OnGlobalLayoutListener, SlidingPaneLayout.PanelSlideListener {

	private ActionBar mActionBar;
	private SlidingPaneLayout mSlidingPane;
	private NavigationFragment mNavigationFragment;
	private ContentsFragment mContentsFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		mActionBar = getSupportActionBar();
		mSlidingPane = (SlidingPaneLayout) findViewById(R.id.sliding_pane);
		mNavigationFragment = new NavigationFragment();
		mContentsFragment = new ContentsFragment();

		// Register listeners for layout changes
		mSlidingPane.setPanelSlideListener(this);
		mSlidingPane.getViewTreeObserver().addOnGlobalLayoutListener(this);

		getSupportFragmentManager().beginTransaction()
			.replace(R.id.navigation_fragment, mNavigationFragment)
			.replace(R.id.contents_fragment, mContentsFragment)
		.commit();

		// Make sure the navigation is shown on startup
		mSlidingPane.openPane();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		super.onCreateOptionsMenu(menu);

		getSupportMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

			case android.R.id.home:

				// Open the pane to reveal the navigation
				mSlidingPane.openPane();

				return true;

			case R.id.action_settings:

				Toast.makeText(this, "Not implemented (MainActivity)", Toast.LENGTH_SHORT).show();

				return true;

			default:

				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onGlobalLayout() {

		if (mSlidingPane.isSlideable() && !mSlidingPane.isOpen()) {

			// If only the contents are shown, the panel is closed
			onPanelClosed(mSlidingPane);
		}
		else {

			// Otherwise, the panel is open
			onPanelOpened(mSlidingPane);
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

			mSlidingPane.getViewTreeObserver().removeOnGlobalLayoutListener(this);
		}
		else {

			mSlidingPane.getViewTreeObserver().removeGlobalOnLayoutListener(this);
		}
	}

	@Override
	public void onPanelOpened(View panel) {

		mActionBar.setDisplayHomeAsUpEnabled(false);

		if (mSlidingPane.isSlideable()) {

			// Only the navigation is active
			mNavigationFragment.setHasOptionsMenu(true);
			mContentsFragment.setHasOptionsMenu(false);
		}
		else {

			// Both the navigation and the contents are active
			mNavigationFragment.setHasOptionsMenu(false);
			mContentsFragment.setHasOptionsMenu(true);
		}
	}

	@Override
	public void onPanelClosed(View panel) {

		mActionBar.setDisplayHomeAsUpEnabled(true);

		// Only the contents are active
		mNavigationFragment.setHasOptionsMenu(false);
		mContentsFragment.setHasOptionsMenu(true);
	}

	@Override
	public void onPanelSlide(View panel, float slideOffset) {}
}
