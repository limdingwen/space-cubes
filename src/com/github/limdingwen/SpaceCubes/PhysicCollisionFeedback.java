package com.github.limdingwen.SpaceCubes;

public class PhysicCollisionFeedback {
	public boolean isGrounded = false;
	public boolean collidedAtWaist = false;
	public boolean collidedAtHead = false;
	public boolean collidedAtFeet = false;
	
	public boolean collided() {
		return (collidedAtWaist || collidedAtHead || collidedAtFeet);
	}
}
