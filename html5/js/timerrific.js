/* Timerrific - Countdown timers for your device
 * Copyright (C) 2013-2014  Andrea Bolognani <eof@kiyuko.org>
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

var timerrific = {};

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

	timerrific.Util = Util;

	// Timer class

	var Timer;

	Timer = function()
	{
		this._id = 0;
		this._label = "";
		this._targetTime = 0;

		this._state = Timer.State.STOPPED;
		this._elapsedTime = 0;
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
		this._elapsedTime = 0;
		this._lastUpdateTime = 0;
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

		this._elapsedTime += now - this._lastUpdateTime;
		this._lastUpdateTime = now;

		if (this._elapsedTime >= this._targetTime)
		{
			this._finish();
		}
	};

	Timer.prototype.setId = function(id)
	{
		this._id = id;
	};

	Timer.prototype.getId = function()
	{
		return this._id;
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
		return Math.ceil((this._targetTime - this._elapsedTime) / 1000);
	};

	timerrific.Timer = Timer;

	// TimerWidget class

	var TimerWidget;

	TimerWidget = function()
	{
		this._timer = new Timer();

		this._element = null;
		this._label = null;
		this._id = null;
		this._remaining = null;
		this._state = null;
		this._interval = null;
	};

	TimerWidget.prototype.show = function()
	{
		if (!this._element)
		{
			this._prepare();
		}

		// Update immediately to prevent flashing
		this._update();

		if (!this._interval)
		{
			this._interval = setInterval(this._update.bind(this), 250);
		}

		this._element.style.display = "";
	};

	TimerWidget.prototype.hide = function()
	{
		this._element.style.display = "none";

		clearInterval(this._interval);
	};

	TimerWidget.prototype._prepare = function()
	{
		var id;
		var el;
		var span;
		var text;
		var br;

		id = this._timer.getId();

		el = document.createElement("div");
		el.id = "timer:" + id;

		// First line: label (id)

		span = document.createElement("span");
		span.id = "label:" + id;
		el.appendChild(span);

		this._label = span;

		text = document.createTextNode(" (");
		el.appendChild(text);

		span = document.createElement("span");
		span.id = "id:" + id;
		el.appendChild(span);

		this._id = span;

		text = document.createTextNode(")");
		el.appendChild(text);

		br = document.createElement("br")
		el.appendChild(br);

		// Second line: remaining (state)

		span = document.createElement("span");
		span.id = "remaining:" + id;
		el.appendChild(span);

		this._remaining = span;

		text = document.createTextNode(" (");
		el.appendChild(text);

		span = document.createElement("span");
		span.id = "state:" + id;
		el.appendChild(span);

		this._state = span;

		text = document.createTextNode(")");
		el.appendChild(text);

		br = document.createElement("br")
		el.appendChild(br);

		// Third line: start pause stop

		span = document.createElement("span");
		span.id = "start:" + id;
		span.innerHTML = "[start]";
		el.appendChild(span);

		span.addEventListener("click", this._timer.start.bind(this._timer));

		text = document.createTextNode(" ");
		el.appendChild(text);

		span = document.createElement("span");
		span.id = "pause:" + id;
		span.innerHTML = "[pause]";
		el.appendChild(span);

		span.addEventListener("click", this._timer.pause.bind(this._timer));

		text = document.createTextNode(" ");
		el.appendChild(text);

		span = document.createElement("span");
		span.id = "stop:" + id;
		span.innerHTML = "[stop]";
		el.appendChild(span);

		span.addEventListener("click", this._timer.stop.bind(this._timer));

		this._element = el;
	};

	TimerWidget.prototype._update = function()
	{
		this._label.innerHTML = this._timer.getLabel();
		this._id.innerHTML = this._timer.getId();
		this._remaining.innerHTML = Util.secondsToDisplayString(this._timer.getRemainingSeconds());
		this._state.innerHTML = this._timer.getState();
	};

	TimerWidget.prototype.setTimer = function(timer)
	{
		if (this._timer)
		{
			clearInterval(this._interval);
			this._timer.stop();
			this._element = null;
		}

		this._timer = timer;
	};

	TimerWidget.prototype.getTimer = function()
	{
		return this._timer;
	};

	TimerWidget.prototype.getElement = function()
	{
		if (!this._element)
		{
			this._prepare();
		}

		return this._element;
	};

	timerrific.TimerWidget = TimerWidget;

	// Exported functions

	timerrific.main = function()
	{
		var timer;
		var widget;
		var el;

		timer = new Timer();
		timer.setId(10);
		timer.setLabel("10 seconds");
		timer.setTargetSeconds(10);

		widget = new TimerWidget();
		widget.setTimer(timer);
		el = widget.getElement();
		document.getElementById("container")
		        .appendChild(el);
		widget.show();

		timer = new Timer();
		timer.setId(30);
		timer.setLabel("30 seconds");
		timer.setTargetSeconds(30);

		widget = new TimerWidget();
		widget.setTimer(timer);
		el = widget.getElement();
		document.getElementById("container")
		        .appendChild(el);
		widget.show();
	};

}());

document.addEventListener("DOMContentLoaded", timerrific.main);
