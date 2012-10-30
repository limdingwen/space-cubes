package com.github.limdingwen.SpaceCubes.World;

import org.lwjgl.opengl.Display;

import com.github.limdingwen.SpaceCubes.Debug;

public class WorldEngine {
	public WorldEngine() {
		
	}
	
	public static World createWorld() {
		Display.setTitle("Building world....");
		
		World world = new World();
		world.generate();
		
		return world;
	}
	
	public static void loadWorldIntoMemory(World world) {
		Debug.info("Loading world " + world.toString() + " into memory...");
		world.moveGraphics();
/*
		try {
			for (int wx = 0; wx < world.chunks.length; wx++) {
				for (int wz = 0; wz < world.chunks.length; wz++) {
					// Every chunk in the world...

					world.chunks[wx][wz].loadChunk();
				}
			}

			Debug.info("World " + world.toString() + " loaded!");
		}
		catch (OutOfMemoryError e) {
			Main.crash(e, "Out of memory while loading world " + world.toString(), 2);
		}*/
	}
}
