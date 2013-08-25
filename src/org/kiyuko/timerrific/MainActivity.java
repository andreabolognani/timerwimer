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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends BaseFragmentActivity implements ViewTreeObserver.OnGlobalLayoutListener, SlidingPaneLayout.PanelSlideListener, SelectionListener, DatabaseListener {

	private static final String PREFERENCES_FILE = "preferences";
	private static final String KEY_MODE = "mode";

	private static final int MODE_VIEW = 1;
	private static final int MODE_EDIT = 2;

	private ActionBar mActionBar;
	private SlidingPaneLayout mSlidingPane;
	private BaseListFragment mNavigationFragment;
	private BaseFragment mContentsFragment;
	private TimerDatabase mDatabase;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {

			// Force view mode on startup
			setMode(MODE_VIEW);
		}

		mDatabase = TimerDatabase.getInstance(this);

		mActionBar = getSupportActionBar();
		mSlidingPane = (SlidingPaneLayout) findViewById(R.id.sliding_pane);

		mNavigationFragment = new NavigationFragment();

		if (mDatabase.size() < 1) {

			mContentsFragment = new MessageFragment();
		}
		else {

			if (getMode() == MODE_EDIT) {

				mContentsFragment = new EditDetailsFragment();
			}
			else {

				mContentsFragment = new ViewDetailsFragment();
			}
		}

		// Register listeners for layout changes
		mSlidingPane.setPanelSlideListener(this);
		mSlidingPane.getViewTreeObserver().addOnGlobalLayoutListener(this);

		getSupportFragmentManager().beginTransaction()
			.replace(R.id.navigation_fragment, mNavigationFragment)
			.replace(R.id.contents_fragment, mContentsFragment)
		.commit();
	}

	@Override
	public void onBackPressed() {

		if (mSlidingPane.isSlideable() && !mSlidingPane.isOpen()) {

			// Showing contents only: return to navigation
			mSlidingPane.openPane();
		}
		else {

			mDatabase.commit();

			finish();
		}
	}

	@Override
	protected void onPause() {

		mDatabase.commit();

		super.onPause();
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

				promptRemoveItem();

				return true;

			case R.id.action_accept:

				acceptChanges();

				return true;

			case R.id.action_settings:

				Toast.makeText(this, getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();

				return true;

			default:

				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onSelectionChanged(int oldPosition, int newPosition) {

		setMode(MODE_VIEW);

		if (oldPosition != newPosition) {

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
	public void onItemAdded(int position) {

		setMode(MODE_EDIT);

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
			((DatabaseListener) mNavigationFragment).onItemAdded(position);
		}
		catch (ClassCastException e) {

			throw new ClassCastException(mNavigationFragment.getClass().getName()
					+ " must implement DatabaseListener");
		}
	}

	@Override
	public void onItemRemoved(int position) {

		setMode(MODE_VIEW);

		if (mDatabase.size() < 1) {

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
			((DatabaseListener) mNavigationFragment).onItemRemoved(position);
		}
		catch (ClassCastException e) {

			throw new ClassCastException(mNavigationFragment.getClass().getName()
					+ " must implement DatabaseListener");
		}
	}

	@Override
	public void onItemModified(int position) {

		updateActionBar();

		try {

			// Notify navigation fragment
			((DatabaseListener) mNavigationFragment).onItemModified(position);
		}
		catch (ClassCastException e) {

			throw new ClassCastException(mNavigationFragment.getClass().getName()
					+ " must implement DatabaseListener");
		}
	}

	@Override
	public void onGlobalLayout() {

		if (getMode() == MODE_EDIT) {

			// Always focus on content in edit mode
			mSlidingPane.closePane();
		}

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

		InputMethodManager imm;

		// Hide soft keyboard
		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mSlidingPane.getWindowToken(), 0);

		setMode(MODE_VIEW);

		if (mContentsFragment instanceof EditDetailsFragment) {

			// Switch to view mode
			mContentsFragment = new ViewDetailsFragment();

			getSupportFragmentManager().beginTransaction()
				.replace(R.id.contents_fragment, mContentsFragment)
			.commit();
		}

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

		int position;

		position = mDatabase.size();

		// Insert a new timer into the database
		mDatabase.add(new Timer(getString(R.string.no_label), 30));

		// Change selection
		setSelectedPosition(mDatabase.size() - 1);

		onItemAdded(position);
	}

	/**
	 * Prompt the user to confirm timer removal.
	 */
	private void promptRemoveItem() {

		AlertDialog.Builder builder;

		builder = new AlertDialog.Builder(this);

		builder.setTitle(R.string.question_remove_timer);
		builder.setMessage(R.string.warning_action_cannot_be_undone);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				// Actually remove the timer
				removeItem();
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {}
		});

		builder.create().show();
	}

	/**
	 * Remove selected timer.
	 */
	private void removeItem() {

		int position;

		position = getSelectedPosition();

		// Remove selected timer from the database
		mDatabase.remove(position);

		// Invalidate selection
		setSelectedPosition(findSelectionCandidate(position));

		onItemRemoved(position);
	}

	/**
	 * Edit selected timer.
	 */
	private void editItem() {

		setMode(MODE_EDIT);

		mContentsFragment = new EditDetailsFragment();

		getSupportFragmentManager().beginTransaction()
			.replace(R.id.contents_fragment, mContentsFragment)
		.commit();

		updateActionBar();
	}

	/**
	 * Accept changes and stop editing.
	 */
	private void acceptChanges() {

		InputMethodManager imm;

		// Hide soft keyboard
		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mSlidingPane.getWindowToken(), 0);

		setMode(MODE_VIEW);

		// Switch to view mode
		mContentsFragment = new ViewDetailsFragment();

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

			timer = mDatabase.get(getSelectedPosition());

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

	private int findSelectionCandidate(int oldSelectedPosition) {

		int position;
		int lowestPosition;
		int highestPosition;

		lowestPosition = 0;
		highestPosition = mDatabase.size() - 1;

		// Look forward for a selection candidate
		for (position = oldSelectedPosition; position <= highestPosition; position++) {

			if (mDatabase.get(position) != null) {

				return position;
			}
		}

		// Look backwards for a selection candidate
		for (position = oldSelectedPosition; position >= lowestPosition; position--) {

			if (mDatabase.get(position) != null) {

				return position;
			}
		}

		// Fall back
		return 0;
	}

	/**
	 * Switch between view and edit mode.
	 *
	 * @param mode Either {@link #MODE_VIEW} or {@link #MODE_EDIT}.
	 */
	private void setMode(int mode) {

		SharedPreferences preferences;

		preferences = getSharedPreferences(PREFERENCES_FILE, MODE_PRIVATE);

		preferences.edit()
			.putInt(KEY_MODE, mode)
		.commit();
	}

	/**
	 * Get current mode.
	 *
	 * @return Either {@link #MODE_VIEW} or {@link #MODE_EDIT}.
	 */
	private int getMode() {

		SharedPreferences preferences;

		preferences = getSharedPreferences(PREFERENCES_FILE, MODE_PRIVATE);

		return preferences.getInt(KEY_MODE, MODE_VIEW);
	}
}
