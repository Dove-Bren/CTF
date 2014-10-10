package com.SkyIsland.CTF;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.SkyIsland.CTF.NoEditGame.NoEditSession;
import com.SkyIsland.CTF.Team.CTFTeam;
import com.SkyIsland.CTF.Team.TeamPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class CTFPlugin extends JavaPlugin implements Listener {
	
	public static CTFPlugin plugin;
	public static WorldEditPlugin weplugin;
	private List<CTFSession> sessions;
	
	private static Map<UUID, TeamPlayer> playerMap = new HashMap<UUID, TeamPlayer>();
	
	public void onLoad() {
		
	}
	
	public void onEnable() {
		sessions = new LinkedList<CTFSession>();
		CTFPlugin.plugin = this;
		CTFPlugin.weplugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
		Bukkit.getPluginManager().registerEvents(this, this);
		
		CTFTypes.registerType(NoEditSession.class);
		
	}
	
	public void onDisable() {
		sessions.clear();
		sessions = null;
		playerMap.clear();
		playerMap = null;
		CTFTypes.clear();
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
			return true;
		}
		
		if (command.equalsIgnoreCase("join")) {
			if (args.length == 1) {
				//specified only the team to join. join hte first
				for (CTFSession s: sessions) {
					if (s.hasTeam(args[0])) {
						CTFTeam team = s.getTeam(args[0]);
						team.addPlayer((Player) sender);
						getTeamPlayer((Player) sender).setTeam(team);
						sender.sendMessage("You have joined the team [" + team.getName() + "] in the session [" + s.getName() + "]!");
						return true;
					}
				}
				//never returned so never found that team
				sender.sendMessage("Could not find a team with the name " + args[0] + "! Try using /lteams to see all available teams");
				return true;
			}
			else if (args.length == 2) {
				//arg 1 is session, arg 2 is team 
				CTFSession session = null;
				CTFTeam team = null;
				//first try and get the session.
				for (CTFSession s : sessions) {
					if (s.getName().equalsIgnoreCase(args[0])) {
						//found the sesson
						session = s;
						break;
					}
				}
				
				//make sure we found the sesson
				if (session == null) {
					sender.sendMessage("Could not find the session " + args[0] + "!");
					return false;
				}
				
				//we found the session, so try and find the team
				team = session.getTeam(args[1]);
				if (team == null) {
					sender.sendMessage("Could not find the team " + args[1] + "!");
					return false;
				}
				
				//found both the session and team
				team.addPlayer((Player) sender);
				getTeamPlayer((Player) sender).setTeam(team);
				sender.sendMessage("You have joined the team [" + team.getName() + "] in the session [" + session.getName() + "]!");

				return true;
			}
			else {
				//invalid argument cound
				return false;
			}
		}
		
		if (command.equalsIgnoreCase("capturetheflag") || command.equalsIgnoreCase("ctf")) {
			//admin command. commands are: session, team
			if (args.length == 0) {
				return false; //no just ctf command
			}
			
			if (args[0].equalsIgnoreCase("session")) {
				//what to do with session. We cant create one, delete one, start one, or end one
				if (args.length == 1) {
					return false; //no /ctf session command
				}
				if (args[1].equalsIgnoreCase("create")) {
					//going to create one. Need to pass a type
					if (args.length < 4) {
						return false; //need at least 4: session, create, type, name
					}
					Class<? extends CTFSession> sesClass = CTFTypes.getSession(args[2]);
					if (sesClass == null) {
						//couldn't find that kind of session
						sender.sendMessage("Invalid session type!");
						return false;
					}
					
					CTFSession session = null;
					try {
						session = sesClass.getConstructor(String.class).newInstance(args[3]);
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if (session == null) {
						sender.sendMessage("Unable to instantiate the session...");
						return false;
					}
					
					sessions.add(session);
					sender.sendMessage("Session <" + session.getName() + "> created!");
					return true;
					
				}
				else if (args[1].equalsIgnoreCase("start")) {
					if (args.length != 3) {
						return false; //has to be /ctf session start <session>
					}
					CTFSession session = null;
					for (CTFSession s : sessions) {
						if (s.getName().equalsIgnoreCase(args[1])) {
							session = s;
							break;
						}
					}
					if (session == null) {
						//diddn't find a session with that name
						sender.sendMessage("Session with the name " + args[1] + " wasn't found!");
						return true;
					}
					session.start();
					return true;
				}
				else if (args[1].equalsIgnoreCase("stop")) {
					if (args.length != 3) {
						return false; //has to be /ctf session stop <session>
					}
					CTFSession session = null;
					for (CTFSession s : sessions) {
						if (s.getName().equalsIgnoreCase(args[1])) {
							session = s;
							break;
						}
					}
					if (session == null) {
						//diddn't find a session with that name
						sender.sendMessage("Session with the name " + args[1] + " wasn't found!");
						return true;
					}
					session.stop();
					return true;
				}
				else if (args[1].equalsIgnoreCase("remove")) {
					if (args.length != 3) {
						return false; //has to be ctf session remove <session>
					}
					CTFSession session = null;
					for (CTFSession s : sessions) {
						if (s.getName().equalsIgnoreCase(args[1])) {
							session = s;
							break;
						}
					}
					if (session == null) {
						//diddn't find a session with that name
						sender.sendMessage("Session with the name " + args[1] + " wasn't found!");
						return true;
					}

					if (session.isRunning()) {
						session.stop();
					}
					sessions.remove(session);
					
					return true;
				}
				
			}
			else if (args[0].equalsIgnoreCase("team")) {
				//we can create teams or remove teams
				if (args.length == 1) {
					return false; //no /ctf team   command
				}
				if (args[1].equalsIgnoreCase("create")) {
					//it'll be /ctf team create [session] [name] [color]
					if (args.length != 5) {
						//we don't have the right number of arguments
						return false;
					}
					
					//find the team specified
					CTFSession session = null;
					for (CTFSession s : sessions) {
						if (s.getName().equalsIgnoreCase(args[2])) {
							session = s;
							break;
						}
					}
					if (session == null) {
						sender.sendMessage("Failed to find a session by the name " + args[2]);
						return true;
					}
					
					//make sure a team with that name doesn't exist
					if (session.hasTeam(args[3])) {
						sender.sendMessage("A team with that name in this session already exists!");
						return true;
					}
					
					//try and get that color					
					DyeColor color = null;
					try {
						color = DyeColor.valueOf(args[4].toUpperCase());
					}
					catch (IllegalArgumentException e) {
						sender.sendMessage("Could not convert " + args[4].toUpperCase() + " to a dye color!");
						return true;
					}
					
					CTFTeam team = session.createTeam(args[3]);
					team.setColor(color);
					sender.sendMessage("Team named [" + team.getName() + "] created on session [" + session.getName() + "]!");
					return true;
				}
					
			}
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
	public static TeamPlayer getTeamPlayer(Player player) {
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
	public static TeamPlayer getTeamPlayer(UUID pID) {
		return playerMap.get(pID);
	}
	
	/**
	 * Adds the passed player to the map between players and TeamPlayers if they don't already have an entity
	 * @param player
	 */
	private static void hashPlayer(Player player) {
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
		CTFPlugin.hashPlayer(event.getPlayer());
	}
}
