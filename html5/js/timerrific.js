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

var Timer = function() {

	this._state = Timer.State.STOPPED;
};

Timer.State = {
	STOPPED:  "STOPPED",
	RUNNING:  "RUNNING",
	PAUSED:   "PAUSED",
	FINISHED: "FINISHED"
};

Timer.now = function() {

	return new Date().getTime();
};

Timer.prototype.start = function() {

	this._state = Timer.State.RUNNING;

	this._startTime = Timer.now();

	this._update();
	this._interval = setInterval(this._update.bind(this), 100);
};

Timer.prototype._finish = function() {

	this._state = Timer.State.FINISHED;

	clearInterval(this._interval);
	this._interval = null;
};

Timer.prototype.stop = function() {

	this._state = Timer.State.STOPPED;

	this._elapsedSeconds = 0;
	this._remainingSeconds = this.getTargetSeconds();

	clearInterval(this._interval);
	this._interval = null;
};

Timer.prototype._update = function() {

	this._elapsedSeconds = Math.floor((Timer.now() - this._startTime) / 1000);
	this._remainingSeconds = this.getTargetSeconds() - this.getElapsedSeconds();

	if (this.getElapsedSeconds() >= this.getTargetSeconds()) {

		this._finish();
	}

	document.getElementById("label").innerHTML = this.getLabel();
	document.getElementById("state").innerHTML = this.getState();
	document.getElementById("targetSeconds").innerHTML = this.getTargetSeconds();
	document.getElementById("elapsedSeconds").innerHTML = this.getElapsedSeconds();
	document.getElementById("remainingSeconds").innerHTML = this.getRemainingSeconds();
};

Timer.prototype.setLabel = function(label) {

	this._label = label;
};

Timer.prototype.getLabel = function() {

	return this._label;
};

Timer.prototype.setTargetSeconds = function(targetSeconds) {

	this._targetSeconds = targetSeconds;
};

Timer.prototype.getTargetSeconds = function() {

	return this._targetSeconds;
};

Timer.prototype.getState = function() {

	return this._state;
};

Timer.prototype.getElapsedSeconds = function() {

	return this._elapsedSeconds;
};

Timer.prototype.getRemainingSeconds = function() {

	return this._remainingSeconds;
};

function main() {

	var timer;

	timer = new Timer();
	timer.setLabel("Test");
	timer.setTargetSeconds(10);

	document.getElementById("start")
	        .addEventListener("click",
		                  timer.start.bind(timer));
	document.getElementById("stop")
	        .addEventListener("click",
		                  timer.stop.bind(timer));
}

document.addEventListener("DOMContentLoaded", main);
