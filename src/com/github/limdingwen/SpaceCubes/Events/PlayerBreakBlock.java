package com.github.limdingwen.SpaceCubes.Events;

import org.lwjgl.util.vector.Vector3f;

public class PlayerBreakBlock extends EventHolder {
	public Vector3f blockLoc;
	public PlayerBreakBlock(Vector3f blockLoc) {
		super();
		this.blockLoc = blockLoc;
	}
}
