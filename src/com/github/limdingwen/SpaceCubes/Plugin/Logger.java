package com.github.limdingwen.SpaceCubes.Plugin;

import com.github.limdingwen.SpaceCubes.Debug;

public class Logger {
	String name = "";
	
	public Logger(String n) {
		name = n;
	}
	
	public void info(String message) {
		Debug.info(name + ": " + message);
	}

	public void warning(String message) {
		Debug.warning(name + ": " + message);
	}

	public void error(String message) {
		Debug.error(name + ": " + message);
	}
}
