/* Timer Wimer - Countdown timers for your device
 * Copyright (C) 2013-2015  Andrea Bolognani <eof@kiyuko.org>
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

var timerwimer = {};

(function() {

	// Date.now() polyfill
	// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Date/now
	if (!Date.now)
	{
		Date.now = function now()
		{
			return new Date().getTime();
		};
	}


	// === Utility functions === //

	var Util;

	Util = {};

	Util.numberToDisplayString = function(number)
	{
		var display;

		if (number < 10)
		{
			display = "0";
		}
		else
		{
			display = "";
		}
		display += number;

		return display;
	};

	timerwimer.Util = Util;


	// === Timer class === //

	var Timer;

	Timer = function()
	{
		this._label = "";
		this._targetMinutes = 0;
		this._targetSeconds = 0;

		this._state = Timer.State.IDLE;
		this._targetTime = 0;
		this._remainingTime = 0;
		this._lastUpdateTime = 0;

		this._rootElement = null;
		this._labelElement = null;
		this._remainingElement = null;

		this._prepare();
	};

	Timer.State =
	{
		IDLE:    "IDLE",
		RUNNING: "RUNNING",
		PAUSED:  "PAUSED",
		ELAPSED: "ELAPSED"
	};

	Timer.prototype.start = function()
	{
		if (this.getState() == Timer.State.IDLE ||
		    this.getState() == Timer.State.PAUSED)
		{
			// Don't start a timer with zero target time
			if (this._targetTime <= 0)
			{
				return;
			}

			this._state = Timer.State.RUNNING;
			this._lastUpdateTime = Date.now();

			this.update();
		}
	};

	Timer.prototype.pause = function()
	{
		if (this.getState() == Timer.State.RUNNING)
		{
			this._interval = null;

			this._state = Timer.State.PAUSED;
			this._lastUpdateTime = Date.now();
		}
	};

	Timer.prototype.reset = function()
	{
		this._state = Timer.State.IDLE;
		this._remainingTime = this._targetTime;
		this._lastUpdateTime = 0;
	};

	Timer.prototype.action = function()
	{
		switch (this.getState())
		{
			case Timer.State.IDLE:
				this.start();
				break;
			case Timer.State.RUNNING:
				this.pause();
				break;
			case Timer.State.PAUSED:
				this.start();
				break;
			case Timer.State.ELAPSED:
				this.reset();
				break;
		}
	};

	Timer.prototype._prepare = function()
	{
		// <li>
		//   <a href="#">
		//     <div class="timer-remaining"></div>
		//     <div class="timer-label"></div>
		//   </a>
		//   <a href="#"></a>
		// </li>

		var li;
		var div;
		var a;

		// Root element

		li = document.createElement("li");
		this._rootElement = $(li);

		// Action button

		a = document.createElement("a");
		li.appendChild(a);

		// Perform a state-sensitive operation on tap
		$(a).on("tap", this.action.bind(this));

		// Reset the timer on long press
		$(a).on("taphold", this.reset.bind(this));

		// Remaining time

		div = document.createElement("div");
		div.className = "timer-remaining";
		a.appendChild(div);

		this._remainingElement = div;

		// Label

		div = document.createElement("div");
		div.className = "timer-label";
		a.appendChild(div);

		this._labelElement = div;

		// Edit button

		a = document.createElement("a");
		li.appendChild(a);

		$(a).on("tap", function() {

			var editLabel;
			var editMinutes;
			var editSeconds;
			var deleteButton;

			// Load and enhance page in advance.
			// This is required to ensure sliders are properly set
			// when the page is displayed to the user
			$.mobile.loadPage("#edit");

			editLabel = $("#edit-label");
			editMinutes = $("#edit-minutes");
			editSeconds = $("#edit-seconds");
			deleteButton = $("#delete");

			// Set values
			editLabel.val(this.getLabel());
			editMinutes.val(this.getTargetMinutes());
			editSeconds.val(this.getTargetSeconds());

			// Remove previous event handlers
			editLabel.off("change");
			editMinutes.off("change");
			editSeconds.off("change");
			deleteButton.off("tap");

			// Install new event handlers
			editLabel.on("change", function() {
				this.setLabel(editLabel.val());
			}.bind(this));
			editMinutes.on("change", function() {
				this.setTargetMinutes(editMinutes.val());
			}.bind(this));
			editSeconds.on("change", function() {
				this.setTargetSeconds(editSeconds.val());
			}.bind(this));
			deleteButton.on("tap", function() {
				$(this).trigger("deleteRequest");
			}.bind(this));

			// Change page
			$.mobile.changePage("#edit",
			                    { transition: "slide" });
		}.bind(this));
	};

	Timer.prototype.update = function()
	{
		var now;

		if (this.getState() == Timer.State.RUNNING)
		{
			now = Date.now();

			this._remainingTime -= now - this._lastUpdateTime;
			this._lastUpdateTime = now;

			if (this._remainingTime <= 0)
			{
				this._remainingTime = 0;
				this._state = Timer.State.ELAPSED;
			}

		}

		this._labelElement.innerHTML = this.getLabel();
		this._remainingElement.innerHTML = Util.numberToDisplayString(this.getRemainingMinutes()) +
		                                   ":" +
		                                   Util.numberToDisplayString(this.getRemainingSeconds());
	};

	Timer.prototype.setLabel = function(label)
	{
		this._label = label;
	};

	Timer.prototype.getLabel = function()
	{
		return this._label;
	};

	Timer.prototype.setTargetMinutes = function(targetMinutes)
	{
		// Changing the target time causes the timer to reset
		this.reset();

		// Make sure the value is in the permitted range
		if (targetMinutes < 0) {
			targetMinutes = 0;
		}
		if (targetMinutes > 99) {
			targetMinutes = 99;
		}

		this._targetMinutes = targetMinutes;

		this._targetTime = ((this._targetMinutes * 60) + this._targetSeconds) * 1000;
		this._remainingTime = this._targetTime;
	};

	Timer.prototype.getTargetMinutes = function()
	{
		return this._targetMinutes;
	};

	Timer.prototype.setTargetSeconds = function(targetSeconds)
	{
		// Changing the target time causes the timer to reset
		this.reset();

		// Make sure the value is in the permitted range
		if (targetSeconds < 0) {
			targetSeconds = 0;
		}
		if (targetSeconds > 59) {
			targetSeconds = 59;
		}

		this._targetSeconds = targetSeconds;

		this._targetTime = ((this._targetMinutes * 60) + this._targetSeconds) * 1000;
		this._remainingTime = this._targetTime;
	};

	Timer.prototype.getTargetSeconds = function()
	{
		return this._targetSeconds;
	};

	Timer.prototype.getState = function()
	{
		return this._state;
	};

	Timer.prototype.getRemainingMinutes = function()
	{
		return Math.floor(Math.ceil(this._remainingTime / 1000) / 60);
	};

	Timer.prototype.getRemainingSeconds = function()
	{
		return Math.ceil(this._remainingTime / 1000) % 60;
	};

	Timer.prototype.getRootElement = function()
	{
		return this._rootElement;
	};

	timerwimer.Timer = Timer;


	// === TimerList class === //

	var TimerList;

	TimerList = function()
	{
		this._list = [];
		this._rootElement = null;

		this._prepare();
	};

	TimerList.prototype.add = function(timer)
	{
		this._list.push(timer);

		this.getRootElement().append(timer.getRootElement());

		$(timer).on("deleteRequest", function() {

			// Remove the timer from the list
			this.remove(timer);

			// Return to the main page
			$.mobile.changePage("#main",
			                    { transition: "slide",
			                      reverse:    true });
		}.bind(this));

		this.getRootElement().listview("refresh");
	};

	TimerList.prototype.remove = function(timer)
	{
		var oldList;

		// Replace the timer list with a new, empty one
		oldList = this._list;
		this._list = [];

		// Copy all timers to the new list...
		for (var i = 0; i < oldList.length; i++)
		{
			// ... except the one we're removing
			if (oldList[i] != timer)
			{
				this._list.push(oldList[i]);
			}
		}

		// Remove the timer from the document
		timer.getRootElement().remove();

		this.getRootElement().listview("refresh");
	};

	TimerList.prototype.each = function(f)
	{
		for (var i = 0; i < this._list.length; i++)
		{
			f(this._list[i]);
		}
	};

	TimerList.prototype._prepare = function()
	{
		// <ul>
		// </ul>

		var ul;

		// Root element

		ul = document.createElement("ul");
		this._rootElement = $(ul);

		// Initialize listview with a suitable split icon
		$(ul).listview({ splitIcon: "edit" });
	};

	TimerList.prototype.update = function()
	{
		this.each(function(timer) {
			timer.update();
		});
	};

	TimerList.prototype.getRootElement = function()
	{
		return this._rootElement;
	};

	timerwimer.TimerList = TimerList;


	// === Application class === //

	var Application;

	Application = function()
	{
		this._timerList = null;
		this._interval = null;

		this._load();
	};

	Application.prototype._load = function()
	{
		var timer;
		var timerList;

		timerList = new TimerList();

		timer = new Timer();
		timer.setLabel("Nine minutes");
		timer.setTargetMinutes(9);
		timer.setTargetSeconds(0);
		timerList.add(timer);

		timer = new Timer();
		timer.setLabel("Three seconds");
		timer.setTargetMinutes(0);
		timer.setTargetSeconds(3);
		timerList.add(timer);

		timer = new Timer();
		timer.setLabel("One minute");
		timer.setTargetMinutes(1);
		timer.setTargetSeconds(0);
		timerList.add(timer);

		$("#timerlist").append(timerList.getRootElement());

		$("#add").on("tap", function() {

			// Add a new timer using default settings
			timerList.add(new Timer());

			// The panel doesn't close automatically
			$("#menu").panel("close");
		});

		this._timerList = timerList;
	};

	Application.prototype.run = function()
	{
		this._interval = setInterval(this.update.bind(this), 100);
	};

	Application.prototype.update = function()
	{
		this._timerList.update();
	};

	timerwimer.Application = Application;


	// === Exported functions === //

	timerwimer.main = function()
	{
		// Don't emit a tap event right after a taphold event
		$.event.special.tap.emitTapOnTaphold = false;

		$(document).on("pageinit", "#main", function() {
			new Application().run();
		});
	};
}());

timerwimer.main();
