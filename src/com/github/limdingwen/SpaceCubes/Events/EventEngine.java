package com.github.limdingwen.SpaceCubes.Events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import com.github.limdingwen.SpaceCubes.Debug;

public class EventEngine {
	public static final int PLAYER = 0;
	
	private static ArrayList<ListenerEntry> listeners = new ArrayList<ListenerEntry>();
	
	public static void registerListener(Object listener, int[] types) {
		listeners.add(new ListenerEntry(listener, types));
	}
	
	public static void executeEvent(int type, String function, EventHolder eh) {
		for (int i = listeners.size() - 1; i >= 0; i--) {
			ListenerEntry le = listeners.get(i);
			
			for (int t = 0; t < le.types.length; t++) {
				if (le.types[t] == type) {
					try {
						if (!eh.cancelled) {
							Method m = le.listener.getClass().getDeclaredMethod(function, new Class[] {eh.getClass()});
							m.invoke(le.listener, eh);
						}
					} catch (SecurityException e) {
						Debug.warning("Security risk while executing event!");
					} catch (NoSuchMethodException e) {
						Debug.error("No such event!");
					} catch (IllegalArgumentException e) {
						Debug.error("Wrong event holder!");
					} catch (IllegalAccessException e) {
						Debug.error("Cannot access event!");
					} catch (InvocationTargetException e) {
						Debug.error("Unknown Java Reflection error while processing event method call!");
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public static void fireListener(Object listener) {
		for (int i = 0; i < listeners.size(); i++) {
			ListenerEntry l = listeners.get(i);
			
			if (l.listener == listener) {
				listeners.remove(i);
			}
		}
	}
	
	public static void changeTypesTo(Object listener, int[] t) {
		for (int i = 0; i < listeners.size(); i++) {
			ListenerEntry l = listeners.get(i);
			
			if (l.listener == listener) {
				l.types = t;
			}
		}
	}
}
