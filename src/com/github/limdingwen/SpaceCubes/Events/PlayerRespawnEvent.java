package com.github.limdingwen.SpaceCubes.Events;

public class PlayerRespawnEvent extends EventHolder {
	public boolean setSpawn;
	
	public PlayerRespawnEvent(boolean ss) {
		setSpawn = ss;
	}
}
