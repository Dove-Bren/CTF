package com.SkyIsland.CTF.Team;

import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class TeamPlayer {
	
	private Player player;
	
	private boolean hasFlag;
	
	private CTFTeam team;
	
	private static Random rand = new Random();
	
	private Location leaveLocation;
	
	private int points;
	
	
	
	
	public TeamPlayer(Player player) {
		this.player = player;
		this.hasFlag = false;
		team = null;
		leaveLocation = player.getWorld().getSpawnLocation();
		points = 0;
	}
	
	
	
	public Player getPlayer() {
		return this.player;
	}
	
	public boolean hasFlag() {
		return this.hasFlag;
	}
	
	public CTFTeam getTeam() {
		return this.team;
	}
	
	public void setTeam(CTFTeam team) {
		this.team = team;
	}
	
	public Location getLeaveLocation() {
		return this.leaveLocation;
	}
	
	public void setLeaveLocation(Location loc) {
		this.leaveLocation = loc;
	}
	
	
	
	public int getPoints() {
		return points;
	}



	public void setPoints(int points) {
		this.points = points;
	}



	/**
	 * Simulates a spawn by teleporting the players to a set location.
	 */
	public void spawn() {
		List<Location> spawnLocs = team.getSpawnLocations();
		if (spawnLocs == null || spawnLocs.isEmpty()) {
			player.sendMessage("You were not able to be spawned because no spawn locations for your team"
					+ " were set. Please inform the admin.");
			return;
		}
		
		player.teleport(spawnLocs.get(  rand.nextInt(spawnLocs.size())));
		PlayerInventory playerInventory = player.getInventory();
		playerInventory.clear();
		if (team.getInventory() != null){
			for (ItemStack i: team.getInventory()){
				if (i != null){
					
					switch(i.getType()){
					case LEATHER_BOOTS:
					case IRON_BOOTS:
					case GOLD_BOOTS:
					case DIAMOND_BOOTS:
						playerInventory.setBoots(i.clone());
						break;
					case LEATHER_HELMET:
					case IRON_HELMET:
					case GOLD_HELMET:
					case DIAMOND_HELMET:
						playerInventory.setHelmet(i.clone());
						break;
					case LEATHER_CHESTPLATE:
					case IRON_CHESTPLATE:
					case GOLD_CHESTPLATE:
					case DIAMOND_CHESTPLATE:
						playerInventory.setChestplate(i.clone());
						break;
					case LEATHER_LEGGINGS:
					case IRON_LEGGINGS:
					case GOLD_LEGGINGS:
					case DIAMOND_LEGGINGS:
						playerInventory.setLeggings(i.clone());
						break;
					case WOOD_SWORD:
					case IRON_SWORD:
					case GOLD_SWORD:
					case DIAMOND_SWORD:
						playerInventory.setItemInHand(i.clone());
						break;
					default:
						playerInventory.addItem(i.clone());
					}
					
				}
			}
		}
		
	}
	
	/**
	 * Teleports the player out of the game so they don't leave and run around without a team
	 */
	public void moveLeave() {
		player.sendMessage("You were teleported away from the session.");
		player.teleport(leaveLocation);
	}
	
}
