package com.SkyIsland.CTF.EditGame;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;

import com.SkyIsland.CTF.Team.CTFTeam;
import com.SkyIsland.CTF.Team.Goal;
import com.SkyIsland.CTF.Team.TeamPlayer;
import com.sk89q.worldedit.regions.Region;

public final class EditGoal extends Goal {

	/**
	 * This constructor invokes the Abstract class Goal's constructor
	 * @param GoalDimensions
	 * @param myTeam
	 */
	public EditGoal(Region GoalDimensions, CTFTeam myTeam) {
		super(GoalDimensions, myTeam);
	}
	
	/**
	 * Determine if a goal is valid or not
	 * @param p The player in question
	 */
	@Override
	public boolean isValidScore(TeamPlayer p) {
		
		if (this.GoalTeam.equals(p.getTeam())){
			System.out.println("Is your own team");
			
			return containsEnemyWool(p, p.getTeam());
		}
		
		return false;
	}
	
	/**
	 * This method checks a player inventory for any wool and checks the wool color to the player's team
	 * If the wool colors do not match then a score has been made.
	 * Otherwise, envoke a CTF flag return
	 * @param p The player being analyzed
	 * @param team The Goal's team
	 * @return True if a player has scored a goal
	 */
	private boolean containsEnemyWool(TeamPlayer p, CTFTeam team) {
		Wool w = new Wool(this.GoalTeam.getColor());
		//Check for wool
		
		for (ItemStack m : p.getPlayer().getInventory()) {
			
			
			//If item in inventory is wool
			if (m != null && m.getType().equals(Material.WOOL))
			{
				
				System.out.println("Got some wool");
				
				for (CTFTeam t: ((EditTeam)this.GoalTeam).session.getTeams()){
					if (t.getColor().getWoolData() == m.getData().getData()){
						t.resetFlag();
					}
				}
				
				p.getPlayer().getInventory().remove(m);
				return true;
			}
		}
		return false;
	}
}
