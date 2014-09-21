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

var Timer = function() {};

Timer.now = function() {

	return new Date().getTime();
};

Timer.prototype.start = function() {

	this._startTime = Timer.now();
	this._update();
	this._interval = setInterval(this._update.bind(this), 100);
};

Timer.prototype.stop = function() {

	clearInterval(this._interval);
};

Timer.prototype._update = function() {

	this._elapsedSeconds = Math.floor((Timer.now() - this._startTime) / 1000);
	this._remainingSeconds = this._targetSeconds - this._elapsedSeconds;

	document.getElementById("label").innerHTML = this.getLabel();
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
	timer.setTargetSeconds(60);

	document.getElementById("action")
	        .addEventListener("click",
		                  timer.start.bind(timer));
}

document.addEventListener("DOMContentLoaded", main);
