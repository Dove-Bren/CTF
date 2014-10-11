package com.SkyIsland.CTF.PigGame;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;
import org.bukkit.scoreboard.Score;

import com.SkyIsland.CTF.CTFPlugin;
import com.SkyIsland.CTF.Team.CTFTeam;
import com.SkyIsland.CTF.Team.Goal;
import com.SkyIsland.CTF.Team.TeamPlayer;

public class PigTeam implements CTFTeam {

	private String TeamName;
	private List<TeamPlayer> teamPlayers;
	private List<Location> spawnLocations;
	private List<Location> flagLocations;
	private int score;
	private Goal TeamGoal;
	private DyeColor teamColor;
	private Score teamScore;
	private static Random rand = new Random();
	public PigSession session;
	private Inventory inventory;
	//Constructors
	
	/**
	 * This constructor allows for specification of the Team name
	 * @param TeamName
	 */
	public PigTeam(PigSession session, String TeamName, Score score) {
		this.session = session;
		setTeamName(TeamName);
		teamScore = score;
		teamPlayers = new LinkedList<TeamPlayer>();
		this.score = 0;
		setgoal(null);
		setSpawnLocations(new LinkedList<Location>());
		setFlagLocations(new LinkedList<Location>());
		Bukkit.getPluginManager().registerEvents(this, CTFPlugin.plugin);
	}
	
	/**
	 * This is the main constructor, all fields are specified
	 * @param Players The list of players to be in the team
	 * @param score The current score of the team
	 * @param TeamGoal The Goal Region of the team
	 * @param SpawnLocation The spawn location of the team players
	 */
	public PigTeam(PigSession session, String TeamName, Score teamScore, List<TeamPlayer> Players, int score, Goal TeamGoal, List<Location> spawnLocations, List<Location> flagLocations) {
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
	@EventHandler
	public void handleTeamPlayerDeath(PlayerDeathEvent e) {
		if (inTeam( CTFPlugin.getTeamPlayer(e.getEntity())  )) {
			
			System.out.println("Played died");
			
			//set the respawn point
			e.getEntity().setBedSpawnLocation(spawnLocations.get(rand.nextInt(spawnLocations.size())));
			
			
			for (Iterator<ItemStack> i = e.getDrops().iterator(); i.hasNext();) {
				ItemStack item = i.next();
				
				if (item != null){
					if (item.getType().equals(Material.WOOL)) {
						//DROP ANY FUCKING WOOL
						System.out.println("PLAYER WAS HOLDING WOOL");
					}
					else{
						i.remove();
					}
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

	/**
	 * This method handles a respawn event by randomly choosing a respawn location in the team base
	 * @param e The player respawn event
	 */
	@Override
	@EventHandler
	public void handleTeamPlayerRespawn(PlayerRespawnEvent e) {
		
		if(!inTeam(CTFPlugin.getTeamPlayer(e.getPlayer()))) {
			return;
		}
		
		System.out.println("Played respawned");
		
		e.setRespawnLocation(randomLocation(this.spawnLocations));
				
		//Reset inventory
		TeamPlayer tp = CTFPlugin.getTeamPlayer(e.getPlayer());
		CTFTeam team = tp.getTeam();
		if (team.getInventory() != null){
			for (ItemStack i: team.getInventory()){
				if (i != null){
					switch(i.getType()){
					case LEATHER_BOOTS:
					case IRON_BOOTS:
					case GOLD_BOOTS:
					case DIAMOND_BOOTS:
						tp.getPlayer().getInventory().setBoots(i.clone());
						break;
					case LEATHER_HELMET:
					case IRON_HELMET:
					case GOLD_HELMET:
					case DIAMOND_HELMET:
						tp.getPlayer().getInventory().setHelmet(i.clone());
						break;
					case LEATHER_CHESTPLATE:
					case IRON_CHESTPLATE:
					case GOLD_CHESTPLATE:
					case DIAMOND_CHESTPLATE:
						tp.getPlayer().getInventory().setChestplate(i.clone());
						break;
					case LEATHER_LEGGINGS:
					case IRON_LEGGINGS:
					case GOLD_LEGGINGS:
					case DIAMOND_LEGGINGS:
						tp.getPlayer().getInventory().setLeggings(i.clone());
						break;
					case WOOD_SWORD:
					case IRON_SWORD:
					case GOLD_SWORD:
					case DIAMOND_SWORD:
						tp.getPlayer().getInventory().setItemInHand(i.clone());
						break;
					default:
						tp.getPlayer().getInventory().addItem(i.clone());
					}
				}
			}
		}
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

	@Override
	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	@Override
	public Inventory getInventory() {
		return this.inventory;
	}
}
