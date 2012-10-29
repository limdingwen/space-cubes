package com.github.limdingwen.SpaceCubes.Rendering;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;

import com.github.limdingwen.SpaceCubes.Debug;
import com.github.limdingwen.SpaceCubes.Main;
import com.github.limdingwen.SpaceCubes.BlockTypes.Block;
import com.github.limdingwen.SpaceCubes.BlockTypes.Material;
import com.github.limdingwen.SpaceCubes.DataTypes.Vector2i;
import com.github.limdingwen.SpaceCubes.DataTypes.Vector3i;
import com.github.limdingwen.SpaceCubes.File.ChunkLevelParser;
import com.github.limdingwen.SpaceCubes.File.LevelData;
import com.github.limdingwen.SpaceCubes.File.LevelDataParser;
import com.github.limdingwen.SpaceCubes.World.Chunk;
import com.github.limdingwen.SpaceCubes.World.World;
import com.github.limdingwen.SpaceCubes.World.WorldEngine;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.BufferUtils;

public class RenderEngine {
	private static Stage stage = new Stage();
	public static Camera camera = new Camera(new Vector3f(0,0,0));
	public static BlockRenderEngine blockRenderEngine = new BlockRenderEngine();

	public static World world;

	public void init() {
		float density = 0.05f;
		float fogColor[] = {
				0.7f, 
				0.7f, 
				0.7f, 
				1.0f
		};
		FloatBuffer fogColorBuffer = BufferUtils.createFloatBuffer(4);
		fogColorBuffer.put(fogColor);
		fogColorBuffer.flip();

		// Init OpenGL

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GLU.gluPerspective(60, 1, 0.1f, 128);

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		//	GL11.glClearColor(0, 0.5f, 1, 1);

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_FOG);
		GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP2);
		GL11.glFog(GL11.GL_FOG_COLOR, fogColorBuffer);
		GL11.glFogf(GL11.GL_FOG_DENSITY, density);

		Debug.info("Initalized OpenGL");

		// Set player and time

		LevelData ld = null;

		try {
			ld = LevelDataParser.parseLevel("TestWorld");

			Main.player.translation = ld.playerPosition;
			Main.player.rotation = ld.playerRotation;
		} catch (IOException e) {
			Debug.warning("Cannot find level.dat, using defaults.");

			Main.player.translation = new Vector3f(0,0,0);
		}

		// World default

		Display.setTitle("Building world....");

		world = ChunkLevelParser.parseWorld("TestWorld", Main.player.translation);
		WorldEngine.loadWorldIntoMemory(world);

		Vector2i chunkPos = World.getChunkPosAtBlockCoords(
				World.coordRealToBlock(Main.player.translation));

		if (ld != null) world.time = ld.time;

		// Init spawn point
		/*
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

		Block spawnBlock = RenderEngine.world.getTopBlock(World.coordRealToBlock(Main.spawn).x,
				World.coordRealToBlock(Main.spawn).z);

		Vector3i spawnBlockVec;

		if (spawnBlock == null) {
			spawnBlockVec = new Vector3i(0, World.CHUNK_HEIGHT, 0);
		}
		else {
			spawnBlockVec = spawnBlock.translation;
		}

		Main.spawn = new Vector3f(
				spawnBlockVec.x * BlockRenderEngine.doubleBs,
				(spawnBlockVec.y + 1) * BlockRenderEngine.doubleBs,
				spawnBlockVec.z * BlockRenderEngine.doubleBs);*/
	}

	public void render() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);  // Clear background
		//	GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);

		GL11.glLoadIdentity();

		// Translate to block position
		RenderEngine.camera.lookThrough();

		for (int ix = 0; ix < World.WORLD_LENGTH; ix++) {
			for (int iz = 0; iz < World.WORLD_LENGTH; iz++) {
				// For every chunk...

				if (world.chunks[ix][iz].getIsLoaded()) {
					// If it is loaded

					Chunk chunk = world.chunks[ix][iz];

					if (chunk != null) {
						for (int bx = 0; bx < World.CHUNK_LENGTH; bx++) {
							for (int by = 0; by < World.CHUNK_HEIGHT; by++) {
								for (int bz = 0; bz < World.CHUNK_LENGTH; bz++) {
									// Render each block if it is not transparent

									if (chunk.blocks[bx][by][bz] != null) {
										if (!Material.getMaterialFromID(chunk.blocks[bx][by][bz].material, chunk.blocks[bx][by][bz].meta).transparent) {
											Block block = chunk.blocks[bx][by][bz];

											if (!block.comCulled) {
												blockRenderEngine.render(block);
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

		GL11.glFlush();
	}

	public void update() {		
	}

	public static  Stage getStage() {
		return stage;
	}

	public static void setStage(Stage stage) {
		RenderEngine.stage = stage;
	}
}
