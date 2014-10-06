package com.SkyIsland.CTF.Team;

import java.util.List;

import org.bukkit.entity.Player;

public interface CTFTeam {
	
	public void setPlayers(List<Player> list);
	
	public List<Player> getPlayers();
	
	public void addPlayer(Player player);
	
	public void removePlayer(Player player);
	
	
}
