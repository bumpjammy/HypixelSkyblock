package io.github.bumpjammy.testplugin;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.md_5.bungee.api.ChatColor;

public class abilities {
	
	public static void aote(Player player){
		for(int i=0;i<8;i++) {
			Location oneBlockAway = player.getLocation().add(player.getLocation().getDirection().multiply(1));
			if(oneBlockAway.getBlock().isEmpty()) {
				player.teleport(oneBlockAway);
			}else {
				if(oneBlockAway.add(0, 1, 0).getBlock().isEmpty()) {
					oneBlockAway = oneBlockAway.add(0, 1, 0);
					player.teleport(oneBlockAway);
				}else {
					return;
				}
			}
		}
	}
	
	public static void hyperion(Player player) {
		for(int i=0;i<8;i++) {
			Location oneBlockAway = player.getLocation().add(player.getLocation().getDirection().multiply(1));
			if(oneBlockAway.getBlock().isEmpty()) {
				player.teleport(oneBlockAway);
			}else {
				if(oneBlockAway.add(0, 1, 0).getBlock().isEmpty()) {
					oneBlockAway = oneBlockAway.add(0, 1, 0);
					player.teleport(oneBlockAway);
				}else {
					player.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, player.getLocation(), 0);
					player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2, 0);
					List<Entity> nearby =  player.getNearbyEntities(5,5,5);
					for (Entity tmp: nearby) {
						if (tmp instanceof Damageable) {
							if(!(tmp instanceof Player)) {
								((Damageable) tmp).damage(1000);
							}
						}
					}
					return;
				}
			}
			player.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, player.getLocation(), 0);
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 4, 0);
			List<Entity> nearby =  player.getNearbyEntities(5,5,5);
			for (Entity tmp: nearby) {
				if (tmp instanceof Damageable) {
					if(!(tmp instanceof Player)) {
						((Damageable) tmp).damage(1000);
					}
				}
			}
		}
	}
	
	public static void rogueSword(Player player) {
		PotionEffect potion = new PotionEffect(PotionEffectType.SPEED, 30*20, 1);
		player.addPotionEffect(potion);
	}

}
