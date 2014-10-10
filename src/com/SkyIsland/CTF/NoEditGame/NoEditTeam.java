package com.SkyIsland.CTF.NoEditGame;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;




import com.SkyIsland.CTF.Team.CTFTeam;
import com.SkyIsland.CTF.Team.Goal;
import com.SkyIsland.CTF.Team.TeamPlayer;

public class NoEditTeam implements CTFTeam {

	private String TeamName;
	private List<Player> Players;
	private int score;
	private Goal TeamGoal;
	private Location SpawnLocation;
	
	//Constructors
	
	/**
	 * This constructor allows for specification of the Team name
	 * @param TeamName
	 */
	public NoEditTeam(String TeamName) {
		setTeamName(TeamName);
		Players = new LinkedList<Player>();
		this.score = 0;
		setgoal(null);
		setSpawn(null);
	}
	
	/**
	 * This is the main constructor, all fields are specified
	 * @param Players The list of players to be in the team
	 * @param score The current score of the team
	 * @param TeamGoal The Goal Region of the team
	 * @param SpawnLocation The spawn location of the team players
	 */
	public NoEditTeam(String TeamName, List<Player> Players, int score, Goal TeamGoal, Location SpawnLocation) {
		setTeamName(TeamName);
		setPlayers(Players);
		this.score = 0;
		setgoal(TeamGoal);
		setSpawn(SpawnLocation);
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
	public void setPlayers(List<Player> list) {
		this.Players = list;
	}

	@Override
	public List<Player> getPlayers() {
		return this.Players;
	}

	@Override
	public void addPlayer(Player player) {
		this.Players.add(player);
	}

	@Override
	public void removePlayer(Player player) {
		this.Players.remove(player);
	}

	@Override
	public int getScore() {
		return score;
	}

	@Override
	public void addToScore(int increment) {
		this.score += increment;
	}

	@Override
	public void subToScore(int decrement) {
		this.score -= decrement;
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
	public void setSpawn(Location spawnLocation) {
		this.SpawnLocation = spawnLocation;
	}

	@Override
	public Location getSpawn() {
		return this.SpawnLocation;
	}

	@Override
	public boolean inTeam(Player player) {
		return this.Players.contains(player);
	}
	
	/**
	 * @TODO IMPLEMENT METHOD
	 */
	@Override
	public TeamPlayer getTeamPlayer(Player player) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return this.TeamName;
	}
	
}
