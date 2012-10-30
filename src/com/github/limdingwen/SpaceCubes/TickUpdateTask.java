package com.github.limdingwen.SpaceCubes;

import java.util.TimerTask;

import org.lwjgl.Sys;

import com.github.limdingwen.SpaceCubes.BlockTypes.Block;
import com.github.limdingwen.SpaceCubes.BlockTypes.Material;
import com.github.limdingwen.SpaceCubes.Rendering.RenderEngine;
import com.github.limdingwen.SpaceCubes.World.Chunk;
import com.github.limdingwen.SpaceCubes.World.World;

public class TickUpdateTask extends TimerTask {

	@Override
	public void run() {
		long startTime = Sys.getTime();

		// CRITICAL SECTION: NO EXPENSIVE OPERATIONS ALLOWED (1/20)

		// Update time

		RenderEngine.world.time++;

		if (RenderEngine.world.time > World.END) {
			RenderEngine.world.time = World.DAWN;
		}

		// Update the Player's chunk

		Chunk c = RenderEngine.world.getChunkAtBlockCoords(World.coordRealToBlock(Main.player.translation));

		if (c.getIsLoaded()) {
			for (int ix = 0; ix < World.CHUNK_LENGTH; ix++) {
				for (int iy = 0; iy < World.CHUNK_HEIGHT; iy++) {
					for (int iz = 0; iz < World.CHUNK_LENGTH; iz++) {
						Block b = c.blocks[ix][iy][iz];

						if (b != null) {

							// Update dirt to grass

							if (Material.getMaterialFromID(b.material, b.meta) == Material.DIRT) {
								if (Math.round(Math.random()*50) == 0) {
									Block bTop;

									try {
										bTop = c.blocks[ix][iy + 1][iz];
									}
									catch (Exception e) {
										break;
									}

									if (Material.getMaterialFromID(bTop.material, bTop.meta).transparent) {
										b.material = Material.GRASS.id;
										b.meta = Material.GRASS.meta;

										Block.updateBlock(b, true, true);
									}
								}
							}

							// Update grass to dirt

							if (Material.getMaterialFromID(b.material, b.meta) == Material.GRASS) {
								if (Math.round(Math.random()*50) == 0) {
									Block bTop;

									try {
										bTop = c.blocks[ix][iy + 1][iz];
									}
									catch (Exception e) {
										break;
									}

									if (!Material.getMaterialFromID(bTop.material, bTop.meta).transparent) {
										b.material = Material.DIRT.id;
										b.meta = Material.DIRT.meta;

										Block.updateBlock(b, true, true);
									}
								}
							}
						}
					}
				}
			}

			long endTime = Sys.getTime();
			long goodTime = 1000 / 20;

			if (endTime - startTime > goodTime) {
				Main.cannotKeepUp();
			}
		}
	}
}
