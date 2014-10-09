package com.SkyIsland.CTF;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class CTFPlugin extends JavaPlugin {
	
	public static CTFPlugin plugin;
	public static WorldEditPlugin weplugin;
	
	public void onLoad() {
		
	}
	
	public void onEnable() {
		this.getDataFolder();
		
		CTFPlugin.plugin = this;
		CTFPlugin.weplugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
	}
	
	public void onDisable() {
		
	}
	
	
}
