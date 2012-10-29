package com.github.limdingwen.SpaceCubes.File;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.github.limdingwen.SpaceCubes.Debug;
import com.github.limdingwen.SpaceCubes.Main;
import com.github.limdingwen.SpaceCubes.SystemNativesHelper;
import com.github.limdingwen.SpaceCubes.Rendering.RenderEngine;

public class LevelDataEncoder {
	public static void encodeLevel(String worldName) throws FileNotFoundException, IOException {
		DataOutputStream dataStream = null;
		File file = new File(SystemNativesHelper.defaultDirectory() + "/saves/" + worldName + "/level.dat");
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
			dataStream = new DataOutputStream(new FileOutputStream(file));
		} catch (FileNotFoundException ex) {
			Debug.warning("Cannot load level.dat: Cannot find file!");

			throw new FileNotFoundException();
		}
		
		// Write them into stream

		try {
			dataStream.writeFloat(Main.player.translation.x);
			dataStream.writeFloat(Main.player.translation.y);
			dataStream.writeFloat(Main.player.translation.z);

			dataStream.writeFloat(Main.player.rotation.x);
			dataStream.writeFloat(Main.player.rotation.y);
			dataStream.writeFloat(Main.player.rotation.z);

			dataStream.writeInt(RenderEngine.world.time);
			
			dataStream.writeFloat(Main.spawn.x);
			dataStream.writeFloat(Main.spawn.y);
			dataStream.writeFloat(Main.spawn.z);
		} catch (IndexOutOfBoundsException ex) {
			Debug.warning("Unable to write to stream!");
			
			dataStream.close();
			throw new IOException();
		}
		
		dataStream.close();
		
		Debug.info("Encoded level.dat successfully.");
	}
}
