package io.github.bumpjammy.testplugin;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Combat {
	
	public static void HealthRegen(Player p) {
		if(p.getHealth() < p.getMaxHealth()) { // If health is below max
	    	double regen = 1.5 + (p.getMaxHealth()/100);
	    	if(p.getHealth() + regen > p.getMaxHealth()) {
	    		p.setHealth(p.getMaxHealth()); // Stop going over max health through regen
	    	} else {
	    		p.setHealth(p.getHealth() + regen);
	    	}
	    }
	}
}
