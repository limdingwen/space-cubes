package com.github.limdingwen.SpaceCubes.World;

import com.github.limdingwen.SpaceCubes.BlockTypes.Block;
import com.github.limdingwen.SpaceCubes.Rendering.BlockRenderEngine;

public class GraphicsMoveRunnable implements Runnable {
	Block[][][] blocks = null;
	int positionX;
	int positionY;
	
	public GraphicsMoveRunnable(Block[][][] blocks, int positionX, int positionY) {
		this.blocks = blocks;
		
		this.positionX = positionX;
		this.positionY = positionY;
	}
	
	@Override
	public void run() {
		for (int cx = 0; cx < World.CHUNK_LENGTH; cx++) {
			for (int cy = 0; cy < World.CHUNK_HEIGHT; cy++) {
				for (int cz = 0; cz < World.CHUNK_LENGTH; cz++) {
					// Every block in the chunk
					
					blocks[cx][cy][cz].translation.x += positionX * World.CHUNK_LENGTH; // Move blocks
					if (blocks[cx][cy][cz].col != null) { // Move colliders (check if it is collidable to prevent nullexception)
						blocks[cx][cy][cz].col.point1.x += positionX * World.CHUNK_LENGTH * (BlockRenderEngine.doubleBs);
						blocks[cx][cy][cz].col.point2.x += positionX * World.CHUNK_LENGTH * (BlockRenderEngine.doubleBs);
					}
					
					blocks[cx][cy][cz].translation.z += positionY * World.CHUNK_LENGTH;
					if (blocks[cx][cy][cz].col != null) {
						blocks[cx][cy][cz].col.point1.z += positionY * World.CHUNK_LENGTH * (BlockRenderEngine.doubleBs);
						blocks[cx][cy][cz].col.point2.z += positionY * World.CHUNK_LENGTH * (BlockRenderEngine.doubleBs);
					}
				}
			}
		}
				
	}

}
