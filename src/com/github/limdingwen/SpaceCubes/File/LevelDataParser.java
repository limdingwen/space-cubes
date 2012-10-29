package com.github.limdingwen.SpaceCubes.File;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.lwjgl.util.vector.Vector3f;

import com.github.limdingwen.SpaceCubes.Debug;
import com.github.limdingwen.SpaceCubes.Main;
import com.github.limdingwen.SpaceCubes.SystemNativesHelper;

public class LevelDataParser {	
	public static LevelData parseLevel(String worldName) throws IOException {
		DataInputStream dataStream;
		
		try {
		dataStream = new DataInputStream(new FileInputStream(
				new File(SystemNativesHelper.defaultDirectory() + "/saves/" + worldName + "/level.dat")));
		}
		catch (FileNotFoundException e) {
			Debug.warning("Cannot load level data for " + worldName + ": Cannot find file!");
			
			throw new IOException();
		}
		
		LevelData ld = new LevelData();
		
		float x = dataStream.readFloat();
		float y = dataStream.readFloat();
		float z = dataStream.readFloat();
		
		float xr = dataStream.readFloat();
		float yr = dataStream.readFloat();
		float zr = dataStream.readFloat();
		
		int time = dataStream.readInt();
		
		Main.spawn.x = dataStream.readFloat();
		Main.spawn.y = dataStream.readFloat();
		Main.spawn.z = dataStream.readFloat();
		
		dataStream.close();
		
		ld.playerPosition = new Vector3f(x,y,z);
		ld.playerRotation = new Vector3f(xr,yr,zr);
		ld.time = time;
		
		Debug.info("Successfully parsed level.dat");
		
		return ld;
	}
}
