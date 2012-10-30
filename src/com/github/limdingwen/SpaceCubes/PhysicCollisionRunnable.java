package com.github.limdingwen.SpaceCubes;

import org.lwjgl.util.vector.Vector3f;

import com.github.limdingwen.SpaceCubes.BlockTypes.Block;
import com.github.limdingwen.SpaceCubes.BlockTypes.Material;
import com.github.limdingwen.SpaceCubes.Collision.BoxCollider;
import com.github.limdingwen.SpaceCubes.Collision.Collision;
import com.github.limdingwen.SpaceCubes.DataTypes.Vector2i;
import com.github.limdingwen.SpaceCubes.EntityTypes.Player.Player;
import com.github.limdingwen.SpaceCubes.Rendering.BlockRenderEngine;
import com.github.limdingwen.SpaceCubes.Rendering.PrimitivesEngine;
import com.github.limdingwen.SpaceCubes.Rendering.RenderEngine;
import com.github.limdingwen.SpaceCubes.World.Chunk;
import com.github.limdingwen.SpaceCubes.World.ChunkLeaker;
import com.github.limdingwen.SpaceCubes.World.LeakedChunk;
import com.github.limdingwen.SpaceCubes.World.World;

public class PhysicCollisionRunnable {
	public PhysicCollisionFeedback feedback = new PhysicCollisionFeedback();
	public Player object = null;
	public boolean checkGrounded = false;
	public boolean checkWalls = false;
	public Vector3f velocity = null;
	public Vector3f acceleration = null;

	public PhysicCollisionRunnable(Player o, Vector3f velocity, Vector3f acc, boolean checkGrounded, boolean checkWalls) {
		object = o;
		this.checkGrounded = checkGrounded;
		this.checkWalls = checkWalls;
		this.velocity = velocity;
		acceleration = acc;
	}

	public void run() {
		LeakedChunk[] leakedChunks = ChunkLeaker.leakChunksByCube(
				World.toFirstChunk(World.coordRealToBlock(object.translation)), 3);
		
		for (int i = 0; i < leakedChunks.length; i++) {
			LeakedChunk leakedChunk = leakedChunks[i];

			Vector2i cp = World.getChunkPosAtBlockCoords(
					World.coordRealToBlock(object.translation));
			Chunk chunk = null;
			
			try {
				chunk = RenderEngine.world.chunks[cp.x + leakedChunk.chunkRelativePos.x][cp.y + leakedChunk.chunkRelativePos.y];
			}
			catch (ArrayIndexOutOfBoundsException e) {
				continue;
			}
			
			if (chunk == null) continue;
			
			if (chunk.getIsLoaded()) {				
				for (int b = 0; b < leakedChunk.blocks.size(); b++) {
					int bx = leakedChunk.blocks.get(b).x;
					int by = leakedChunk.blocks.get(b).y;
					int bz = leakedChunk.blocks.get(b).z;
					
					if (by >= World.CHUNK_HEIGHT || by < 0) continue;

					if (Material.getMaterialFromID(chunk.blocks[bx][by][bz].material, chunk.blocks[bx][by][bz].meta).collidable) {
						Block block = chunk.blocks[bx][by][bz];

						if (!block.comCulled) {
							if (Material.getMaterialFromID(block.material, block.meta).fullCollider) {
								if (Collision.isBoxColliding((BoxCollider) block.col, new Vector3f(object.translation.x,
										object.translation.y,
										object.translation.z)) && checkGrounded) {
									feedback.isGrounded = true;

									// To collider contact

									if (velocity.y <= 0) {
										boolean doIt = false;

										if (!(by+1 >= World.CHUNK_HEIGHT)) {
											Block topBlock = chunk.blocks[bx][by + 1][bz];

											if (Material.getMaterialFromID(topBlock.material, topBlock.meta).notSolid) {
												doIt = true;
											}
										}
										else {
											doIt = true;
										}

										if (doIt) {
											float y = Math.max(block.col.point1.y, block.col.point2.y);

											object.translation.y = y - BlockRenderEngine.BLOCK_SIZE/64;	
										}
									}
								}
							}

							// Anti-wall enter

							if (Collision.isBoxColliding((BoxCollider) block.col, new Vector3f(object.translation.x,
									object.translation.y + BlockRenderEngine.BLOCK_SIZE/63,
									object.translation.z)) && checkWalls) {

								feedback.collidedAtFeet = true;
							}

							if (Collision.isBoxColliding((BoxCollider) block.col, new Vector3f(object.translation.x,
									object.translation.y + BlockRenderEngine.BLOCK_SIZE*2,
									object.translation.z)) && checkWalls) {

								feedback.collidedAtWaist = true;
							}

							if (Collision.isBoxColliding((BoxCollider) block.col, new Vector3f(object.translation.x,
									object.translation.y + BlockRenderEngine.BLOCK_SIZE*3.3f,
									object.translation.z))) {

								if (checkWalls) feedback.collidedAtHead = true;

								if (checkGrounded) {
									if (velocity.y >= 0) {
										velocity.y = 0;
										acceleration.y = 0;

										float y = Math.min(block.col.point1.y, block.col.point2.y);

										object.translation.y = y - BlockRenderEngine.BLOCK_SIZE * 4;
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
