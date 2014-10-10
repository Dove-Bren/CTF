package com.SkyIsland.CTF;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;

/**
 * Keeps track of the types of CTF. Not an enum so you can add more
 * @author Skyler
 *
 */
public final class CTFTypes {
	
	private static List<Class<? extends CTFSession>> sessionTypes = new LinkedList<Class<? extends CTFSession>>();
	
	public static void registerType(Class<? extends CTFSession> session) {
		sessionTypes.add(session);
		Bukkit.getLogger().info("Registered CTFSession with name " + session.getSimpleName() + " successfully!");
	}
	
	public static Class<? extends CTFSession> getSession(String name) {
		for (Class<? extends CTFSession> cls : sessionTypes) {
			if (cls.getSimpleName().equalsIgnoreCase(name)) {
				return cls;
			}
		}
		return null;
	}
	
	public static void clear() {
		sessionTypes.clear();
	}
}
