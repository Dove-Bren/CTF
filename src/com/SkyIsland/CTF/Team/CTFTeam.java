package com.SkyIsland.CTF.Team;

import java.util.List;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Location;

import org.bukkit.entity.Player;

public interface CTFTeam {
	
	public void setPlayers(List<Player> list);
	
	public List<Player> getPlayers();
	
	public void addPlayer(Player player);
	
	public void removePlayer(Player player);
	
	//methods for score handling
	public int getScore();
	public void addToScore(int increment);
	public void subToScore(int decrement);
	
	public Region getGoal();
	public void setgoal(Region goal);

	public void setSpawn(Location spawnLocation);
	public Location getSpawn();
	
	public boolean inTeam(Player player);
	
	public TeamPlayer getTeamPlayer(Player player);
	
	//ADD private Region Goal;
	//ADD public setRespawn();
	
	
}
