package com.github.limdingwen.SpaceCubes;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Debug {	
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
