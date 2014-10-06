package com.SkyIsland.CTF.Team;

import org.bukkit.entity.Player;

public class TeamPlayer {
	
	private Player player;
	
	private boolean hasFlag;
	
	
	
	
	public TeamPlayer(Player player) {
		this.player = player;
		this.hasFlag = false;
	}
	
	
	
	public Player getPlayer() {
		return this.player;
	}
	
	public boolean hasFlag() {
		return this.hasFlag;
	}
	
	
}
