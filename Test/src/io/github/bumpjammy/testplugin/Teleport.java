package io.github.bumpjammy.testplugin;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Teleport {
	
	
	public static void Hub(Player player) {
		player.teleport(new Location(Bukkit.getWorld("world"), -2,70,-68,180f,0f));
	}
	
	
	public static void island(Player player){
		String islandName = PlayerInfo.getProfileData(player, "island");
		if(Bukkit.getWorld(islandName) != null) {
			Location island = new Location(Bukkit.getWorld(islandName), 6,100,41,180f,0f);
			player.teleport(island);
		}else {
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "mvclone Default_Island " + islandName);
			Location island = new Location(Bukkit.getWorld(islandName), 6,100,41,180f,0f);
			player.teleport(island);
			Bukkit.getWorld(islandName).setGameRule(GameRule.DO_MOB_SPAWNING, false);
		}
	}
	
	
}
