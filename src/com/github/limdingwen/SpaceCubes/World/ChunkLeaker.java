package com.github.limdingwen.SpaceCubes.World;

import java.util.ArrayList;

import com.github.limdingwen.SpaceCubes.Debug;
import com.github.limdingwen.SpaceCubes.DataTypes.Vector3i;

public class ChunkLeaker {
	// East-west enabled = not North-south
	
	public static Vector3i leakPosition(Vector3i pos, boolean east) {
		Vector3i leakedPos = new Vector3i(pos.x, pos.y, pos.z);
		
		if (east) {
			if (leakedPos.x >= World.CHUNK_LENGTH)
				leakedPos.x -= World.CHUNK_LENGTH;
			else if (leakedPos.x < 0)
				leakedPos.x += World.CHUNK_LENGTH;
		}
		else {
			if (leakedPos.z >= World.CHUNK_LENGTH)
				leakedPos.z -= World.CHUNK_LENGTH;
			else if (leakedPos.z < 0)
				leakedPos.z += World.CHUNK_LENGTH;
		}
				
		return leakedPos;
	}
	
	public static LeakedChunk[] leakChunksByCube(Vector3i pos, int radius) {
		ArrayList<Vector3i> vectors = new ArrayList<Vector3i>();
		ArrayList<LeakedChunk> chunks = new ArrayList<LeakedChunk>();
		
		// Take all the points
		
		for (int ix=pos.x-radius; ix <= pos.x+radius; ix++) {
			for (int iy=pos.y-radius; iy <= pos.y+radius; iy++) {
				for (int iz=pos.z-radius; iz <= pos.z+radius; iz++) {
					vectors.add(new Vector3i(ix, iy, iz));
				}
			}
		}
		
		// Leak
		
		for (int i = 0; i < vectors.size(); i++) {
			Vector3i vec = vectors.get(i);
			Vector3i oldVec = new Vector3i(vec.x, vec.y, vec.z);
			
			if (vec.x < 0 || vec.x >= World.CHUNK_LENGTH) {
				vec.x = leakPosition(vec, true).x;
			}
			
			if (vec.z < 0 || vec.z >= World.CHUNK_LENGTH) {
				vec.z = leakPosition(vec, false).z;
			}
			
			if (LeakedChunk.findSamePos(chunks, 
					LeakedChunk.findLeakedPos(oldVec)) == null) {
				chunks.add(new LeakedChunk(LeakedChunk.findLeakedPos(oldVec)));
			}
			LeakedChunk leaked = LeakedChunk.findSamePos(chunks, 
					LeakedChunk.findLeakedPos(oldVec));

			leaked.blocks.add(vec);
		}

		return chunks.toArray(new LeakedChunk[chunks.size()]);
	}
}
