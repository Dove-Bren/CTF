package com.SkyIsland.CTF;

import java.util.List;


import com.SkyIsland.CTF.Team.CTFTeam;
import com.SkyIsland.CTF.Team.TeamPlayer;

public interface CTFSession {
	
	public List<CTFTeam> getTeams();
	
	public CTFTeam createTeam(String name);
	
	public CTFTeam createTeam(String name, List<TeamPlayer> players);
	
	public void removeTeam(CTFTeam team);
	
	public void addPlayer(CTFTeam team, TeamPlayer player);
	
	public void removePlayer(CTFTeam team, TeamPlayer player);
	
	public boolean isRunning();
	
	public void start();
	
	public void stop();
	
	public String getName();
	
	public boolean hasTeam(String name);
	
	public CTFTeam getTeam(String name);
	
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