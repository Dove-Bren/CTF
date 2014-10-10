package com.SkyIsland.CTF;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.SkyIsland.CTF.Team.CTFTeam;
import com.SkyIsland.CTF.Team.TeamPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class CTFPlugin extends JavaPlugin {
	
	public static CTFPlugin plugin;
	public static WorldEditPlugin weplugin;
	private List<CTFSession> sessions;
	
	private Map<Player, TeamPlayer> playerMap;
	
	public void onLoad() {
		
	}
	
	public void onEnable() {
		sessions = new LinkedList<CTFSession>();
		playerMap = new HashMap<Player, TeamPlayer>();
		CTFPlugin.plugin = this;
		CTFPlugin.weplugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
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
			
		}
		
		
		return false;
	}
}
