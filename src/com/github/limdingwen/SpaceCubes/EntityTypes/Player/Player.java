package com.github.limdingwen.SpaceCubes.EntityTypes.Player;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.github.limdingwen.SpaceCubes.Debug;
import com.github.limdingwen.SpaceCubes.Main;
import com.github.limdingwen.SpaceCubes.Physic;
import com.github.limdingwen.SpaceCubes.PhysicCollisionFeedback;
import com.github.limdingwen.SpaceCubes.PhysicCollisionRunnable;
import com.github.limdingwen.SpaceCubes.BlockTypes.Block;
import com.github.limdingwen.SpaceCubes.BlockTypes.Material;
import com.github.limdingwen.SpaceCubes.DataTypes.Vector2i;
import com.github.limdingwen.SpaceCubes.DataTypes.Vector3i;
import com.github.limdingwen.SpaceCubes.EntityTypes.Entity;
import com.github.limdingwen.SpaceCubes.Events.EventEngine;
import com.github.limdingwen.SpaceCubes.Events.PlayerBreakBlock;
import com.github.limdingwen.SpaceCubes.Events.PlayerEventListener;
import com.github.limdingwen.SpaceCubes.Events.PlayerJumpEvent;
import com.github.limdingwen.SpaceCubes.Events.PlayerMoveEvent;
import com.github.limdingwen.SpaceCubes.Events.PlayerPlaceBlock;
import com.github.limdingwen.SpaceCubes.Events.PlayerRespawnEvent;
import com.github.limdingwen.SpaceCubes.Events.PlayerRotateEvent;
import com.github.limdingwen.SpaceCubes.File.ChunkLevelParser;
import com.github.limdingwen.SpaceCubes.Rendering.BlockRenderEngine;
import com.github.limdingwen.SpaceCubes.Rendering.RenderEngine;
import com.github.limdingwen.SpaceCubes.Rendering.Stage;
import com.github.limdingwen.SpaceCubes.World.Chunk;
import com.github.limdingwen.SpaceCubes.World.World;

public class Player extends Entity implements PlayerEventListener {
	// CONSTANTS
	
	public static float WALK_FORWARD_SPEED = 0.02f;
	public static float AIR_WALK_DIVIDER = 1;
	public static float SHIFT_DIVIDER = 3;
	public static float JUMP_VELOCITY = 0.17f;
	public static float ROTATE_SENSITIVITY = 0.25f;
	public static int START_HEALTH = 20;
	public static float SHIFT_DOWN = BlockRenderEngine.BLOCK_SIZE / 4;
	
	public static float ACCELERATION = 0.003f;
	public static float FRICTION = 0.005f;
	
	public float speed = 0;
	
	public static float FALL_DAMAGE_START = 0.001f;
	
	public static float VOIDPOS = -64 * BlockRenderEngine.BLOCK_SIZE;
	
	public boolean shifting = false;
	
	// Physics
	
	public Physic physic = new Physic(this);
	
	public boolean grounded = true;
	public int health = START_HEALTH;
	public float fell = 0;
	
	public Material inHand = Material.DIRT;
	
	private boolean isMoving = false;
	
	// Direction codes
	
	public byte direction = 0;
	
	public static final byte NORTH = 0;
	public static final byte NORTHEAST = 1;
	public static final byte EAST = 2;
	public static final byte SOUTHEAST = 3;
	public static final byte SOUTH = 4;
	public static final byte SOUTHWEST = 5;
	public static final byte WEST = 6;
	public static final byte NORTHWEST = 7;
	
	public Player(Vector3f[] v, Vector3f t, Vector3f r, int g, Stage stage) {
		super(v, t, r, g, stage); // Construct entity
		
		// INIT CODE
		
		physic.hasGravity = true;
		
		// Register events

		EventEngine.registerListener(this, new int[] {EventEngine.PLAYER});
	}

	public void playerUpdate() {
		// Update physics
		// Now updated through timers

		// Move camera

		Vector3f camTranslate = new Vector3f(translation.x, translation.y, translation.z);
		camTranslate.y += BlockRenderEngine.BLOCK_SIZE * 3.3f;
		if (shifting) camTranslate.y -= SHIFT_DOWN;
		RenderEngine.camera.position = camTranslate;

		// Rotate camera

		RenderEngine.camera.pitch = rotation.x;
		RenderEngine.camera.yaw = rotation.y;
		RenderEngine.camera.roll = rotation.z;

		// Void detection

		if (translation.y < VOIDPOS) {
			EventEngine.executeEvent(EventEngine.PLAYER, "playerRespawn", new PlayerRespawnEvent(true));
		}

		// Because events execute first:

		if (!isMoving) {			
			speed -= FRICTION;

			if (speed < 0) {
				speed = 0;
			}
		}
		else {
			isMoving = false;
		}
		
		// Update direction

		float dirRot = calculateRotation(rotation.y);
		
		if (dirRot >= 338 || dirRot < 23) direction = WEST;
		else if (dirRot >= 23 && dirRot < 68) direction = SOUTHWEST;
		else if (dirRot >= 68 && dirRot < 113) direction = SOUTH;
		else if (dirRot >= 113 && dirRot < 158) direction = SOUTHEAST;
		else if (dirRot >= 158 && dirRot < 203) direction = EAST;
		else if (dirRot >= 203 && dirRot < 248) direction = NORTHEAST;
		else if (dirRot >= 248 && dirRot < 293) direction = NORTH;
		else if (dirRot >= 293 && dirRot < 338) direction = NORTHWEST;
	}

	public float calculateRotation(float rot) {
		float rotRet = rot % 360;
		
		if (rot < 0) rotRet += 360;
				
		return Math.abs(rotRet);
	}
	
	@Override
	public void playerMove(PlayerMoveEvent event) {
		//		physic.lastPosition = translation;

		Vector2f moveRaw = event.moveDirection;
		float totalDivider = 1;

		if (!grounded) totalDivider *= AIR_WALK_DIVIDER;
		if (shifting) totalDivider *= SHIFT_DIVIDER;
		
		isMoving = true;
		speed += ACCELERATION;
		
		if (speed > WALK_FORWARD_SPEED / totalDivider) {
			speed = WALK_FORWARD_SPEED / totalDivider;
		}
		
		if (moveRaw.x == 1) strafeRight(speed * Main.dt);
		if (moveRaw.x == -1) strafeLeft(speed * Main.dt);
		if (moveRaw.y == 1) walkBackwards(speed * Main.dt);
		if (moveRaw.y == -1) walkForward(speed * Main.dt);
	}

	@Override
	public void playerRotate(PlayerRotateEvent event) {
		rotation.y += -event.moveDirection.x * ROTATE_SENSITIVITY;
		rotation.x += event.moveDirection.y * ROTATE_SENSITIVITY;
		
		// Check if rotation is too much
		
		if (rotation.x > 90) rotation.x = 90;
		if (rotation.x < -90) rotation.x = -90;		
	}

	@Override
	public void playerJump(PlayerJumpEvent event) {
		if (grounded) physic.velocity.y = JUMP_VELOCITY;
	}
	
	// Direction code decoder
	
	public static String decodeDirectionToString(byte dir) {
		switch (dir) {
		case NORTH: return "NORTH";
		case NORTHEAST: return "NORTHEAST";
		case EAST: return "EAST";
		case SOUTHEAST: return "SOUTHEAST";
		case SOUTH: return "SOUTH";
		case SOUTHWEST: return "SOUTHWEST";
		case WEST: return "WEST";
		case NORTHWEST: return "NORTHWEST";
		default: return null;
		}
	}
	
	// Player walk events
	
	//moves the camera forward relative to its current rotation (rotation.y)
	public void walkForward(float distance)
	{
		PhysicCollisionFeedback feedback;
		PhysicCollisionRunnable runnable = new PhysicCollisionRunnable(this, null, null, false, true);
		Vector3f tempPos = new Vector3f(translation.x, translation.y, translation.z);
				
	    translation.x -= distance * (float)Math.sin(Math.toRadians(rotation.y));
		runnable.run();
		feedback = runnable.feedback;
		
	    if (feedback.collided()) {
	    	translation.x = tempPos.x;
	    }
	    
	    translation.z -= distance * (float)Math.cos(Math.toRadians(rotation.y));
		runnable.run();
		feedback = runnable.feedback;
		
		
	    if (feedback.collided()) {
	    	translation.z = tempPos.z;
	    }
	}
	 
	//moves the camera backward relative to its current rotation (rotation.y)
	public void walkBackwards(float distance)
	{
		PhysicCollisionFeedback feedback;
		PhysicCollisionRunnable runnable = new PhysicCollisionRunnable(this, null, null, false, true);
		Vector3f tempPos = new Vector3f(translation.x, translation.y, translation.z);
		
	    translation.x += distance * (float)Math.sin(Math.toRadians(rotation.y));
		runnable.run();
		feedback = runnable.feedback;
		
	    if (feedback.collided()) {
	    	translation.x = tempPos.x;
	    }
	    
	    translation.z += distance * (float)Math.cos(Math.toRadians(rotation.y));
		runnable.run();
		feedback = runnable.feedback;
	    
	    if (feedback.collided()) {
	    	translation.z = tempPos.z;
	    }
	}
	 
	//strafes the camera left relitive to its current rotation (rotation.y)
	public void strafeLeft(float distance)
	{
		PhysicCollisionFeedback feedback;
		PhysicCollisionRunnable runnable = new PhysicCollisionRunnable(this, null, null, false, true);
		Vector3f tempPos = new Vector3f(translation.x, translation.y, translation.z);
		
	    translation.x -= distance * (float)Math.sin(Math.toRadians(rotation.y+90));
		runnable.run();
		feedback = runnable.feedback;
	    
	    if (feedback.collided()) {
	    	translation.x = tempPos.x;
	    }
	    
	    translation.z -= distance * (float)Math.cos(Math.toRadians(rotation.y+90));
		runnable.run();
		feedback = runnable.feedback;
	    
	    if (feedback.collided()) {
	    	translation.z = tempPos.z;
	    }
	}
	 
	//strafes the camera right relitive to its current rotation (rotation.y)
	public void strafeRight(float distance)
	{
		PhysicCollisionFeedback feedback;
		PhysicCollisionRunnable runnable = new PhysicCollisionRunnable(this, null, null, false, true);
		Vector3f tempPos = new Vector3f(translation.x, translation.y, translation.z);
		
	    translation.x -= distance * (float)Math.sin(Math.toRadians(rotation.y-90));
		runnable.run();
		feedback = runnable.feedback;
	    
	    if (feedback.collided()) {
	    	translation.x = tempPos.x;
	    }
	    
	    translation.z -= distance * (float)Math.cos(Math.toRadians(rotation.y-90));
		runnable.run();
		feedback = runnable.feedback;
	    
	    if (feedback.collided()) {
	    	translation.z = tempPos.z;
	    }
	}

	@Override
	public void playerBreakBlock(PlayerBreakBlock event) {
		RenderEngine.world.changeBlockAtToRealCoord(event.blockLoc, Material.AIR, true);
	}

	@Override
	public void playerPlaceBlock(PlayerPlaceBlock event) {
		RenderEngine.world.changeBlockAtToRealCoord(event.blockLoc, event.material, false);
	}
	
	public void respawn(boolean setSpawn) {
		Vector3i spawnBlockVec = null;
		
		if (setSpawn) {
			Chunk ch = null;
			Vector2i chPos = new Vector2i(0, 0);
			
			try {
				ch = RenderEngine.world.getChunkAtBlockCoords(World.coordRealToBlock(Main.spawn));
				chPos = World.getChunkPosAtBlockCoords(World.coordRealToBlock(Main.spawn));
			}
			catch (ArrayIndexOutOfBoundsException e) {
				ch = RenderEngine.world.chunks[0][0];
				
				Debug.error("Invalid spawn location: Resetting to 0,0,0");
				Main.spawn = new Vector3f();
			}
			
			if (!ch.getIsLoaded()) {
				try {
					ch.blocks = ChunkLevelParser.parseChunk("TestWorld", chPos.x, chPos.y).blocks;
				} catch (IOException ex) {
					Debug.warning("Unable to parse chunk " + chPos.x + ", " + chPos.y);
					Debug.warning("Generating chunk!");

					Logger.getLogger(ChunkLevelParser.class.getName()).log(Level.INFO, null, ex);

					// Generate new chunk

					ch.generate();
				}
				
				RenderEngine.world.chunks[chPos.x][chPos.y] = ch;
			}
			
			Block spawnBlock = null;
			
			try {
				spawnBlock = RenderEngine.world.getTopBlock(World.coordRealToBlock(Main.spawn).x,
					World.coordRealToBlock(Main.spawn).z);
			}
			catch (ArrayIndexOutOfBoundsException e) {
				Main.spawn = new Vector3f(0,0,0);
				
				spawnBlock = RenderEngine.world.getTopBlock(World.coordRealToBlock(Main.spawn).x,
						World.coordRealToBlock(Main.spawn).z);
			}
			
			if (spawnBlock == null) {
				spawnBlockVec = new Vector3i(0, 64, 0);
			}
			else {
				spawnBlockVec = spawnBlock.translation;
			}
		
		
		Main.spawn = new Vector3f(
				spawnBlockVec.x * BlockRenderEngine.doubleBs,
				(spawnBlockVec.y + 1) * BlockRenderEngine.doubleBs,
				spawnBlockVec.z * BlockRenderEngine.doubleBs);
		}
		
		translation = new Vector3f(
				Main.spawn.x,
				Main.spawn.y,
				Main.spawn.z);
		
		physic.velocity = new Vector3f();
	}
	
	public void playerRespawn(PlayerRespawnEvent event) {
		respawn(event.setSpawn);
	}
	
	public boolean getMoving() {
		return isMoving;
	}
}
