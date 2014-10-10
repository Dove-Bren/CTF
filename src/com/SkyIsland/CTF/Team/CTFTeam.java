package com.SkyIsland.CTF.Team;

import java.util.List;


import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * This interface is for containing one CTF team and it's attributes. All these methods must be implemented correctly
 * for the entire plugin to work.
 * @author William
 *
 */
public interface CTFTeam {
	
	
	//methods for lists of flagLocations
	public List<Location> getFlagLocations();
	public void setFlagLocations(List<Location> locations);
	
	//methods for lists of spawn locations
	public List<Location> getSpawnLocations();
	public void setSpawnLocations(List<Location> locations);
	
	//method for handling respawn event
	@EventHandler
	public void handleTeamPlayerDeath(PlayerDeathEvent e);
	
	@EventHandler
	public void handleTeamPlayerRespawn(PlayerRespawnEvent e);
	
	public void addPlayer(TeamPlayer player);
	
	public void removePlayer(TeamPlayer player);
	
	//methods for score handling
	public int getScore();
	public void addToScore(int increment);
	public void subToScore(int decrement);
	
	public void resetFlag();
	
	//Methods for setting and getting Goal Regions
	public Goal getGoal();
	public void setgoal(Goal goal);
	
	//Checks to see if a player is in a team
	public boolean inTeam(TeamPlayer player);
	
	public String getName();
	
	public DyeColor getColor();
	
	public void setColor(DyeColor color);
	
	public List<TeamPlayer> getTeamPlayers();
	
	public void setTeamPlayers(List<TeamPlayer> list);

	
	
}
