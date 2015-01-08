package com.github.limdingwen.SpaceCubes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import com.github.limdingwen.SpaceCubes.DataTypes.Vector2i;
import com.github.limdingwen.SpaceCubes.EntityTypes.Player.Player;
import com.github.limdingwen.SpaceCubes.Events.IOEngine;
import com.github.limdingwen.SpaceCubes.File.ChunkLevelEncoder;
import com.github.limdingwen.SpaceCubes.File.ChunkLevelParser;
import com.github.limdingwen.SpaceCubes.File.LevelDataEncoder;
import com.github.limdingwen.SpaceCubes.Plugin.PluginEngine;
import com.github.limdingwen.SpaceCubes.Rendering.RenderEngine;
import com.github.limdingwen.SpaceCubes.World.Chunk;
import com.github.limdingwen.SpaceCubes.World.ChunkManagementTask;
import com.github.limdingwen.SpaceCubes.World.World;

public class Main {
	
	// VERSION: <Alpha/Beta/Release> <Version> Snapshot <Year>w<Week><Build>
	
	public static final String VERSION_NO = "Pre-Alpha 0.6.0 Release Snapshot 15w022";
	
	public static final int FPS = 60;
	
	public static RenderEngine renderer = new RenderEngine();
	
	public static int width;
	public static int height;
	
	public static Vector3f spawn = new Vector3f();
	public static Player player;
	
	public static float dt;
	private long lastTime;
	
	public static int chunkUpdates = 0;
	public static int verts = 0;
	public static int blocks = 0;
	
	public Timer saveTimer = new Timer();
	public Timer tickTimer = new Timer();
	public Timer chunkTimer = new Timer();
	public Timer physicsTimer = new Timer();
	
	private boolean physicsTimerStarted = false;
	
	public static final Vector3f dawn = new Vector3f(0, 0, 0.5f);
	public static final Vector3f morning = new Vector3f(0, 0, 0.75f);
	public static final Vector3f afternoon = new Vector3f(0, 0.5f, 1);
	public static final Vector3f evening = new Vector3f(0, 0, 0.7f);
	public static final Vector3f night = new Vector3f(0, 0, 0.5f);
	public static final Vector3f midnight = new Vector3f(0, 0, 0);
	
	public static int CHUNK_LOAD_RADIUS = 2;
	
	public static boolean[][] chunkLoads = null;
		
	public static void main(String[] args) {
		try {
			Debug.info("Space Cubes version is " + VERSION_NO);
			System.out.println();

			Main Test1 = new Main();
			Debug.info("Created Space Cubes.");
			Test1.start();
		}
		catch (Exception e) {
			crash(e, null, 1);
		}
	}
	
	public void start() {
		// Check for 64-bit
		
		if (!Sys.is64Bit()) Debug.warning("It is better you run Space Cubes in 64-bit. But it's ok.");
		
		try {
			Display.setDisplayMode(new DisplayMode(800,800));
			Display.create();
			Display.setTitle("Space Cubes " + VERSION_NO);
			Display.sync(FPS);
			
			Debug.info("Created display.");
			
			// Get width and height
			
			width = Display.getWidth();
			height = Display.getHeight();
			
			// Lock mouse in frame
			
			Mouse.setGrabbed(true);
		} catch (LWJGLException e) {
			Debug.error("Space Cubes has crashed on startup!");
			Debug.error("-- BEGIN ERROR REPORT --");
			e.printStackTrace();
			Debug.error("-- END ERROR REPORT --");
			Debug.error("Please submit this report to limdingwen@gmail.com.");
			
			System.exit(1);
		}	
		
		// Init player
		
		player = new Player(null, new Vector3f(), 
				new Vector3f(0,0,0), 0, null);
		
		renderer.init();
		
		player.addToStage(RenderEngine.getStage());
		
		PluginEngine.loadPlugins();
		
		Debug.info("Initialized renderer.");
		
		Debug.info("Started Space Cubes!");
		
		saveTimer.scheduleAtFixedRate(new SaveTask(), 5000, 5000);
		tickTimer.scheduleAtFixedRate(new TickUpdateTask(), 0, 50);
		chunkTimer.scheduleAtFixedRate(new ChunkManagementTask(), 0, 200);

		while (!Display.isCloseRequested()) {
			long timeUsed = Sys.getTime();
			
			renderer.render();
			renderer.update();
			
			long renderTime = Sys.getTime() - timeUsed;
			int rrenderTime = (int) Math.floor(renderTime);
			
			long processTime = Sys.getTime();
			IOEngine.poll();
			player.playerUpdate();
			
			processTime = Sys.getTime() - processTime;
			int rprocessTime = (int) Math.floor(processTime);

			// Manage chunks
			
			Vector2i playerChunkPos = World.getChunkPosAtBlockCoords(
					World.coordRealToBlock(Main.player.translation));
			
			chunkLoads = new boolean[World.WORLD_LENGTH][World.WORLD_LENGTH];
			
			for (int ix = 0; ix < World.WORLD_LENGTH; ix++) {
				for (int iz = 0; iz < World.WORLD_LENGTH; iz++) {
					chunkLoads[ix][iz] = false;
					RenderEngine.world.chunks[ix][iz].render = false;
				}
			}
			
			for (int ix = playerChunkPos.x - CHUNK_LOAD_RADIUS; ix <= playerChunkPos.x + CHUNK_LOAD_RADIUS; ix++) {
				for (int iz = playerChunkPos.y - CHUNK_LOAD_RADIUS; iz <= playerChunkPos.y + CHUNK_LOAD_RADIUS; iz++) {
					int ix2 = ix - playerChunkPos.x;
					int iz2 = iz - playerChunkPos.y;
					
					byte sx = 0;
					byte sz = 0;
					
					if (ix2 < 0) sz = -1;
					else if (ix2 > 0) sz = 1;
					
					if (iz2 < 0) sx = -1;
					else if (iz2 > 0) sx = 1;
					
					// Set up rules
					
					byte ruleX = 0;
					byte ruleZ = 0;
					
					switch (player.direction) {
					case Player.NORTH: ruleZ = 1; break;
					case Player.NORTHEAST: ruleZ = 1; ruleX = 1; break;
					case Player.EAST: ruleX = 1; break;
					case Player.SOUTHEAST: ruleZ = -1; ruleX = 1; break;
					case Player.SOUTH: ruleZ = -1; break;
					case Player.SOUTHWEST: ruleZ = -1; ruleX = -1; break;
					case Player.WEST: ruleX = -1; break;
					case Player.NORTHWEST: ruleZ = 1; ruleX = -1; break;
					}

					if (sx == 0) sx = ruleX;
					if (sz == 0) sz = ruleZ;
										
					if ((sx == ruleX || ruleX == 0) && (sz == ruleZ || ruleZ == 0)) {
						try {
							RenderEngine.world.chunks[ix][iz].render = true;
						}
						catch (ArrayIndexOutOfBoundsException e) {
						}
					}
					
					try {
						chunkLoads[ix][iz] = true;
					}
					catch (ArrayIndexOutOfBoundsException e) {
					}
				}
			}
			
			Chunk playerChunk = null;
			
			try {
				playerChunk = RenderEngine.world.chunks[playerChunkPos.x][playerChunkPos.y];
			}
			catch (ArrayIndexOutOfBoundsException e) {
			}

			if (playerChunk != null) {
				if (!playerChunk.getIsLoaded()) {	
					// Load the playerChunk
					
					try {
						playerChunk.blocks = ChunkLevelParser.parseChunk("TestWorld", playerChunkPos.x, playerChunkPos.y).blocks;
						playerChunk.moveGraphics();
					} catch (IOException e) {
						Debug.warning("Unable to parse playerChunk " + playerChunkPos.x + ", " + playerChunkPos.y);
						Debug.warning("Generating playerChunk!");

						Logger.getLogger(ChunkLevelParser.class.getName()).log(Level.INFO, null, e);

						// Generate new playerChunk

						playerChunk.generate();
						playerChunk.moveGraphics();
					}

					playerChunk.loadChunk();
				}
			}
			
			if (!physicsTimerStarted) {
				physicsTimer.schedule(player.physic, 0, 15);
				physicsTimerStarted = true;
			}

			// Time sync
			
			dt = (Sys.getTime() - lastTime)/4f;
			lastTime = Sys.getTime();

			long fps;
			
			try {
				fps = Sys.getTimerResolution()/(Sys.getTime() - timeUsed);
			}
			catch (ArithmeticException e) {
				fps = 9001;
			}
			int rfps = (int) Math.floor(fps);
			
			Display.setTitle(
					"R:" + Integer.toString(rrenderTime) +
					"ms P: " + Integer.toString(rprocessTime) +
					"ms FPS: " + Integer.toString(rfps) +
					" CUpdt: " + Integer.toString(chunkUpdates) + 
					" V: " + Integer.toString(verts) +
					" B: " + Integer.toString(blocks) +
					" Hand: " + player.inHand.name + 
					" Time: " + (int) ((Math.floor(RenderEngine.world.time/60/20)+6)%24) + 
					":" + (int) Math.floor((RenderEngine.world.time/20)%60) + 
					" Mem: " + (Runtime.getRuntime().totalMemory() - 
									Runtime.getRuntime().freeMemory())
									/ Runtime.getRuntime().maxMemory() * 100 + "%" +
					" Dir: " + Player.decodeDirectionToString(player.direction));
			
			verts = 0;
			chunkUpdates = 0;
			blocks = 0;
			
			// Update display
		    Display.update();
			
			// Update sky color
			
			if (RenderEngine.world.time >= World.DAWN && RenderEngine.world.time < World.MORNING) GL11.glClearColor(dawn.x, dawn.y, dawn.z, 1);
			if (RenderEngine.world.time >= World.MORNING && RenderEngine.world.time < World.AFTERNOON) GL11.glClearColor(morning.x, morning.y, morning.z, 1);
			if (RenderEngine.world.time >= World.AFTERNOON && RenderEngine.world.time < World.EVENING) GL11.glClearColor(afternoon.x, afternoon.y, afternoon.z, 1);
			if (RenderEngine.world.time >= World.EVENING && RenderEngine.world.time < World.NIGHT) GL11.glClearColor(evening.x, evening.y, evening.z, 1);
			if (RenderEngine.world.time >= World.NIGHT && RenderEngine.world.time < World.MIDNIGHT) GL11.glClearColor(night.x, night.y, night.z, 1);
			if (RenderEngine.world.time >= World.MIDNIGHT) GL11.glClearColor(midnight.x, midnight.y, midnight.z, 1);
		
			if (fps > 60) {
				
			}
		}
		
		// Stopped (out of main loop)
		
		// Save level
				
		Display.destroy();
		ChunkLevelEncoder.encodeWorld("TestWorld", RenderEngine.world, true);
		try {
			LevelDataEncoder.encodeLevel("TestWorld");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		tickTimer.cancel();
		saveTimer.cancel();
		chunkTimer.cancel();
		physicsTimer.cancel();

		Debug.info("Stopped timers");

		PluginEngine.disablePlugins();
		
		System.out.println();
		Debug.info("Destroyed display");
		Debug.info("Stopped with no errors!");
	}
	
	public static void crash(Throwable e, String message, int status) {
		Debug.error("Space Cubes has crashed!");
		Debug.error("== BEGIN FATAL ERROR REPORT ==");
		Debug.error("Respondant message:");
		
		if (message != null) {
			Debug.error(message);
		}
		else {
			Debug.error("There's no message, so this is an unexpected error!");
		}
		
		Logger.getLogger("CrashReporter").log(Level.SEVERE, "CrashStackTrace", e);
		
		Debug.error("== END FATAL ERROR REPORT ==");
		Debug.error("System exitted with status " + status);
		
		System.exit(status);
	}
	
	public static void cannotKeepUp() {
		Debug.warning("Cannot keep up! Did the system time change, or is the software overloaded?");
	}
}
