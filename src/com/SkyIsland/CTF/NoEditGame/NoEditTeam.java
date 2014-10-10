package com.SkyIsland.CTF.NoEditGame;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;





import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.Wool;
import org.bukkit.scoreboard.Score;

import com.SkyIsland.CTF.CTFPlugin;
import com.SkyIsland.CTF.Team.CTFTeam;
import com.SkyIsland.CTF.Team.Goal;
import com.SkyIsland.CTF.Team.TeamPlayer;

public class NoEditTeam implements CTFTeam {

	private String TeamName;
	private List<TeamPlayer> teamPlayers;
	private List<Location> spawnLocations;
	private List<Location> flagLocations;
	private int score;
	private Goal TeamGoal;
	private DyeColor teamColor;
	private Score teamScore;
	
	public NoEditSession session;
	
	//Constructors
	
	/**
	 * This constructor allows for specification of the Team name
	 * @param TeamName
	 */
	public NoEditTeam(NoEditSession session, String TeamName, Score score) {
		this.session = session;
		setTeamName(TeamName);
		teamScore = score;
		teamPlayers = new LinkedList<TeamPlayer>();
		this.score = 0;
		setgoal(null);
		setSpawnLocations(new LinkedList<Location>());
		setFlagLocations(new LinkedList<Location>());
	}
	
	/**
	 * This is the main constructor, all fields are specified
	 * @param Players The list of players to be in the team
	 * @param score The current score of the team
	 * @param TeamGoal The Goal Region of the team
	 * @param SpawnLocation The spawn location of the team players
	 */
	public NoEditTeam(NoEditSession session, String TeamName, Score teamScore, List<TeamPlayer> Players, int score, Goal TeamGoal, List<Location> spawnLocations, List<Location> flagLocations) {
		this.session = session;
		setTeamName(TeamName);
		this.teamScore = teamScore;
		setTeamPlayers(Players);
		this.score = 0;
		setgoal(TeamGoal);
		setSpawnLocations(spawnLocations);
		setFlagLocations(flagLocations);
	}
	
	/**
	 * This method sets the Team's name
	 * @param TeamName The new Team Name
	 */
	public void setTeamName(String TeamName) {
		this.TeamName = TeamName;
	}
	
	/**
	 * This method returns the Team name
	 * @return The Team name
	 */
	public String GetTeamName() {
		return this.TeamName;
	}
	//Below are method inherited by the CTFTeam Interface
	@Override
	public void setTeamPlayers(List<TeamPlayer> list) {
		this.teamPlayers = list;
	}


	@Override
	public void addPlayer(TeamPlayer player) {
		this.teamPlayers.add(player);
		player.setTeam(this);
	}

	@Override
	public void removePlayer(TeamPlayer player) {
		this.teamPlayers.remove(player);
		player.setTeam(null);
	}

	@Override
	public int getScore() {
		return score;
	}

	@Override
	public void addToScore(int increment) {
		this.score += increment;
		this.teamScore.setScore(score);
	}

	@Override
	public void subToScore(int decrement) {
		this.score -= decrement;
		this.teamScore.setScore(score);
	}
	
	@Override
	public void resetScore() {
		this.score = 0;
		this.teamScore.setScore(score);
	}
	
	
	@Override
	public Goal getGoal() {
		return this.TeamGoal;
	}

	@Override
	public void setgoal(Goal goal) {
		this.TeamGoal = goal;
	}

	@Override
	public boolean inTeam(TeamPlayer player) {
		return this.teamPlayers.contains(player);
	}

	@Override
	public String getName() {
		return this.TeamName;
	}

	@Override
	public DyeColor getColor() {
		return this.teamColor;
	}

	@Override
	public List<Location> getSpawnLocations() {
		return this.spawnLocations;
	}

	@Override
	public void setSpawnLocations(List<Location> locations) {
		this.spawnLocations = locations;
	}

	/**
	 * This method handles a team death event by dropping all flags they may carry and deleting their inventory
	 */
	@Override
	public void handleTeamPlayerDeath(PlayerDeathEvent e) {
		if (inTeam( CTFPlugin.getTeamPlayer(e.getEntity())  )) {
			for (ItemStack i : e.getEntity().getInventory()) {
				if (i.equals(Material.WOOL)) {
					//DROP ANY FUCKING WOOL
					e.getEntity().getWorld().dropItem(e.getEntity().getLocation(), i);
				}
			}
			//Remove inventory
			e.getEntity().getInventory().clear();
		}
	}
	
	/**
	 * This method chooses a random location from a list of locations
	 * @param locations The list of locations
	 * @return 
	 */
	private Location randomLocation(List<Location> locations) {
		int size = locations.size();
		Random r = new Random();
		if (size == 0){
			return null;
		}
		return locations.get(r.nextInt(size));
	}
	
	private ItemStack[] generateArmor() {
		ItemStack[] Armor = new ItemStack[] {};
		LeatherArmorMeta dyer;
		Armor[0] = new ItemStack(Material.LEATHER_HELMET);
		dyer = ((LeatherArmorMeta) Armor[0].getItemMeta());
		dyer.setColor(this.teamColor.getColor());
		Armor[0].setItemMeta(dyer);
		
		Armor[1] = new ItemStack(Material.LEATHER_CHESTPLATE);
		dyer = ((LeatherArmorMeta) Armor[1].getItemMeta());
		dyer.setColor(this.teamColor.getColor());
		Armor[1].setItemMeta(dyer);
		
		Armor[2] = new ItemStack(Material.LEATHER_LEGGINGS);
		dyer = ((LeatherArmorMeta) Armor[2].getItemMeta());
		dyer.setColor(this.teamColor.getColor());
		Armor[2].setItemMeta(dyer);
		
		Armor[3] = new ItemStack(Material.LEATHER_BOOTS);
		dyer = ((LeatherArmorMeta) Armor[3].getItemMeta());
		dyer.setColor(this.teamColor.getColor());
		Armor[3].setItemMeta(dyer);
		return Armor;
	}

	/**
	 * This method handles a respawn event by randomly choosing a respawn location in the team base
	 * @param e The player respawn event
	 */
	@Override
	public void handleTeamPlayerRespawn(PlayerRespawnEvent e) {
		if(inTeam(   CTFPlugin.getTeamPlayer(e.getPlayer())  )) {
			e.setRespawnLocation(randomLocation(this.spawnLocations));
		}
		e.getPlayer().getInventory().setArmorContents(generateArmor());
		e.getPlayer().getInventory().setItemInHand(new ItemStack(Material.STONE_SWORD));
	}
	
	@Override
	public List<Location> getFlagLocations() {
		return this.flagLocations;
	}

	@Override
	public void setFlagLocations(List<Location> locations) {
		this.flagLocations = locations;
	}

	@Override
	public void resetFlag() {
		Location flagRespawn = randomLocation(this.flagLocations);
		Wool w = new Wool();
		w.setColor(this.teamColor);
		if ((flagRespawn) != null){
			System.out.println("Setting le flag for team " + this.getName() + " with color " + this.teamColor);
			flagRespawn.getBlock().setType(w.getItemType());
			flagRespawn.getBlock().setData(w.getData());
		}
		
	}
	
	@Override
	public void setColor(DyeColor color) {
		this.teamColor = color;
	}
	
	@Override
	public List<TeamPlayer> getTeamPlayers() {
		return this.teamPlayers;
	}
}
