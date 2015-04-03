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

	// Utility functions

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

	// Timer class

	var Timer;

	Timer = function()
	{
		this._label = "";
		this._targetMinutes = 0;
		this._targetSeconds = 0;

		this._state = Timer.State.STOPPED;
		this._targetTime = 0;
		this._remainingTime = 0;
		this._lastUpdateTime = 0;
		this._interval = null;
	};

	Timer.State =
	{
		STOPPED:  "STOPPED",
		RUNNING:  "RUNNING",
		PAUSED:   "PAUSED",
		FINISHED: "FINISHED"
	};

	Timer.prototype.start = function()
	{
		if (this.getState() == Timer.State.STOPPED ||
		    this.getState() == Timer.State.PAUSED)
		{
			this._state = Timer.State.RUNNING;
			this._lastUpdateTime = Date.now();

			this._update();
			this._interval = setInterval(this._update.bind(this), 100);
		}
	};

	Timer.prototype.pause = function()
	{
		if (this.getState() == Timer.State.RUNNING)
		{
			clearInterval(this._interval);
			this._interval = null;

			this._state = Timer.State.PAUSED;
			this._lastUpdateTime = Date.now();
		}
	};

	Timer.prototype.toggle = function()
	{
		switch (this.getState())
		{
			case Timer.State.RUNNING:
				this.pause();
				break;

			case Timer.State.PAUSED:
				this.start();
				break;
		}
	};

	Timer.prototype.stop = function()
	{
		clearInterval(this._interval);
		this._interval = null;

		this._state = Timer.State.STOPPED;
		this._remainingTime = this._targetTime;
		this._lastUpdateTime = 0;
	};

	Timer.prototype.action = function()
	{
		switch (this.getState())
		{
			case Timer.State.STOPPED:
				this.start();
				break;
			case Timer.State.RUNNING:
				this.pause();
				break;
			case Timer.State.PAUSED:
				this.start();
				break;
			case Timer.State.FINISHED:
				this.stop();
				break;
		}
	};

	Timer.prototype._finish = function()
	{
		clearInterval(this._interval);
		this._interval = null;

		this._state = Timer.State.FINISHED;
	};

	Timer.prototype._update = function()
	{
		var now;

		now = Date.now();

		this._remainingTime -= now - this._lastUpdateTime;
		this._lastUpdateTime = now;

		if (this._remainingTime <= 0)
		{
			this._remainingTime = 0;
			this._finish();
		}
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
		// Changing the target time causes the timer to stop
		this.stop();

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
		// Changing the target time causes the timer to stop
		this.stop();

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

	timerwimer.Timer = Timer;

	// TimerWidget class

	var TimerWidget;

	TimerWidget = function(timer)
	{
		this._timer = timer;

		this._element = null;
		this._label = null;
		this._remaining = null;

		this._prepare();
		this.update();
	};

	TimerWidget.prototype._prepare = function()
	{
		// <li>
		//   <a href="#">
		//     <div class="timer-remaining"></div>
		//     <div class="timer-label"></div>
		//   </a>
		//   <a href="#"></a>
		// </li>

		var timer;

		var li;
		var div;
		var a;

		timer = this._timer;

		// Root element

		li = document.createElement("li");

		this._element = $(li);

		// Action button

		a = document.createElement("a");
		li.appendChild(a);

		$(a).on("tap", function() {

			// Perform a state-sensitive operation on tap
			timer.action();
		});
		$(a).on("taphold", function() {

			// Stop the timer on long press
			timer.stop();
		});

		// Remaining time

		div = document.createElement("div");
		div.className = "timer-remaining";
		a.appendChild(div);

		this._remaining = div;

		// Label

		div = document.createElement("div");
		div.className = "timer-label";
		a.appendChild(div);

		this._label = div;

		// Edit button

		a = document.createElement("a");
		li.appendChild(a);

		$(a).on("tap", function() {

			var editLabel;
			var editMinutes;
			var editSeconds;

			// Load and enhance page in advance.
			// This is required to ensure sliders are properly set
			// when the page is displayed to the user
			$.mobile.loadPage("#edit");

			editLabel = $("#edit-label");
			editMinutes = $("#edit-minutes");
			editSeconds = $("#edit-seconds");

			// Set values
			editLabel.val(timer.getLabel());
			editMinutes.val(timer.getTargetMinutes());
			editSeconds.val(timer.getTargetSeconds());

			// Remove previous event handlers
			editLabel.off("change");
			editMinutes.off("change");
			editSeconds.off("change");

			// Install new event handlers
			editLabel.on("change", function() {
				timer.setLabel(editLabel.val());
			});
			editMinutes.on("change", function() {
				timer.setTargetMinutes(editMinutes.val());
			});
			editSeconds.on("change", function() {
				timer.setTargetSeconds(editSeconds.val());
			});

			// Change page
			$.mobile.changePage("#edit",
			                    { transition: "slide" });
		});
	};

	TimerWidget.prototype.update = function()
	{
		this._label.innerHTML = this._timer.getLabel();
		this._remaining.innerHTML = Util.numberToDisplayString(this._timer.getRemainingMinutes()) +
		                            ":" +
		                            Util.numberToDisplayString(this._timer.getRemainingSeconds());
	};

	TimerWidget.prototype.getTimer = function()
	{
		return this._timer;
	};

	TimerWidget.prototype.getElement = function()
	{
		return this._element;
	};

	timerwimer.TimerWidget = TimerWidget;

	// TimerListWidget class

	var TimerListWidget;

	TimerListWidget = function()
	{
		this._list = [];
		this._interval = null;
		this._element = null;

		this._prepare();
	};

	TimerListWidget.prototype._prepare = function()
	{
		// <ul>
		// </ul>

		var ul;

		// Root element

		ul = document.createElement("ul");

		$(ul).listview({ splitIcon: "edit" });

		this._element = $(ul);

		this._interval = setInterval(this.update.bind(this), 100);
	};

	TimerListWidget.prototype.add = function(timer)
	{
		this._list.push(timer);

		this._element.append(timer.getElement());
		this._element.listview("refresh");
	};

	TimerListWidget.prototype.each = function(f)
	{
		for (var i = 0; i < this._list.length; i++)
		{
			f(this._list[i]);
		}
	};

	TimerListWidget.prototype.update = function()
	{
		this.each(function(timerWidget) {
			timerWidget.update();
		});
		this._element.listview("refresh");
	};

	TimerListWidget.prototype.getElement = function()
	{
		return this._element;
	};

	timerwimer.TimerListWidget = TimerListWidget;

	// Exported functions

	timerwimer.main = function()
	{
		// Don't emit a tap event right after a taphold event
		$.event.special.tap.emitTapOnTaphold = false;

		$(document).on("pageinit", "#main", function() {

			var timer;
			var widget;
			var widgetList;

			widgetList = new TimerListWidget();

			timer = new Timer();
			timer.setLabel("Nine minutes");
			timer.setTargetMinutes(9);
			timer.setTargetSeconds(0);

			widget = new TimerWidget(timer);
			widgetList.add(widget);

			timer = new Timer();
			timer.setLabel("Eleven seconds");
			timer.setTargetMinutes(0);
			timer.setTargetSeconds(11);

			widget = new TimerWidget(timer);
			widgetList.add(widget);

			timer = new Timer();
			timer.setLabel("One minute");
			timer.setTargetMinutes(1);
			timer.setTargetSeconds(0);

			widget = new TimerWidget(timer);
			widgetList.add(widget);

			$("#timerlist").append(widgetList.getElement());
		});
	};
}());

timerwimer.main();
