package com.github.limdingwen.SpaceCubes.Events;

import org.lwjgl.util.vector.Vector2f;

public class PlayerRotateEvent extends EventHolder {
	public PlayerRotateEvent(Vector2f md) {
		moveDirection = md;
	}
	
	public Vector2f moveDirection;
}
