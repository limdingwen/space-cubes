/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.limdingwen.SpaceCubes.File;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.github.limdingwen.SpaceCubes.Debug;
import com.github.limdingwen.SpaceCubes.SystemNativesHelper;
import com.github.limdingwen.SpaceCubes.World.Chunk;
import com.github.limdingwen.SpaceCubes.World.World;

/**
 *
 * @author wen
 */
public class ChunkLevelEncoder {
	public static void encodeChunk(String worldName, Chunk chunk, int x, int y) throws FileNotFoundException, IOException {
		FileOutputStream chunkStream = null;
		File file = new File(SystemNativesHelper.defaultDirectory() + "/saves/" + worldName + "/c" + x + "|" + y + ".chk");
		File fold = new File(SystemNativesHelper.defaultDirectory() + "/saves");
		File worldFold = new File(SystemNativesHelper.defaultDirectory() + "/saves/" + worldName);

		if (!fold.exists()) {
			fold.mkdir();
		}

		if (!worldFold.exists()) {
			worldFold.mkdir();
		}

		if (!file.exists()) {
			file.createNewFile();
		}

		try {
			chunkStream = new FileOutputStream(file);
		} catch (FileNotFoundException ex) {
			Debug.warning("Cannot load chunk at " + x + ", " + y + ": Cannot find file!");

			throw new FileNotFoundException();
		}

		byte[] ids = new byte[World.CHUNK_HEIGHT*World.CHUNK_LENGTH*World.CHUNK_LENGTH];
		byte[] metas = new byte[(World.CHUNK_HEIGHT*World.CHUNK_LENGTH*World.CHUNK_LENGTH)*2];

		for (int ix = 0; ix < World.CHUNK_LENGTH; ix++) {
			for (int iz = 0; iz < World.CHUNK_LENGTH; iz++) {
				for (int iy = 0; iy < World.CHUNK_HEIGHT; iy++) {
					ids[iy+(ix+iz*World.CHUNK_LENGTH)*World.CHUNK_HEIGHT] = chunk.blocks[ix][iy][iz].material;
					metas[(iy+(ix+iz*World.CHUNK_LENGTH)*World.CHUNK_HEIGHT)+(World.CHUNK_HEIGHT*World.CHUNK_LENGTH*World.CHUNK_LENGTH)] = chunk.blocks[ix][iy][iz].meta;
				}
			}
		}

		// Write them into stream

		try {
			chunkStream.write(ids, 0, World.CHUNK_HEIGHT*World.CHUNK_LENGTH*World.CHUNK_LENGTH); // ID
			chunkStream.write(metas, World.CHUNK_HEIGHT*World.CHUNK_LENGTH*World.CHUNK_LENGTH, World.CHUNK_HEIGHT*World.CHUNK_LENGTH*World.CHUNK_LENGTH); // Meta
		} catch (IndexOutOfBoundsException ex) {
			Debug.warning("Unable to write to stream!");

			chunkStream.close();
			throw new IOException();
		}

		chunkStream.close();

		Debug.info("Encoded chunk successfully. (" + x + "," + y + ")");
	}

	public static void encodeWorld(String worldName, World world, boolean onlyLoaded) {
		Debug.info("Saving world...");

		for (int ix = 0; ix < World.WORLD_LENGTH; ix++) {
			for (int iz = 0; iz < World.WORLD_LENGTH; iz++) {
				try {
					if (world.chunks[ix][iz].getIsLoaded()) encodeChunk(worldName, world.chunks[ix][iz], ix, iz);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
