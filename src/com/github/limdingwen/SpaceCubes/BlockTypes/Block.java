package com.github.limdingwen.SpaceCubes.BlockTypes;

import com.github.limdingwen.SpaceCubes.Main;
import com.github.limdingwen.SpaceCubes.Collision.BoxCollider;
import com.github.limdingwen.SpaceCubes.DataTypes.Vector2i;
import com.github.limdingwen.SpaceCubes.DataTypes.Vector3i;
import com.github.limdingwen.SpaceCubes.Rendering.BlockRenderEngine;
import com.github.limdingwen.SpaceCubes.Rendering.RenderEngine;
import com.github.limdingwen.SpaceCubes.World.Chunk;
import com.github.limdingwen.SpaceCubes.World.World;

import org.lwjgl.util.vector.Vector3f;

public class Block {
	public Vector3i translation = null;
	
	public byte material;
	public byte meta;
	public BoxCollider col;
	
	public boolean front = false;
	public boolean left = false;
	public boolean right = false;
	public boolean up = false;
	public boolean bottom = false;
	public boolean back = false;
	public boolean comCulled = false;
	
	public boolean notUnderTheSun = true;
	
	public Vector2i parentChunkPos;
	
	public Block(byte mat, byte m, Vector3i t, Vector2i pcp) {	
		translation = t;
		
		material = mat;
		meta = m;
		
		parentChunkPos = pcp;
	}
	
	public static void updateBlock(Block block, boolean counted, boolean spreadLighting) {
		if (counted) Main.chunkUpdates++;
		
		// Update collision
		
		float bs = BlockRenderEngine.BLOCK_SIZE;
		
		if (Material.getMaterialFromID(block.material, block.meta).fullCollider && Material.getMaterialFromID(block.material, block.meta).collidable) {
			Vector3f p1 = new Vector3f(
					block.translation.x * (bs * 2) - bs,
					block.translation.y * (bs * 2) - bs,
					block.translation.z * (bs * 2) - bs);
			Vector3f p2 = new Vector3f(
					block.translation.x * (bs * 2) + bs,
					block.translation.y * (bs * 2) + bs,
					block.translation.z * (bs * 2) + bs);
			
			block.col = new BoxCollider(p1, p2);
		}
		else {
			block.col = null;
		}
		
		// Update rendering faces

		World w = RenderEngine.world;
		// Culling
		// Less writing and better performance by getting the pointer first
		Chunk ch = w.getChunkAtBlockCoords(block.translation);
		Block[][][] b = ch.blocks;
		// Get local block coordinates
		Vector3i c = World.getLocalBlockCoords(block.translation);
		// get chunk length
		int cl = World.CHUNK_LENGTH;

		// Check if block is beside transparent ones (culling & *critically* better performance)

		block.front =
				block.left=
						block.right=
								block.up=
										block.bottom=
												block.back=
				false; // Set rendering to false, then set to true later on.

		boolean edge = false;

		if (c.x+1 >= cl || // X+
				c.x-1 < 0 || // X- 
				c.z+1 >= cl || // Z+
				c.z-1 < 0 || // Z-
				c.y+1 >= World.CHUNK_HEIGHT || // Y+
				c.y-1 < 0) // Y-
			edge = true;

		if (edge) {
			if (!(c.y+1 >= World.CHUNK_HEIGHT)) {
				if (Material.getMaterialFromID(b[(int) c.x][(int) c.y+1][(int) c.z].material, block.meta).seeThru) block.up = true;
			}
			if (!(c.y-1 < 0)) {
				if (Material.getMaterialFromID(b[(int) c.x][(int) c.y-1][(int) c.z].material, block.meta).seeThru) block.bottom = true;
			}
			if (!(c.x+1 >= World.CHUNK_LENGTH)) {
				if (Material.getMaterialFromID(b[(int) c.x+1][(int) c.y][(int) c.z].material, block.meta).seeThru) block.right = true;
			}
			if (!(c.x-1 < 0)) {
				if (Material.getMaterialFromID(b[(int) c.x-1][(int) c.y][(int) c.z].material, block.meta).seeThru) block.left = true;
			}
			if (!(c.z+1 >= World.CHUNK_LENGTH)) {
				if (Material.getMaterialFromID(b[(int) c.x][(int) c.y][(int) c.z+1].material, block.meta).seeThru) block.front = true;
			}
			if (!(c.z-1 < 0)) {
				if (Material.getMaterialFromID(b[(int) c.x][(int) c.y][(int) c.z-1].material, block.meta).seeThru) block.back = true;
			}
		}

		if (!edge) {
			if (Material.getMaterialFromID(b[(int) c.x+1][(int) c.y][(int) c.z].material, block.meta).seeThru) block.right = true;
			if (Material.getMaterialFromID(b[(int) c.x-1][(int) c.y][(int) c.z].material, block.meta).seeThru) block.left = true;
			if (Material.getMaterialFromID(b[(int) c.x][(int) c.y+1][(int) c.z].material, block.meta).seeThru) block.up = true;
			if (Material.getMaterialFromID(b[(int) c.x][(int) c.y-1][(int) c.z].material, block.meta).seeThru) block.bottom = true;
			if (Material.getMaterialFromID(b[(int) c.x][(int) c.y][(int) c.z+1].material, block.meta).seeThru) block.front = true;
			if (Material.getMaterialFromID(b[(int) c.x][(int) c.y][(int) c.z-1].material, block.meta).seeThru) block.back = true;
		}
		
		if (block.up || block.bottom || block.left ||
				block.right || block.front || block.back) {
			block.comCulled = false;
		}
		else {
			block.comCulled = true;
		}
		
		Block.updateLighting(block, spreadLighting);
	}
	
	public static void updateLighting(Block block, boolean spread) {
		Chunk ch = RenderEngine.world.getChunkAtBlockCoords(block.translation);
		Vector3i c = World.getLocalBlockCoords(block.translation);
		
		// Update lighting
		// Ray-trace upwards
		
		boolean isBlocked = false;
		
		for (int i = block.translation.y+1; i < World.CHUNK_HEIGHT; i++) {
			Block blockRay = ch.blocks[c.x][i][c.z];
			
			if (!Material.getMaterialFromID(blockRay.material, blockRay.meta).transparent)
				isBlocked = true;
		}
		
		block.notUnderTheSun = isBlocked;
		
		// Spread to same column

		if (spread) {
			for (int i = 0; i < World.CHUNK_HEIGHT; i++) {
				if (i != c.y) 
					Block.updateLighting(ch.blocks[c.x][i][c.z], false);
			}
		}
	}
}
