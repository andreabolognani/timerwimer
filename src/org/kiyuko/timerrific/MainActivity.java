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
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends BaseFragmentActivity implements ViewTreeObserver.OnGlobalLayoutListener, SlidingPaneLayout.PanelSlideListener, SelectionListener, DatabaseListener {

	private ActionBar mActionBar;
	private SlidingPaneLayout mSlidingPane;
	private BaseListFragment mNavigationFragment;
	private BaseFragment mContentsFragment;
	private TimerDatabase mDatabase;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		mActionBar = getSupportActionBar();
		mSlidingPane = (SlidingPaneLayout) findViewById(R.id.sliding_pane);

		mNavigationFragment = new NavigationFragment();

		if (getSelectionId() == Timer.INVALID_ID) {

			mContentsFragment = new MessageFragment();
		}
		else {

			mContentsFragment = new ViewDetailsFragment();
		}

		mDatabase = new TimerDatabase(this);

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
	protected void onDestroy() {

		mDatabase.close();

		super.onDestroy();
	}

	@Override
	public void onBackPressed() {

		if (mSlidingPane.isSlideable() && !mSlidingPane.isOpen()) {

			// Showing contents only: return to navigation
			mSlidingPane.openPane();
		}
		else {

			finish();
		}
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

			case R.id.action_add:

				addItem();

				return true;

			case R.id.action_edit:

				editItem();

				return true;

			case R.id.action_remove:

				removeItem();

				return true;

			case R.id.action_settings:

				Toast.makeText(this, getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();

				return true;

			default:

				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onSelectionChanged(long oldId, long newId) {

		if (oldId != newId) {

			// Replace contents fragment if selection has actually changed
			mContentsFragment = new ViewDetailsFragment();
		}

		// Update the contents
		getSupportFragmentManager().beginTransaction()
			.replace(R.id.contents_fragment, mContentsFragment)
		.commit();

		if (mSlidingPane.isSlideable() && mSlidingPane.isOpen()) {

			// Close the pane to focus on the contents
			mSlidingPane.closePane();
		}
		else {

			updateActionBar();
		}
	}

	@Override
	public void onItemAdded(long id) {

		mContentsFragment = new EditDetailsFragment();

		// Replace contents
		getSupportFragmentManager().beginTransaction()
			.replace(R.id.contents_fragment, mContentsFragment)
		.commit();

		if (mSlidingPane.isSlideable()) {

			// Single pane: focus on contents
			mSlidingPane.closePane();
		}
		else {

			// Dual pane: update action bar
			updateActionBar();
		}

		try {

			// Notify navigation fragment
			((DatabaseListener) mNavigationFragment).onItemAdded(id);
		}
		catch (ClassCastException e) {

			throw new ClassCastException(mNavigationFragment.getClass().getName()
					+ " must implement DatabaseListener");
		}
	}

	@Override
	public void onItemRemoved(long id) {

		if (mDatabase.isEmpty()) {

			mContentsFragment = new MessageFragment();
		}
		else {

			mContentsFragment = new ViewDetailsFragment();
		}

		// Replace contents
		getSupportFragmentManager().beginTransaction()
			.replace(R.id.contents_fragment, mContentsFragment)
		.commit();

		if (mSlidingPane.isSlideable()) {

			// Single pane: focus on navigation
			mSlidingPane.openPane();
		}
		else {

			// Dual pane: update action bar
			updateActionBar();
		}

		try {

			// Notify navigation fragment
			((DatabaseListener) mNavigationFragment).onItemRemoved(id);
		}
		catch (ClassCastException e) {

			throw new ClassCastException(mNavigationFragment.getClass().getName()
					+ " must implement DatabaseListener");
		}
	}

	@Override
	public void onItemModified(long id) {

		updateActionBar();

		try {

			// Notify navigation fragment
			((DatabaseListener) mNavigationFragment).onItemModified(id);
		}
		catch (ClassCastException e) {

			throw new ClassCastException(mNavigationFragment.getClass().getName()
					+ " must implement DatabaseListener");
		}
	}

	@Override
	public void onGlobalLayout() {

		updateActionBar();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

			mSlidingPane.getViewTreeObserver().removeOnGlobalLayoutListener(this);
		}
		else {

			mSlidingPane.getViewTreeObserver().removeGlobalOnLayoutListener(this);
		}
	}

	@Override
	public void onPanelOpened(View panel) {

		updateActionBar();
	}

	@Override
	public void onPanelClosed(View panel) {

		updateActionBar();
	}

	@Override
	public void onPanelSlide(View panel, float slideOffset) {}

	/**
	 * Add a new timer.
	 */
	private void addItem() {

		long id;

		id = mDatabase.newId();

		// Insert a new timer into the database
		mDatabase.put(new Timer(id,
				getString(R.string.new_timer),
				60));

		// Change selection
		setSelectionId(id);

		onItemAdded(id);
	}

	/**
	 * Remove selected timer.
	 */
	private void removeItem() {

		long id;

		id = getSelectionId();

		// Remove selected timer from the database
		mDatabase.remove(id);

		// Invalidate selection
		setSelectionId(findSelectionCandidate(id));

		onItemRemoved(id);
	}

	/**
	 * Edit selected timer.
	 */
	private void editItem() {

		mContentsFragment = new EditDetailsFragment();

		getSupportFragmentManager().beginTransaction()
			.replace(R.id.contents_fragment, mContentsFragment)
		.commit();

		updateActionBar();
	}

	/**
	 * Update contents of the action bar.
	 */
	private void updateActionBar() {

		Timer timer;

		if (mSlidingPane.isSlideable() && !mSlidingPane.isOpen()) {

			// Enable up navigation
			mActionBar.setHomeButtonEnabled(true);
			mActionBar.setDisplayHomeAsUpEnabled(true);

			timer = mDatabase.get(getSelectionId());

			if (timer != null) {

				// Use timer label as window title
				mActionBar.setTitle(timer.getLabel());
			}
		}
		else {

			// Disable up navigation
			mActionBar.setHomeButtonEnabled(false);
			mActionBar.setDisplayHomeAsUpEnabled(false);

			// Use application name as window title
			mActionBar.setTitle(R.string.app_name);
		}

		if (mSlidingPane.isSlideable()) {

			if (mSlidingPane.isOpen()) {

				// Single pane, open: navigation menu
				mNavigationFragment.setHasOptionsMenu(true);
				mContentsFragment.setHasOptionsMenu(false);
			}
			else {

				// Single pane, closed: contents menu
				mNavigationFragment.setHasOptionsMenu(false);
				mContentsFragment.setHasOptionsMenu(true);
			}
		}
		else {

			// Dual pane: both navigation and contents menu
			mNavigationFragment.setHasOptionsMenu(true);
			mContentsFragment.setHasOptionsMenu(true);
		}
	}

	private long findSelectionCandidate(long oldSelectionId) {

		long id;
		long lowestId;
		long highestId;

		lowestId = mDatabase.getLowestId();
		highestId = mDatabase.getHighestId();

		// Look forward for a selection candidate
		for (id = oldSelectionId; id <= highestId; id++) {

			if (mDatabase.get(id) != null) {

				return id;
			}
		}

		// Look backwards for a selection candidate
		for (id = oldSelectionId; id >= lowestId; id--) {

			if (mDatabase.get(id) != null) {

				return id;
			}
		}

		// Fall back
		return Timer.INVALID_ID;
	}
}
