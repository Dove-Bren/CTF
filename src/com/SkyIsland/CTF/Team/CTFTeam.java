package com.SkyIsland.CTF.Team;

import java.util.List;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Location;

import org.bukkit.entity.Player;

/**
 * This interface is for containing one CTF team and it's attributes. All these methods must be implemented correctly
 * for the entire plugin to work.
 * @author William
 *
 */
public interface CTFTeam {
	
	public void setPlayers(List<Player> list);
	
	public List<Player> getPlayers();
	
	public void addPlayer(Player player);
	
	public void removePlayer(Player player);
	
	//methods for score handling
	public int getScore();
	public void addToScore(int increment);
	public void subToScore(int decrement);
	
	//Methods for setting and getting Goal Regions
	public Region getGoal();
	public void setgoal(Region goal);

	//Methods for setting and getting Spawn Point
	public void setSpawn(Location spawnLocation);
	public Location getSpawn();
	
	//Checks to see if a player is in a team
	public boolean inTeam(Player player);
	
	public TeamPlayer getTeamPlayer(Player player);

	//ADD public setRespawn();
	
	
}
