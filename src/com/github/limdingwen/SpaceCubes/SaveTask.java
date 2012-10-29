package com.github.limdingwen.SpaceCubes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.TimerTask;

import com.github.limdingwen.SpaceCubes.File.ChunkLevelEncoder;
import com.github.limdingwen.SpaceCubes.File.LevelDataEncoder;
import com.github.limdingwen.SpaceCubes.Rendering.RenderEngine;
import com.github.limdingwen.SpaceCubes.World.Chunk;
import com.github.limdingwen.SpaceCubes.World.World;

public class SaveTask extends TimerTask {

	@Override
	public void run() {
		Chunk chunk = RenderEngine.world.getChunkAtBlockCoords(
				World.coordRealToBlock(Main.player.translation));
		
		if (chunk == null) 
			return;
		
		try {
			ChunkLevelEncoder.encodeChunk("TestWorld", chunk, chunk.getPositionX(), chunk.getPositionY());
			LevelDataEncoder.encodeLevel("TestWorld");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
