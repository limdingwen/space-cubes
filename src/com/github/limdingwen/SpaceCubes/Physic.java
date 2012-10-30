package com.github.limdingwen.SpaceCubes;

import java.util.TimerTask;

import com.github.limdingwen.SpaceCubes.EntityTypes.Player.Player;
import com.github.limdingwen.SpaceCubes.Rendering.Actor;
import org.lwjgl.util.vector.Vector3f;

public class Physic extends TimerTask {	
	public static final float GRAVITY = 0.01f;
	public static final float TERM_VELOCITY = 0.5f;

	public Vector3f velocity = new Vector3f();
	public Vector3f acceleration = new Vector3f();
	public Vector3f lastPosition = new Vector3f();

	public boolean hasGravity = true;
	
	// Creator (to update physics on this particular object)

	public Actor object;

	public Physic(Actor obj) {
		object = obj;
	}

	@Override
	public void run() {
		// Update gravity

		if (hasGravity) acceleration.y = -GRAVITY;

		// Update velocity

		velocity.x += acceleration.x;
		velocity.y += acceleration.y;
		velocity.z += acceleration.z;

		// Check if terminal velocity is reached

		if (velocity.y < -TERM_VELOCITY) {
			velocity.y = -TERM_VELOCITY;
		}

		// Apply changes to object

		object.translation.x += velocity.x;
		object.translation.y += velocity.y;
		object.translation.z += velocity.z;
		
		// Update collision
		
		if (object instanceof Player) {
			PhysicCollisionRunnable col = new PhysicCollisionRunnable((Player) object, velocity, acceleration, true, false);
			
			col.run();

			((Player) object).grounded = col.feedback.isGrounded;
			
			if (col.feedback.isGrounded) {
				if (velocity.y < 0) {
					velocity.y = 0f;
					acceleration.y = 0f;
				}
			}
		}
	}
}
