﻿class RgbModule extends Module {
	var maxRate:Number = 6;
	var running:Boolean;
	var redRate:Number;
	var greenRate:Number;
	var blueRate:Number;
	var redBeam:PhotonGenerator;
	var greenBeam:PhotonGenerator;
	var blueBeam:PhotonGenerator;
	function RgbModule() {
//		redRate = 4;
//		blueRate = 4;
//		greenRate = 4;
		var baseLayer:Number = 30000;
		redBeam = _root.attachMovie("PhotonGeneratorID", "rgb-red", baseLayer++);
		redBeam.setLocation(_root.redBulb._x, _root.redBulb._y);
		redBeam.setTheta(27);
		redBeam.setColor(0xFF0000);
		greenBeam = _root.attachMovie("PhotonGeneratorID", "rgb-green", baseLayer++);
		greenBeam.setLocation(_root.greenBulb._x, _root.greenBulb._y);
		greenBeam.setTheta(0);
		greenBeam.setColor(0x00FF00);
		blueBeam = _root.attachMovie("PhotonGeneratorID", "rgb-blue", baseLayer++);
		blueBeam.setLocation(_root.blueBulb._x, _root.blueBulb._y);
		blueBeam.setTheta(-27);
		blueBeam.setColor(0x0000FF);
	}
	function stop() {
		running = false;
		redBeam.stop();
		greenBeam.stop();
		blueBeam.stop();
	}
	function start() {
		running = true;
		redBeam.start();
		greenBeam.start();
		blueBeam.start();
	}
	function activate() {
		super.activate();
		this.start();
	}
	function deactivate() {
		super.deactivate();
		this.stop();
	}
	function getMaxRate():Number {
		return this.maxRate;
	}
	function setRedRate(rate:Number) {
		redBeam.setRate(rate);
		redRate = rate;
	}
	function setGreenRate(rate:Number) {
		greenBeam.setRate(rate);
		greenRate = rate;
	}
	function setBlueRate(rate:Number) {
		blueBeam.setRate(rate);
		blueRate = rate;
	}
	function getFilteredCtx() {
		var red = Math.round((redBeam.getRateAtEyeball() / this.getMaxRate()) * 255);
		var green = Math.round((greenBeam.getRateAtEyeball() / this.getMaxRate()) * 255);
		var blue = Math.round((blueBeam.getRateAtEyeball() / this.getMaxRate()) * 255);
		var alpha = (Math.max(red, Math.max(green, blue))) * 100 / 255;
		
		var max = Math.max( red, Math.max( green, blue));
		if (max == red ) {
			green = (green / red ) * 255;
			blue = (blue / red ) * 255;
			red = 255;
		}
		if (max == green ) {
			red = (red / green ) * 255;
			blue = (blue / green ) * 255;
			green = 255;
		}
		if (max == blue ) {
			red = (red / blue) * 255;
			green = (green / blue ) * 255;
			blue = 255;
		}
		
		var ctx = {rb:red, gb:green, bb:blue, aa:alpha};
		return ctx;
	}
	function genTheta(theta0) {
		var d_theta = Math.random() * Math.PI / 16 - Math.PI / 32;
		var angle = theta0 + d_theta;
		return angle;
	}
}
