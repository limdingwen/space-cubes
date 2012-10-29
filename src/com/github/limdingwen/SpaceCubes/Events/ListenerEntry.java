package com.github.limdingwen.SpaceCubes.Events;

public class ListenerEntry {
	public Object listener;
	public int[] types;
	
	public ListenerEntry(Object l, int[] t) {
		listener = l;
		types = t;
	}
}
