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

	// Timer class

	var Timer;

	Timer = function()
	{
		this._id = 0;
		this._label = "";
		this._state = Timer.State.STOPPED;

		this._targetSeconds = 0;
		this._elapsedSeconds = 0;
		this._remainingSeconds = 0;
	};

	Timer.State =
	{
		STOPPED:  "STOPPED",
		RUNNING:  "RUNNING",
		PAUSED:   "PAUSED",
		FINISHED: "FINISHED"
	};

	Timer.now = function()
	{
		return new Date().getTime();
	};

	Timer.prototype.start = function()
	{
		if (this.getState() == Timer.State.STOPPED ||
		    this.getState() == Timer.State.PAUSED)
		{
			this._state = Timer.State.RUNNING;

			this._lastUpdateTime = Timer.now();

			this._interval = setInterval(this._update.bind(this), 100);
		}
	};

	Timer.prototype.pause = function()
	{
		if (this.getState() == Timer.State.RUNNING)
		{
			this._state = Timer.State.PAUSED;

			clearInterval(this._interval);
			this._interval = null;
		}
	};

	Timer.prototype._finish = function()
	{
		this._state = Timer.State.FINISHED;

		clearInterval(this._interval);
		this._interval = null;
	};

	Timer.prototype.stop = function()
	{
		this._state = Timer.State.STOPPED;

		this._elapsedSeconds = 0;
		this._remainingSeconds = this.getTargetSeconds();

		clearInterval(this._interval);
		this._interval = null;
	};

	Timer.prototype._update = function()
	{
		// If the timer has been last updated a second ago, update it again
		if (Math.floor((Timer.now() - this._lastUpdateTime) / 1000) > 0)
		{
			this._elapsedSeconds += 1;
			this._remainingSeconds = this.getTargetSeconds() - this.getElapsedSeconds();

			// Increase by a full second instead of using the current
			// time to compensate for JavaScript timer drift
			this._lastUpdateTime += 1000;
		}

		if (this.getElapsedSeconds() >= this.getTargetSeconds())
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
		this._targetSeconds = targetSeconds;

		// Changing the target time causes the timer to stop
		this.stop();
	};

	Timer.prototype.getTargetSeconds = function()
	{
		return this._targetSeconds;
	};

	Timer.prototype.getState = function()
	{
		return this._state;
	};

	Timer.prototype.getElapsedSeconds = function()
	{
		return this._elapsedSeconds;
	};

	Timer.prototype.getRemainingSeconds = function()
	{
		return this._remainingSeconds;
	};

	timerrific.Timer = Timer;

	// Private functions

	setupInterface = function(timers)
	{
		var box;
		var text;
		var span;
		var br;

		for (var i = 0; i < timers.length; i++)
		{
			var timer;
			var id;

			timer = timers[i];
			id = timer.getId();

			box = document.createElement("div");
			box.setAttribute("id", "timer:" + id);
			document.getElementById("container")
				.appendChild(box);

			text = document.createTextNode("Id: ");
			span = document.createElement("span");
			span.id = "id:" + id;
			br = document.createElement("br");
			box.appendChild(text);
			box.appendChild(span);
			box.appendChild(br);

			text = document.createTextNode("Label: ");
			span = document.createElement("span");
			span.id = "label:" + id;
			br = document.createElement("br");
			box.appendChild(text);
			box.appendChild(span);
			box.appendChild(br);

			text = document.createTextNode("State: ");
			span = document.createElement("span");
			span.id = "state:" + id;
			br = document.createElement("br");
			box.appendChild(text);
			box.appendChild(span);
			box.appendChild(br);

			text = document.createTextNode("Target seconds: ");
			span = document.createElement("span");
			span.id = "targetSeconds:" + id;
			br = document.createElement("br");
			box.appendChild(text);
			box.appendChild(span);
			box.appendChild(br);

			text = document.createTextNode("Elapsed seconds: ");
			span = document.createElement("span");
			span.id = "elapsedSeconds:" + id;
			br = document.createElement("br");
			box.appendChild(text);
			box.appendChild(span);
			box.appendChild(br);

			text = document.createTextNode("Remaining seconds: ");
			span = document.createElement("span");
			span.id = "remainingSeconds:" + id;
			br = document.createElement("br");
			box.appendChild(text);
			box.appendChild(span);
			box.appendChild(br);

			span = document.createElement("span");
			span.id = "start:" + id;
			span.innerHTML = "[start]";
			box.appendChild(span);

			span = document.createElement("span");
			span.id = "pause:" + id;
			span.innerHTML = "[pause]";
			box.appendChild(span);

			span = document.createElement("span");
			span.id = "stop:" + id;
			span.innerHTML = "[stop]";
			box.appendChild(span);
		}
	};

	setupUpdates = function(timers)
	{
		setInterval(function()
		{
			for (var i = 0; i < timers.length; i++)
			{
				var timer;
				var id;

				timer = timers[i];
				id = timer.getId();

				document.getElementById("id:" + id).innerHTML = timer.getId();
				document.getElementById("label:" + id).innerHTML = timer.getLabel();
				document.getElementById("state:" + id).innerHTML = timer.getState();
				document.getElementById("targetSeconds:" + id).innerHTML = timer.getTargetSeconds();
				document.getElementById("elapsedSeconds:" + id).innerHTML = timer.getElapsedSeconds();
				document.getElementById("remainingSeconds:" + id).innerHTML = timer.getRemainingSeconds();
			}
		}, 100);
	};

	setupListeners = function(timers)
	{
		var timer;

		for (var i = 0; i < timers.length; i++)
		{
			timer = timers[i];

			document.getElementById("start:" + timer.getId())
				.addEventListener("click",
						  timer.start.bind(timer));
			document.getElementById("pause:" + timer.getId())
				.addEventListener("click",
						  timer.pause.bind(timer));
			document.getElementById("stop:" + timer.getId())
				.addEventListener("click",
						  timer.stop.bind(timer));
		}
	};

	// Exported functions

	timerrific.main = function()
	{
		var timers;
		var timer;

		timers = [];

		timer = new Timer();
		timer.setId(10);
		timer.setLabel("10 seconds");
		timer.setTargetSeconds(10);
		timers.push(timer);

		timer = new Timer();
		timer.setId(30);
		timer.setLabel("30 seconds");
		timer.setTargetSeconds(30);
		timers.push(timer);

		setupInterface(timers);
		setupUpdates(timers);
		setupListeners(timers);
	};

}());

document.addEventListener("DOMContentLoaded", timerrific.main);
