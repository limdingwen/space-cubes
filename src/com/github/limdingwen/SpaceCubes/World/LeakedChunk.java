package com.github.limdingwen.SpaceCubes.World;

import java.util.ArrayList;

import com.github.limdingwen.SpaceCubes.DataTypes.Vector2i;
import com.github.limdingwen.SpaceCubes.DataTypes.Vector3i;

public class LeakedChunk {
	public Vector2i chunkRelativePos;
	public ArrayList<Vector3i> blocks = new ArrayList<Vector3i>();
	
	public LeakedChunk(Vector2i leakedPos) {
		chunkRelativePos = leakedPos;
	}

	public static LeakedChunk findSamePos(ArrayList<LeakedChunk> array, Vector2i chunkPos) {		
		for (int i = 0; i < array.size(); i++) {
			if (array.get(i).chunkRelativePos.equals(chunkPos)) {
				return array.get(i);
			}
		}
		
		return null;
	}
	
	public static Vector2i findLeakedPos(Vector3i relBlockPos) {
		return new Vector2i(
				(int) Math.floor(relBlockPos.x/World.CHUNK_LENGTH),
				(int) Math.floor(relBlockPos.z/World.CHUNK_LENGTH));
	}
}
