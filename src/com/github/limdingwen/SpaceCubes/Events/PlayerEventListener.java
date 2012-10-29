package com.github.limdingwen.SpaceCubes.Events;

public interface PlayerEventListener {
	public void playerMove(PlayerMoveEvent event);
	public void playerRotate(PlayerRotateEvent event);
	public void playerJump(PlayerJumpEvent event);
	public void playerBreakBlock(PlayerBreakBlock event);
	public void playerPlaceBlock(PlayerPlaceBlock event);
	public void playerRespawn(PlayerRespawnEvent event);
}
