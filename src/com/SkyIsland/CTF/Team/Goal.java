package com.SkyIsland.CTF.Team;

import com.sk89q.worldedit.regions.Region;

/**
 * This class contains the Region for the Goal
 * @author William
 *
 */
public class Goal {
	private Region GoalDimensions;
	
	/**
	 * This is the constructor for the Goal Class
	 * @param GoalDimensions
	 */
	public Goal (Region GoalDimensions) {
		this.GoalDimensions = GoalDimensions;
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
	
	
}
