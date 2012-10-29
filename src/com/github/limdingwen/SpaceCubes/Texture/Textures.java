package com.github.limdingwen.SpaceCubes.Texture;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import com.github.limdingwen.SpaceCubes.DataTypes.Vector2i;
import com.github.limdingwen.SpaceCubes.Exceptions.NullTextureGridPositionException;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

public class Textures {
	// Type/allocation reference:
	// 0: terrain
	// 1: crosshair
	
	// Index for terrain goes this way:
	// 1 2 3 4 5
	// 6 7 ...
	
	private ByteBuffer[] buffers = null;
	
	public String texPack = "DefaultPack";
	
	public void loadImages() {
		// Load terrain.png
		buffers[0] = loadImage("terrain");
		
		// crosshair.png
		buffers[1] = loadImage("crosshair");
	}
	
	public ByteBuffer loadImage(String name) {
		InputStream in = null;
		try {
			in = new FileInputStream("textures/" + texPack + "/" + name + ".png");
		} catch (FileNotFoundException e) {
			e.printStackTrace();			
		}
		
		ByteBuffer buf = null;

		try {
			PNGDecoder decoder = new PNGDecoder(in);

			buf = ByteBuffer.allocateDirect(4*decoder.getWidth()*decoder.getHeight());
			decoder.decode(buf, decoder.getWidth()*4, Format.RGBA);
			buf.flip();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Successfully loaded " + name + ".png");
		
		return buf;
	}
	
	public Vector2i getPositionFromGrid(int index, int lengthX, int lengthY) throws NullTextureGridPositionException {
		Vector2i res = new Vector2i();
		
		res.x = index % lengthX;
		res.y = (int) Math.floor(index / lengthX);
		
		if (res.x > lengthX || res.y > lengthY) 
			throw new NullTextureGridPositionException("Out of bounds!");
		
		return res;
	}
}
