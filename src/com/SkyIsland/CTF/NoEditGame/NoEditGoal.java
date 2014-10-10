package com.SkyIsland.CTF.NoEditGame;

import org.bukkit.entity.Player;

import com.SkyIsland.CTF.Team.CTFTeam;
import com.SkyIsland.CTF.Team.Goal;
import com.sk89q.worldedit.regions.Region;

public final class NoEditGoal extends Goal {

	/**
	 * This constructor invokes the Abstract class Goal's constructor
	 * @param GoalDimensions
	 * @param myTeam
	 */
	public NoEditGoal(Region GoalDimensions, CTFTeam myTeam) {
		super(GoalDimensions, myTeam);
	}
	
	/**
	 * Determine if a goal is valid or not
	 * @param p The player in question
	 */
	@Override
	public boolean isValidScore(Player p) {
		if (this.GoalTeam.inTeam(p))
			return doesPlayerHaveGoal(p);
		return false;
	}

	private boolean doesPlayerHaveGoal (Player p) {
		return false;
	}
	
}
