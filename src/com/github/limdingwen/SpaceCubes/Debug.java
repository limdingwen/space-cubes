package com.github.limdingwen.SpaceCubes;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Debug {
	public final int LVL_INFO = 2;
	public final int LVL_WARN = 1;
	public final int LVL_ERROR = 0;

	public final int LEVEL_OF_LOG = 1;

	// -1: Turn off debugging
	// 0: Error
	// 1: Warnings and errors
	// 2: Info, warnings and errors
	
	static SimpleDateFormat sdf = new SimpleDateFormat("h:mm:ss a");
	
	public static void init() {
	}
	
	public static String getTime() {
		return sdf.format(new Timestamp(new Date().getTime()));
	}
	
	public static void info(String message) {
		consoleOutput("INFO", message);
	}
	
	public static void warning(String message) {
		consoleOutput("WARNING", message);
	}
	public static void error(String message) {
		consoleOutput("SEVERE", message);
	}
	
	public static void consoleOutput(String prefix, String message) {
		System.out.println(getTime() + ":" + prefix + " : " + message);
	}
}
