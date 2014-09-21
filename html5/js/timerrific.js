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

	this.startTime = Timer.now();
	this.interval = setInterval(this.update.bind(this), 100);
};

Timer.prototype.stop = function() {

	clearInterval(this.interval);
};

Timer.prototype.update = function() {

	document.getElementById("label").innerHTML = this.getLabel();
	document.getElementById("elapsed").innerHTML = this.getElapsedSeconds();
};

Timer.prototype.setLabel = function(label) {

	this.label = label;
};

Timer.prototype.getLabel = function() {

	return this.label;
};

Timer.prototype.setTargetSeconds = function(targetSeconds) {

	this.targetSeconds = targetSeconds;
};

Timer.prototype.getTargetSeconds = function() {

	return this.targetSeconds;
};

Timer.prototype.getElapsedSeconds = function() {

	return Math.floor((Timer.now() - this.startTime) / 1000);
};

function main() {

	var timer;

	timer = new Timer();
	timer.setLabel("Test");
	timer.setTargetSeconds(60);
	timer.start();
}

document.addEventListener("DOMContentLoaded", main);
