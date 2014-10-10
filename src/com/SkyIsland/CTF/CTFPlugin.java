package com.SkyIsland.CTF;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.SkyIsland.CTF.NoEditGame.NoEditGoal;
import com.SkyIsland.CTF.NoEditGame.NoEditSession;
import com.SkyIsland.CTF.Team.CTFTeam;
import com.SkyIsland.CTF.Team.TeamPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;

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
		
		//make sure to get all players currently in on reload in our map
		for (World w : Bukkit.getWorlds()) {
			if (!w.getPlayers().isEmpty()) {
				for (Player p : w.getPlayers()) {
					CTFPlugin.hashPlayer(p);
				}
			}
		}
		
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
			return onLteamsCommand(sender, cmd, label, args);
		}
		else if (command.equalsIgnoreCase("leave")) {
			return onLeaveCommand(sender, cmd, label, args);
		}
		else if (command.equalsIgnoreCase("join")) {
			return onJoinCommand(sender, cmd, label, args);
		}
		else if (command.equalsIgnoreCase("capturetheflag") || command.equalsIgnoreCase("cf") || command.equalsIgnoreCase("ctf")) {
			return onCTFCommand(sender, cmd, label, args);
		}
		return false;
	}
	
	public boolean onLteamsCommand(CommandSender sender, Command cmd, String label, String[] args){
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
	
	public boolean onLeaveCommand(CommandSender sender, Command cmd, String label, String[] args){
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
		
		team.removePlayer(tp);
		tp.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		sender.sendMessage("You have left your team.");
		if (team.getGoal() != null && team.getGoal().isAccepting()) {
			//session still going on. Teleport them out
			tp.moveLeave(); //to 0,0,0 for now :( 
			//TODO SUPER IMPORTANT
		}
		return true;
	}
	public boolean onJoinCommand(CommandSender sender, Command cmd, String label, String[] args){
		if (args.length == 1) {
			//specified only the team to join. join hte first
			for (CTFSession s: sessions) {
				if (s.hasTeam(args[0])) {
					CTFTeam team = s.getTeam(args[0]);
					s.addPlayer(team, getTeamPlayer((Player) sender));
					
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
				return true;
			}
			
			//we found the session, so try and find the team
			team = session.getTeam(args[1]);
			if (team == null) {
				sender.sendMessage("Could not find the team " + args[1] + "!");
				return true;
			}
			
			//found both the session and team
			team.addPlayer(getTeamPlayer((Player) sender));
			sender.sendMessage("You have joined the team [" + team.getName() + "] in the session [" + session.getName() + "]!");

			return true;
		}
		else {
			//invalid argument cound
			return false;
		}
	}
	
	public boolean onCTFCommand(CommandSender sender, Command cmd, String label, String[] args){
		
		//admin command. commands are: session, team
		if (args.length == 0) {
			return false; //no just ctf command
		}
		
		if (args[0].equalsIgnoreCase("session")) {
			return onCTFSessionCommand(sender, cmd, label, args);
		}
		else if (args[0].equalsIgnoreCase("team")) {
			return onCTFTeamCommand(sender, cmd, label, args);
		}
		
		return false;
	}
	
	public boolean onCTFSessionCommand(CommandSender sender, Command cmd, String label, String[] args){
		//what to do with session. We cant create one, delete one, start one, or end one
		if (args.length == 1) {
			sender.sendMessage("/cf session [create/remove/start/stop]");
			return true; //no /ctf session command
		}
		if (args[1].equalsIgnoreCase("create")) {
			//going to create one. Need to pass a type
			if (args.length < 4) {
				sender.sendMessage("/cf session create [type] [name]");
				return true; //need at least 4: session, create, type, name
			}
			Class<? extends CTFSession> sesClass = CTFTypes.getSession(args[2]);
			if (sesClass == null) {
				//couldn't find that kind of session
				sender.sendMessage("Invalid session type!");
				return true;
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
				return true;
			}
			
			sessions.add(session);
			sender.sendMessage("Session <" + session.getName() + "> created!");
			return true;
			
		}
		else if (args[1].equalsIgnoreCase("start")) {
			if (args.length != 3) {
				sender.sendMessage("/cf session start [session name]");
				return true; //has to be /ctf session start <session>
			}
			CTFSession session = null;
			for (CTFSession s : sessions) {
				if (s.getName().equalsIgnoreCase(args[2])) {
					session = s;
					break;
				}
			}
			if (session == null) {
				//diddn't find a session with that name
				sender.sendMessage("Session with the name " + args[2] + " wasn't found!");
				return true;
			}
			session.start();
			return true;
		}
		else if (args[1].equalsIgnoreCase("stop")) {
			if (args.length != 3) {
				sender.sendMessage("/cf session stop [session name]");
				return true; //has to be /ctf session stop <session>
			}
			CTFSession session = null;
			for (CTFSession s : sessions) {
				if (s.getName().equalsIgnoreCase(args[2])) {
					session = s;
					break;
				}
			}
			if (session == null) {
				//diddn't find a session with that name
				sender.sendMessage("Session with the name " + args[2] + " wasn't found!");
				return true;
			}
			session.stop();
			return true;
		}
		else if (args[1].equalsIgnoreCase("remove")) {
			if (args.length != 3) {
				sender.sendMessage("/cf session remove [session name]");
				return true; //has to be ctf session remove <session>
			}
			CTFSession session = null;
			for (CTFSession s : sessions) {
				if (s.getName().equalsIgnoreCase(args[2])) {
					session = s;
					break;
				}
			}
			if (session == null) {
				//diddn't find a session with that name
				sender.sendMessage("Session with the name " + args[2] + " wasn't found!");
				return true;
			}

			if (session.isRunning()) {
				session.stop();
			}
			//remove all teams
			if (!session.getTeams().isEmpty()) 
			for (CTFTeam t : session.getTeams()) {
				for (TeamPlayer tp : t.getTeamPlayers()) {
					tp.setTeam(null);
					tp.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
				}
			}
			sessions.remove(session);
			sender.sendMessage("Session " + session.getName() + " removed!");
			return true;
		}
		return false;
		
	}
	
	public boolean onCTFTeamCommand(CommandSender sender, Command cmd, String label, String[] args){
		//we can create teams or remove teams
		if (args.length == 1) {
			sender.sendMessage("/cf team [create/remove/spawn/flag/goal/chest]");
			return true; //no /ctf team   command
		}
		
		if (args[1].equalsIgnoreCase("create")) {
			//it'll be /ctf team create [session] [name] [color]
			if (args.length != 5) {
				//we don't have the right number of arguments
				sender.sendMessage("/cf team create [session name] [team name] [team color]");
				return true; 
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
				System.out.println(color);
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
		
		else if (args[1].equalsIgnoreCase("remove")) {
			//it'll be /ctf team remove session team
			if (args.length != 4) {
				sender.sendMessage("/cf team remove [session name] [team name]");
				return true; 
			}
			CTFSession session;
			CTFTeam team;
			session = null;
			for (CTFSession s : sessions) {
				if (s.getName().equalsIgnoreCase(args[2])) {
					session = s;
					break;
				}
			}
			if (session == null) {
				sender.sendMessage("Could not find a session by the name " + args[2]);
				return true;
			}
			
			team = session.getTeam(args[3]);
			if (team == null) {
				sender.sendMessage("Could not find a team by the name " + args[3]);
			}
			
			//got hte session and team
			//reset all players in that team
			for (TeamPlayer tp : team.getTeamPlayers()) {
				tp.setTeam(null);
				tp.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
			}
			session.removeTeam(team);
			sender.sendMessage("Removed the team [" + team.getName() + "] from session [" + session.getName() + "]!");
			return true;
		}
		else if (args[1].equalsIgnoreCase("spawn")) {
			//set a player spawn point for this time
			//can be /ctf team spawn add [session] [team],   ---
			if (args.length != 5 && args.length != 9 && args.length != 12) {
				sender.sendMessage("/cf team spawn [\"add\"] [session name] [team name]");
				return true; 
			}
			
			if (args[2].equalsIgnoreCase("add")) {
				//get session
				CTFSession session = null;
				CTFTeam team = null;
				for (CTFSession s : sessions) {
					if (s.getName().equalsIgnoreCase(args[3])) {
						session = s;
						break;
					}
				}
				
				if (session == null) {
					sender.sendMessage("Unable to find session named " + args[3] + "!");
					return true;
				}
				
				//get the team
				team = session.getTeam(args[4]);
				
				if (team == null) {
					//never found that team
					sender.sendMessage("Could not find a team by the name " + args[4]);
					return true;
				}
				
				Location max = null, min = null;
				
				if (args.length == 5){
					if (!(sender instanceof Player)) {
						sender.sendMessage("Only players are able to use this command!");
						return true;
					}
					Selection selection = CTFPlugin.weplugin.getSelection((Player) sender);
					if (selection == null || selection.getArea() == 0) {
						//player has nothing selected
						sender.sendMessage("You must select 1 more more blocks to set as a spawn point!");
						return true;
					}
					max = selection.getMaximumPoint();
					min = selection.getMinimumPoint();

				}
				else if (args.length == 9){
					World world = Bukkit.getWorld(args[5]);
					Integer x = Integer.parseInt(args[6]);
					Integer y = Integer.parseInt(args[7]);
					Integer z = Integer.parseInt(args[8]);
					max = min = new Location(world, x, y, z);
				}
				else if (args.length == 12){
					World world = Bukkit.getWorld(args[5]);
					Integer x = Integer.parseInt(args[6]);
					Integer y = Integer.parseInt(args[7]);
					Integer z = Integer.parseInt(args[8]);
					Integer x2 = Integer.parseInt(args[9]);
					Integer y2 = Integer.parseInt(args[10]);
					Integer z2 = Integer.parseInt(args[11]);
					min = new Location(world, Math.min(x, x2), Math.min(y, y2), Math.min(z, z2));
					max = new Location(world, Math.max(x, x2), Math.max(y, y2), Math.max(z, z2));					
				}	
				
				World world = max.getWorld();
				for (int i = min.getBlockX(); i <= max.getBlockX(); i++) {
				for (int j = min.getBlockY(); j <= max.getBlockY(); j++) {
				for (int k = min.getBlockZ(); k <= max.getBlockZ(); k++) {
					team.getSpawnLocations().add(new Location(world, i,j,k));
				}}}
				sender.sendMessage("Set all blocks as a spawn point");
				return true;
			}
		}
		else if (args[1].equalsIgnoreCase("flag")) {
			//it'll be /ctf team flag add [session] [team]
			if (args.length != 5 && args.length != 9 && args.length != 12) {
				sender.sendMessage("/cf team flag [\"add\"] [session name] [team name]");
				return true; 
			}
			
			if (args[2].equalsIgnoreCase("add")) {
			
				CTFSession session = null;
				CTFTeam team;
				
				for (CTFSession s : sessions) {
					if (s.getName().equalsIgnoreCase(args[3])) {
						session = s;
						break;
					}
				}
				
				if (session == null) {
					sender.sendMessage("Unable to find session with the name " + args[3]);
					return true;
				}
				
				//now get the team
				team = session.getTeam(args[4]);
				
				if (team == null) {
					sender.sendMessage("Unable to find team with the name " + args[4]);
					return true;
				}
				
				Location min = null, max = null;
				
				if (args.length == 5){
					//this also uses selection, which is only made by a player
					if (!(sender instanceof Player)) {
						sender.sendMessage("Only players are able to use this command!");
						return true;
					}
					
					//team and session are both good. 
					Selection selection = CTFPlugin.weplugin.getSelection((Player) sender);
					if (selection == null || selection.getArea() == 0) {
						sender.sendMessage("You must select an area to set as a flag spawning location!");
						return true;
					}
					
					max = selection.getMaximumPoint();
					min = selection.getMinimumPoint();
				}
				
				else if (args.length == 9){
					World world = Bukkit.getWorld(args[5]);
					Integer x = Integer.parseInt(args[6]);
					Integer y = Integer.parseInt(args[7]);
					Integer z = Integer.parseInt(args[8]);
					max = min = new Location(world, x, y, z);
				}
				else if (args.length == 12){
					World world = Bukkit.getWorld(args[5]);
					Integer x = Integer.parseInt(args[6]);
					Integer y = Integer.parseInt(args[7]);
					Integer z = Integer.parseInt(args[8]);
					Integer x2 = Integer.parseInt(args[9]);
					Integer y2 = Integer.parseInt(args[10]);
					Integer z2 = Integer.parseInt(args[11]);
					min = new Location(world, Math.min(x, x2), Math.min(y, y2), Math.min(z, z2));
					max = new Location(world, Math.max(x, x2), Math.max(y, y2), Math.max(z, z2));					
				}
				
				World world = max.getWorld();
				for (int i = min.getBlockX(); i <= max.getBlockX(); i++)
				for (int j = min.getBlockY(); j <= max.getBlockY(); j++)
				for (int k = min.getBlockZ(); k <= max.getBlockZ(); k++) {
					team.getFlagLocations().add(new Location(world, i,j,k));
				}
				sender.sendMessage("Added all blocks selected as flag locations.");
				return true;						
			}
			
		}
		else if (args[1].equalsIgnoreCase("goal")) {
			//it'll be /ctf team flag add [session] [team]
			if (args.length != 4 && args.length != 8 && args.length != 11) {
				sender.sendMessage("/cf team goal [session name] [team name]");
				return true; 
			}
			
			
			CTFSession session = null;
			CTFTeam team;
				
			for (CTFSession s : sessions) {
				if (s.getName().equalsIgnoreCase(args[2])) {
					session = s;
					break;
				}
			}
				
			if (session == null) {
				sender.sendMessage("Unable to find session with the name " + args[2]);
				return true;
			}
				
			//now get the team
			team = session.getTeam(args[3]);
				
			if (team == null) {
				sender.sendMessage("Unable to find team with the name " + args[3]);
				return true;
			}
			
			Location min = null, max = null;
			
			if (args.length == 4){
				//this also uses selection, which is only made by a player
				if (!(sender instanceof Player)) {
					sender.sendMessage("Only players are able to use this command!");
					return true;
				}
				
				//team and session are both good. 
				Selection selection = CTFPlugin.weplugin.getSelection((Player) sender);
				if (selection == null || selection.getArea() == 0) {
					sender.sendMessage("You must select an area to set as a goal!");
					return true;
				}
				
				min = selection.getMinimumPoint();
				max = selection.getMaximumPoint();
			}
			
			else if (args.length == 8){
				World world = Bukkit.getWorld(args[4]);
				Integer x = Integer.parseInt(args[5]);
				Integer y = Integer.parseInt(args[6]);
				Integer z = Integer.parseInt(args[7]);
				max = min = new Location(world, x, y, z);
			}
			else if (args.length == 11){
				World world = Bukkit.getWorld(args[4]);
				Integer x = Integer.parseInt(args[5]);
				Integer y = Integer.parseInt(args[6]);
				Integer z = Integer.parseInt(args[7]);
				Integer x2 = Integer.parseInt(args[8]);
				Integer y2 = Integer.parseInt(args[9]);
				Integer z2 = Integer.parseInt(args[10]);
				min = new Location(world, Math.min(x, x2), Math.min(y, y2), Math.min(z, z2));
				max = new Location(world, Math.max(x, x2), Math.max(y, y2), Math.max(z, z2));					
			}	
			
			com.sk89q.worldedit.Vector minV, maxV;
			minV = new com.sk89q.worldedit.Vector(min.getX(), min.getY(), min.getZ());
			maxV = new com.sk89q.worldedit.Vector(max.getX(), max.getY(), max.getZ());
			Region region = new CuboidRegion(minV, maxV);
			
			NoEditGoal goal = new NoEditGoal(region, team);
			
			team.setgoal(goal);
			
			sender.sendMessage("set the goal for team " + team.getName());
			return true;						
		}
		else if (args[1].equalsIgnoreCase("chest")) {
			//it will be
			///ctf team chest session teamname world x y z
			if (args.length != 8){
				sender.sendMessage("/cf team chest [session name] [team name] [world] [x] [y] [z]");
				return true; 
			}
			
			CTFSession session = null;
			CTFTeam team;
				
			for (CTFSession s : sessions) {
				if (s.getName().equalsIgnoreCase(args[2])) {
					session = s;
					break;
				}
			}
				
			if (session == null) {
				sender.sendMessage("Unable to find session with the name " + args[2]);
				return true;
			}
				
			//now get the team
			team = session.getTeam(args[3]);
				
			if (team == null) {
				sender.sendMessage("Unable to find team with the name " + args[3]);
				return true;
			}
			
			World world = Bukkit.getWorld(args[4]);
			Integer x = Integer.parseInt(args[5]);
			Integer y = Integer.parseInt(args[6]);
			Integer z = Integer.parseInt(args[7]);
			
			Location loc = new Location(world, x, y, z);
			
			Block block = loc.getBlock();
			if (!block.getType().equals(Material.CHEST)){
				sender.sendMessage("Not a chest");
				return true;
			}
			
			Chest chest = (Chest) block.getState();
			team.setInventory(chest.getInventory());
			
			return true;
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
