package com.SkyIsland.CTF.NoEditGame;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.SkyIsland.CTF.CTFPlugin;
import com.SkyIsland.CTF.CTFSession;
import com.SkyIsland.CTF.Team.CTFTeam;

public class NoEditSession implements CTFSession, Listener {

	private List<CTFTeam> Teams;
	private boolean running;
	
	/**
	 * Default constructor for a No Edit Game Session
	 */
	public NoEditSession() {
		this.Teams = new LinkedList<CTFTeam>();
		Bukkit.getPluginManager().registerEvents(this, CTFPlugin.plugin);
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
	 * This method generates a new empty Team within the Game Session with the specified name
	 * @param name The Name to be attributed to the team
	 */
	@Override
	public void createTeam(String name) {
		this.Teams.add(new NoEditTeam(name));
	}

	/**
	 * This method generates a new Team with the players and name specified
	 * @param name The Team Name
	 * @param players The list of players to be associated with the team
	 */
	@Override
	public void createTeam(String name, List<Player> players) {
		NoEditTeam team;
		team = new NoEditTeam(name);
		this.Teams.add(team);
		team.setPlayers(players);
	}

	@Override
	public void removeTeam(CTFTeam team) {
		if (Teams.contains(team)) {
			Teams.remove(team);
		}
	}

	@Override
	public void addPlayer(CTFTeam team, Player player) {
		if (Teams.contains(team)) //only add if this session has that team???
			team.addPlayer(player);
	}

	@Override
	public void removePlayer(CTFTeam team, Player player) {
		if (Teams.contains(team)) {
			team.removePlayer(player);
		}
	}

	@Override
	public boolean isRunning() {
		return this.running;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}
	
	
	@EventHandler
	public void punchWool(PlayerInteractEvent event) {
		if (event.getClickedBlock().getType() == Material.WOOL) {
			event.getClickedBlock().breakNaturally();
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void playerMove(PlayerMoveEvent event) {
		NoEditTeam team = null;
		for (CTFTeam t : Teams) {
			if (t.inTeam(event.getPlayer())) {
				team = (NoEditTeam) t;
				break;
			}
		}
		
		if (team != null && team.getGoal().isInGoal(event.getTo())) {
			//goal
		}
		
		
	}
	

}
