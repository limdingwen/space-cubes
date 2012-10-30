package com.github.limdingwen.SpaceCubes.Events;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.github.limdingwen.SpaceCubes.Main;
import com.github.limdingwen.SpaceCubes.BlockTypes.Material;
import com.github.limdingwen.SpaceCubes.File.ChunkLevelEncoder;
import com.github.limdingwen.SpaceCubes.Rendering.BlockRenderEngine;
import com.github.limdingwen.SpaceCubes.Rendering.RenderEngine;

public class IOEngine {
	public static void poll() {
		
		// Player walking
		if (Keyboard.isKeyDown(Keyboard.KEY_W))
			EventEngine.executeEvent(EventEngine.PLAYER, "playerMove", new PlayerMoveEvent(0f, -1f));
		if (Keyboard.isKeyDown(Keyboard.KEY_S))
			EventEngine.executeEvent(EventEngine.PLAYER, "playerMove", new PlayerMoveEvent(0f, 1f));
		if (Keyboard.isKeyDown(Keyboard.KEY_A))
			EventEngine.executeEvent(EventEngine.PLAYER, "playerMove", new PlayerMoveEvent(-1f, 0f));
		if (Keyboard.isKeyDown(Keyboard.KEY_D))
			EventEngine.executeEvent(EventEngine.PLAYER, "playerMove", new PlayerMoveEvent(1f, 0f));
		
		// Player jumping
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))
			EventEngine.executeEvent(EventEngine.PLAYER, "playerJump", new PlayerJumpEvent());
		
		// Player looking
		Main.player.playerRotate(new PlayerRotateEvent(new Vector2f(
				Mouse.getDX(),     // Rotate X
				Mouse.getDY())));  // Rotate Y
		
		// Build blocks
		if (Mouse.isButtonDown(1)) {
			EventEngine.executeEvent(EventEngine.PLAYER, "playerPlaceBlock",new PlayerPlaceBlock(new Vector3f(Main.player.translation.x, Main.player.translation.y + (BlockRenderEngine.BLOCK_SIZE), Main.player.translation.z), Main.player.inHand));
		}
		
		if (Mouse.isButtonDown(0)) {
			EventEngine.executeEvent(EventEngine.PLAYER, "playerBreakBlock", new PlayerBreakBlock(new Vector3f(Main.player.translation.x, Main.player.translation.y + (BlockRenderEngine.BLOCK_SIZE), Main.player.translation.z)));
		}		
		// Focusing
		
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			Mouse.setGrabbed(false);
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_TAB)) {
			Mouse.setGrabbed(true);
		}
		
		// Inventory
		
		if (Keyboard.isKeyDown(Keyboard.KEY_1)) Main.player.inHand = Material.DIRT;
		if (Keyboard.isKeyDown(Keyboard.KEY_2)) Main.player.inHand = Material.GRASS;
		if (Keyboard.isKeyDown(Keyboard.KEY_3)) Main.player.inHand = Material.ROCK;
		if (Keyboard.isKeyDown(Keyboard.KEY_4)) Main.player.inHand = Material.CRACKEDROCK;
		if (Keyboard.isKeyDown(Keyboard.KEY_5)) Main.player.inHand = Material.VOIDROCK;
		
		// Saving
		
		if (Keyboard.isKeyDown(Keyboard.KEY_V)) {			
			ChunkLevelEncoder.encodeWorld("TestWorld", RenderEngine.world, true);
		}
		
		// Respawn
		
		if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
			EventEngine.executeEvent(EventEngine.PLAYER, "playerRespawn",new PlayerRespawnEvent(true));
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_RETURN)) {
			Main.spawn = Main.player.translation;
		}
		
		// Shifting
		
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			Main.player.shifting = true;
		}
		else {
			Main.player.shifting = false;
		}
		
		// Time manupilation
		
		if (Keyboard.isKeyDown(Keyboard.KEY_T)) {
			RenderEngine.world.time = 0;
		}
	}
}
