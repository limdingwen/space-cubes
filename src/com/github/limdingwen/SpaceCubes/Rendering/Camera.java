package com.github.limdingwen.SpaceCubes.Rendering;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class Camera {
	public Vector3f position;
	public float yaw = 0;
	public float pitch = 0;
	public float roll = 0;
	
	public Camera(Vector3f pos) {
		position = pos;
	}
	
	// Editing commands
	
	public void yaw(float amount) {
		yaw += amount;
	}
	
	public void pitch(float amount) {
		pitch += amount;
	}
	
	public void roll(float amount) {
		roll += amount;
	}
	
	// Always run this before drawing an object based on the camera.
	// AFTER resetting matrix.
	public void lookThrough() {		
		GL11.glRotatef(-pitch, 1, 0, 0);
		GL11.glRotatef(-yaw, 0, 1, 0);
		GL11.glRotatef(-roll, 0, 0, 1);
		
		GL11.glTranslatef(-position.x, -position.y, -position.z);
	}
}
