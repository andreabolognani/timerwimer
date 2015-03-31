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

	Util.secondsToDisplayString = function(time)
	{
		var display;
		var minutes;
		var seconds;

		seconds = time % 60;
		minutes = (time - seconds) / 60;

		display = "";

		if (minutes < 10)
		{
			display += "0";
		}
		display += minutes + ":";

		if (seconds < 10)
		{
			display += "0";
		}
		display += seconds;

		return display;
	};

	timerwimer.Util = Util;

	// Timer class

	var Timer;

	Timer = function()
	{
		this._label = "";
		this._targetTime = 0;

		this._state = Timer.State.STOPPED;
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

	Timer.prototype.increaseTargetTime = function()
	{
		var remaining;

		remaining = this.getRemainingSeconds();
		remaining -= (remaining % 60);
		remaining += 60;

		this.setTargetSeconds(remaining);
	};

	Timer.prototype.decreaseTargetTime = function()
	{
		var remaining;
		var mod;

		remaining = this.getRemainingSeconds();
		mod = remaining % 60;
		remaining -= mod;

		if (mod == 0)
		{
			remaining -= 60;
		}

		this.setTargetSeconds(remaining);
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

	Timer.prototype.setTargetSeconds = function(targetSeconds)
	{
		// Changing the target time causes the timer to stop
		this.stop();

		this._targetTime = targetSeconds * 1000;
		this._remainingTime = this._targetTime;
	};

	Timer.prototype.getTargetSeconds = function()
	{
		return this._targetTime / 1000;
	};

	Timer.prototype.getState = function()
	{
		return this._state;
	};

	Timer.prototype.getRemainingSeconds = function()
	{
		return Math.ceil(this._remainingTime / 1000);
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

		$(a).on("click", function() {
			timer.action();
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

		$(a).on("click", function() {
			$("#edit-label").val(timer.getLabel());
			$("#edit-minutes").val(0);
			$("#edit-seconds").val(timer.getTargetSeconds());
			$.mobile.changePage("#edit",
					    { transition: "slide" });
		});
	};

	TimerWidget.prototype.update = function()
	{
		this._label.innerHTML = this._timer.getLabel();
		this._remaining.innerHTML = Util.secondsToDisplayString(this._timer.getRemainingSeconds());
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

	// TimerWidgetList class

	var TimerWidgetList;

	TimerWidgetList = function()
	{
		this._list = [];
		this._interval = null;
		this._element = null;

		this._prepare();
	};

	TimerWidgetList.prototype._prepare = function()
	{
		// <ul>
		// </ul>

		var ul;

		// Root element

		ul = document.createElement("ul");

		$(ul).listview({ splitIcon: "gear" });

		this._element = $(ul);

		this._interval = setInterval(this.update.bind(this), 100);
	};

	TimerWidgetList.prototype.add = function(timer)
	{
		this._list.push(timer);

		this._element.append(timer.getElement());
		this._element.listview("refresh");
	};

	TimerWidgetList.prototype.each = function(f)
	{
		for (var i = 0; i < this._list.length; i++)
		{
			f(this._list[i]);
		}
	};

	TimerWidgetList.prototype.update = function()
	{
		this.each(function(timerWidget) {
			timerWidget.update();
		});
		this._element.listview("refresh");
	};

	TimerWidgetList.prototype.getElement = function()
	{
		return this._element;
	};

	timerwimer.TimerWidgetList = TimerWidgetList;

	// Exported functions

	timerwimer.main = function()
	{
		$(document).on("pageinit", "#main", function() {

			var timer;
			var widget;
			var widgetList;

			widgetList = new TimerWidgetList();

			timer = new Timer();
			timer.setLabel("Eleven seconds");
			timer.setTargetSeconds(11);

			widget = new TimerWidget(timer);
			widgetList.add(widget);

			timer = new Timer();
			timer.setLabel("Ten minutes");
			timer.setTargetSeconds(600);

			widget = new TimerWidget(timer);
			widgetList.add(widget);

			$("#timerlist").append(widgetList.getElement());
		});
	};
}());

timerwimer.main();
