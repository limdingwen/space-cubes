package com.github.limdingwen.SpaceCubes.Rendering;

import com.github.limdingwen.SpaceCubes.BlockTypes.Block;
import com.github.limdingwen.SpaceCubes.BlockTypes.Material;

public class BlockRenderEngine {
	public static final float BLOCK_SIZE = 0.5f;
	public static final float SHADOW_COLOR_DECREASE = 0.0f;
	
	public static float doubleBs = BLOCK_SIZE*2;

	public BlockRenderEngine() {
		PrimitivesEngine.init(); // Init after init BLOCK_SIZE
	}
	
	public void render(Block block) throws IndexOutOfBoundsException {
		// Get rendering faces
/*
		GL11.glTranslatef(block.translation.x*doubleBs, 
				block.translation.y*(doubleBs),
				block.translation.z*(doubleBs));
*/
		// Finally, render.
		Material mat = Material.getMaterialFromID(block.material, block.meta);
		
		if (mat == null) {
			mat = Material.AIR;
			block.material = Material.AIR.id;
			block.meta = Material.AIR.meta;
			
			System.out.println("Block at position " + block.translation.toString() + " has id " +
					block.material + " and meta " + block.meta + " which is unregonised. Changing to air.");
		}
		
		PrimitivesEngine.clColorCube(BLOCK_SIZE, mat.color.x, 
				mat.color.y, mat.color.z,
				block.front, block.left, block.right, block.up, block.bottom, block.back, block.notUnderTheSun,
				block.translation.x*doubleBs,
				block.translation.y*doubleBs,
				block.translation.z*doubleBs);
/*
		GL11.glTranslatef(-block.translation.x*(doubleBs), 
				-block.translation.y*(doubleBs), 
				-block.translation.z*(doubleBs));*/
	}
}
