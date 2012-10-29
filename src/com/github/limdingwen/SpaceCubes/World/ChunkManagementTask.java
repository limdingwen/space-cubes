/**
 * 
 */
package com.github.limdingwen.SpaceCubes.World;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.limdingwen.SpaceCubes.Debug;
import com.github.limdingwen.SpaceCubes.Main;
import com.github.limdingwen.SpaceCubes.File.ChunkLevelEncoder;
import com.github.limdingwen.SpaceCubes.File.ChunkLevelParser;
import com.github.limdingwen.SpaceCubes.Rendering.RenderEngine;

/**
 * @author wen
 *
 */
public class ChunkManagementTask extends TimerTask {
	public boolean[][] chunkLoads = null;

	@Override
	public void run() {
		chunkLoads = Main.chunkLoads;
		
		if (chunkLoads != null) {
			for (int ix = 0; ix < World.WORLD_LENGTH; ix++) {
				for (int iz = 0; iz < World.WORLD_LENGTH; iz++) {
					Chunk chunk = RenderEngine.world.chunks[ix][iz];
					boolean chunkLoad = chunkLoads[ix][iz];

					if (!chunk.getIsLoaded() && chunkLoad) {
						// Load the chunk

						try {
							chunk.blocks = ChunkLevelParser.parseChunk("TestWorld", ix, iz).blocks;
							chunk.moveGraphics();
						} catch (IOException e) {
							Debug.warning("Unable to parse chunk " + ix + ", " + iz);
							Debug.warning("Generating chunk!");

							Logger.getLogger(ChunkLevelParser.class.getName()).log(Level.INFO, null, e);

							// Generate new chunk

							chunk.generate();
							chunk.moveGraphics();
						}

						chunk.loadChunk();
						return;
					}
					else if (chunk.getIsLoaded() && !chunkLoad) {
						// Unload chunk

						try {
							ChunkLevelEncoder.encodeChunk("TestWorld", chunk, ix, iz);
						} catch (FileNotFoundException e) {
							Debug.error("Cannot save chunk: " + ix + ", " + iz);
							Debug.error("Data will be lost.");
							e.printStackTrace();
						} catch (IOException e) {
							Debug.error("Cannot save chunk: " + ix + ", " + iz);
							Debug.error("Data will be lost.");
							e.printStackTrace();
						}

						chunk.unloadChunk();
						return;
					}
				}
			}
		}
	}
}
