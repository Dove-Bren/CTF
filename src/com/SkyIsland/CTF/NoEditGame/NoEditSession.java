package com.SkyIsland.CTF.NoEditGame;

import java.util.List;

import org.bukkit.entity.Player;

import com.SkyIsland.CTF.CTFSession;
import com.SkyIsland.CTF.Team.CTFTeam;

public class NoEditSession implements CTFSession {

	private List<CTFTeam> Teams;
	
	/**
	 * Default constructor for a No Edit Game Session
	 */
	public NoEditSession() {
		this.Teams = null;
	}
	
	/**
	 * This constructor allows for specification of Teams
	 * @param Teams The Teams to be added to the session
	 */
	public NoEditSession(List<CTFTeam> Teams) {
		this.Teams = Teams;
	}
	
	/**
	 * This returns a list of Teams within the Session
	 */
	@Override
	public List<CTFTeam> getTeams() {
		return this.Teams;
	}
	
	/**
	 * This method generates a new Team within the Game Session with the specified name
	 * @param name The Name to be attributed to the team
	 */
	@Override
	public void createTeam(String name) {
		
	}

	@Override
	public void createTeam(String name, List<Player> players) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeTeam(CTFTeam team) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addPlayer(CTFTeam team, Player player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removePlayer(CTFTeam team, Player player) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isRunning() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

}
