package com.SkyIsland.CTF;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.SkyIsland.CTF.Team.CTFTeam;
import com.SkyIsland.CTF.Team.TeamPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class CTFPlugin extends JavaPlugin implements Listener {
	
	public static CTFPlugin plugin;
	public static WorldEditPlugin weplugin;
	private List<CTFSession> sessions;
	
	private Map<UUID, TeamPlayer> playerMap;
	
	public void onLoad() {
		
	}
	
	public void onEnable() {
		sessions = new LinkedList<CTFSession>();
		playerMap = new HashMap<UUID, TeamPlayer>();
		CTFPlugin.plugin = this;
		CTFPlugin.weplugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
		Bukkit.getPluginManager().registerEvents(this, this);
	}
	
	public void onDisable() {
		sessions.clear();
		sessions = null;
		playerMap.clear();
		playerMap = null;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		
		String command = cmd.getName();
		
		if (command.equalsIgnoreCase("lteams")) {
			//First, are there any sessions or teams?
			if (sessions.size() == 0) {
				sender.sendMessage("There are currently no sessions.");
				return true;
			}
			
			//list the teams available to join. If they don't specify a session, list all sessions and teams
			if (args.length > 0) {
				//they passes a session?
				String sessionName = args[0];
				CTFSession session = null;
				for (CTFSession s : sessions) {
					if (s.getName() == sessionName) {
						session = s;
						break;
					}
				}
				if (session != null) {
					String msg = "Teams: ";
					for (CTFTeam t : session.getTeams()) {
						msg += t.getName() + " | ";
					}
					sender.sendMessage(msg);
					return true;
				}
				else {
					//no session by that name.
					String msg = "";
					for (CTFSession s: sessions) {
						msg += s.getName() + " | ";
					}
					sender.sendMessage("No valid session exists with that name. Valid sessions: " + msg);
					return true;
				}
			}
			//args == 0
			//no session passed. Just list all teams with their respective session names
			String msg;
			for (CTFSession s: sessions) {
				msg = s.getName() + ":   ";
				for (CTFTeam t: s.getTeams()) {
					msg += t.getName() + " | ";
				}
				sender.sendMessage(msg);
			}
			return true;
		}
		
		if (command.equalsIgnoreCase("leave")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("Only players can execute this command!");
				return true;
			}
			TeamPlayer tp = getTeamPlayer((Player) sender);
			if (tp == null) {
				//didn't have a teamPlayer...?
				sender.sendMessage("<REGERR> You have left your team!");
				hashPlayer((Player) sender);
				return true;
			}
			
			CTFTeam team = tp.getTeam();
			
			if (team == null) {
				sender.sendMessage("You're not in a team!");
				return true;
			}
			
			team.removePlayer((Player) sender);
			tp.setTeam(null);
			
		}
		
		
		return false;
	}
	
	/**
	 * Returns the {@link com.SkyIsland.CTF.Team.TeamPlayer TeamPlayer} corresponding to the passed
	 * {@link org.bukkit.entity.Player Player},
	 * or null if none exist.
	 * @param player The player to try and get the TeamPlayer for
	 * @return
	 */
	public TeamPlayer getTeamPlayer(Player player) {
		UUID id = player.getUniqueId();
		return playerMap.get(id);
	}

	/**
	 * Returns the {@link com.SkyIsland.CTF.Team.TeamPlayer TeamPlayer} corresponding to the passed
	 * {@link org.bukkit.entity.Player Player},
	 * or null if none exist.
	 * @param pID Unique User ID to try and find the TeamPlayer for
	 * @return
	 */
	public TeamPlayer getTeamPlayer(UUID pID) {
		return playerMap.get(pID);
	}
	
	/**
	 * Adds the passed player to the map between players and TeamPlayers if they don't already have an entity
	 * @param player
	 */
	private void hashPlayer(Player player) {
		if (playerMap.containsKey(player.getUniqueId())) {
			return;
		}
		playerMap.put(player.getUniqueId(), new TeamPlayer(player));
	}
	
	/**
	 * Called when a player joins the world. This created a TeamPlayer for them and updates the hashmap.
	 * @param event
	 */
	@EventHandler
	public void playerJoin(PlayerJoinEvent event) {
		hashPlayer(event.getPlayer());
	}
}
