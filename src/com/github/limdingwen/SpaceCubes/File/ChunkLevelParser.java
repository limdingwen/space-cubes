/* * To change this template, choose Tools | Templates * and open the template in the editor. */package com.github.limdingwen.SpaceCubes.File;import java.io.FileInputStream;import java.io.FileNotFoundException;import java.io.IOException;import java.util.logging.Level;import java.util.logging.Logger;import org.lwjgl.util.vector.Vector3f;import com.github.limdingwen.SpaceCubes.Debug;import com.github.limdingwen.SpaceCubes.SystemNativesHelper;import com.github.limdingwen.SpaceCubes.BlockTypes.Block;import com.github.limdingwen.SpaceCubes.BlockTypes.Material;import com.github.limdingwen.SpaceCubes.DataTypes.Vector2i;import com.github.limdingwen.SpaceCubes.DataTypes.Vector3i;import com.github.limdingwen.SpaceCubes.World.Chunk;import com.github.limdingwen.SpaceCubes.World.World;/** * * @author wen */public class ChunkLevelParser {	public static Chunk parseChunk(String worldName, int x, int y) throws IOException {		Block[][][] blocks = new Block[World.CHUNK_LENGTH][World.CHUNK_HEIGHT][World.CHUNK_LENGTH];				FileInputStream chunkStream = null;				try {			chunkStream = new FileInputStream(SystemNativesHelper.defaultDirectory() + "/saves/" + worldName + "/c" + x + "|" + y + ".chk");		} catch (FileNotFoundException ex) {			Debug.warning("Cannot load chunk at " + x + ", " + y + ": Cannot find file!");						throw new IOException();		}				byte[] ids = new byte[World.CHUNK_HEIGHT*World.CHUNK_LENGTH*World.CHUNK_LENGTH];		byte[] metas = new byte[World.CHUNK_HEIGHT*World.CHUNK_LENGTH*World.CHUNK_LENGTH*2];				try {			chunkStream.read(ids, 0, World.CHUNK_HEIGHT*World.CHUNK_LENGTH*World.CHUNK_LENGTH);		} catch (Exception ex) {			Debug.warning("Unable to load ids for chunk " + x + ", " + y);						chunkStream.close();			throw new IOException();		}		try {			chunkStream.read(metas, World.CHUNK_HEIGHT*World.CHUNK_LENGTH*World.CHUNK_LENGTH, World.CHUNK_HEIGHT*World.CHUNK_LENGTH*World.CHUNK_LENGTH);		} catch (IndexOutOfBoundsException ex) {			Debug.warning("Unable to load metas for chunk " + x + ", " + y);						chunkStream.close();			throw new IOException();		}		chunkStream.close();				for (int ix = 0; ix < World.CHUNK_LENGTH; ix++) {			for (int iz = 0; iz < World.CHUNK_LENGTH; iz++) {				for (int iy = 0; iy < World.CHUNK_HEIGHT; iy++) {					blocks[ix][iy][iz] = new Block(ids[iy+(ix+iz*World.CHUNK_LENGTH)*World.CHUNK_HEIGHT], 							metas[(iy+(ix+iz*World.CHUNK_LENGTH)*World.CHUNK_HEIGHT)+(World.CHUNK_HEIGHT*World.CHUNK_LENGTH*World.CHUNK_LENGTH)], new Vector3i(							ix, iy, iz), new Vector2i(x, y));				}			}		}				Debug.info("Parsed chunk successfully. (" + x + "," + y + ")");				// Create chunk with blocks				return new Chunk(blocks, x, y);	}		public static World parseWorld(String worldname) {		Chunk[][] chunks = new Chunk[World.WORLD_LENGTH][World.WORLD_LENGTH];		for (int wx = 0; wx < chunks.length; wx++) {			for (int wz = 0; wz < chunks.length; wz++) {								chunks[wx][wz] = new Chunk(null, wx, wz);			}		}				// Load the chunk		return new World().setChunks(chunks);	}}