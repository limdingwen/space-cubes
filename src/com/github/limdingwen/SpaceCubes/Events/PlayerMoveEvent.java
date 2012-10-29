package com.github.limdingwen.SpaceCubes.Events;

import org.lwjgl.util.vector.Vector2f;

public class PlayerMoveEvent extends EventHolder {
	public PlayerMoveEvent(float x, float z) {
		moveDirection.x = x;
		moveDirection.y = z;
	}

	public Vector2f moveDirection = new Vector2f(0,0);
}