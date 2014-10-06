package com.SkyIsland.CTF;

import java.util.List;

import org.bukkit.entity.Player;

import com.SkyIsland.CTF.Team.CTFTeam;

public interface CTFSession {
	
	public List<CTFTeam> getTeams();
	
	public void createTeam(String name);
	
	public void createTeam(String name, List<Player> players);
	
	public void removeTeam(CTFTeam team);
	
	public void addPlayer(CTFTeam team, Player player);
	
	public void removePlayer(CTFTeam team, Player player);
	
	public boolean isRunning();
	
	public void start();
	
	public void stop();
	
}


/**
 * session.createTeam("");
 * 
 * 
 * for ("" "") 
 * 	 session.addPlayer("" "")
 * 
 * 
 * session.start();
 *
*
*
*
*
*/