package com.SkyIsland.CTF.Team;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.Region;

/**
 * This class contains the Region for the Goal but some more specified methods must be defined
 * @author William
 *
 */
public abstract class Goal {
	
	protected Region GoalDimensions;
	protected CTFTeam GoalTeam;
	
	/**
	 * This is the constructor for the Goal Class
	 * @param GoalDimensions
	 */
	public Goal (Region GoalDimensions, CTFTeam myTeam) {
		this.GoalDimensions = GoalDimensions;
		this.GoalTeam = myTeam;
	}
	
	/**
	 * The getter for Goal Dimensions
	 * @return The dimensions of the Goal
	 */
	public Region getGoalDimensions() {
		return GoalDimensions;
	}
	
	/**
	 * The setter for Goal Dimensions
	 * @param goalDimensions This sets the dimension for the Goal
	 */
	public void setGoalDimensions(Region goalDimensions) {
		GoalDimensions = goalDimensions;
	}
	
	/**
	 * This determines if the player within the goal has actually scored, specific to the game mode
	 * @param p The player 
	 * @return True if a player scores
	 */
	public abstract boolean isValidScore(TeamPlayer p);
	
	/**
	 * This method determines if the given player location is within this Goal's dimensions
	 * @param playerLocation The player's location
	 * @return True if the player is in the Goal, false if otherwise
	 */
	public boolean isInGoal(Location playerLocation) {
		Vector Max = GoalDimensions.getMaximumPoint();
		Vector Min = GoalDimensions.getMinimumPoint();
		
		//Check to see if the player is in the goal
		if (playerLocation.getBlockX() >= Min.getBlockX() && playerLocation.getBlockX() <= Max.getBlockX()) {
			if (playerLocation.getBlockY() >= Min.getBlockY() && playerLocation.getBlockY() <= Max.getBlockY()) {
				if (playerLocation.getBlockZ() >= Min.getBlockZ() && playerLocation.getBlockZ() <= Max.getBlockZ()) {
					//The player is in the goal
					return true;
				}
			}
		}
		//The player is not in the goal
		return false;
	}
	
}
