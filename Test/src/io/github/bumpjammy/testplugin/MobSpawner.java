package io.github.bumpjammy.testplugin;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class MobSpawner {
	
	public static void spawnSpecial(Player player, String mob) {
		switch(mob) {
		case "zombie":
			Zombie zombie = (Zombie) player.getWorld().spawnEntity(player.getLocation(), EntityType.ZOMBIE);
			zombie.setAge(12000);
			zombie.setMaxHealth(100);
			zombie.setHealth(100);
			zombie.setCustomName(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Lv1" + ChatColor.DARK_GRAY + "]" + ChatColor.RED + "Zombie " + ChatColor.GREEN + "100/100" + ChatColor.RED + "❤");
			break;
		case "lapis_zombie":
			Zombie lapis_zombie = (Zombie) player.getWorld().spawnEntity(player.getLocation(), EntityType.ZOMBIE);
			lapis_zombie.setAge(12000);
			lapis_zombie.setMaxHealth(200);
			lapis_zombie.setHealth(200);
			
			ItemStack lapis_boots = new ItemStack(Material.LEATHER_BOOTS);
			LeatherArmorMeta lapis_boots_meta = (LeatherArmorMeta) lapis_boots.getItemMeta();
			lapis_boots_meta.setColor(Color.BLUE);
			lapis_boots.setItemMeta(lapis_boots_meta);
			lapis_zombie.getEquipment().setBoots(lapis_boots);
			
			ItemStack lapis_pants = new ItemStack(Material.LEATHER_LEGGINGS);
			LeatherArmorMeta lapis_pants_meta = (LeatherArmorMeta) lapis_pants.getItemMeta();
			lapis_pants_meta.setColor(Color.BLUE);
			lapis_pants.setItemMeta(lapis_pants_meta);
			lapis_zombie.getEquipment().setLeggings(lapis_pants);
			
			ItemStack lapis_chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
			LeatherArmorMeta lapis_chestplate_meta = (LeatherArmorMeta) lapis_chestplate.getItemMeta();
			lapis_chestplate_meta.setColor(Color.BLUE);
			lapis_chestplate.setItemMeta(lapis_chestplate_meta);
			lapis_zombie.getEquipment().setChestplate(lapis_chestplate);
			
			ItemStack lapis_helmet = new ItemStack(Material.BLUE_STAINED_GLASS);
			lapis_zombie.getEquipment().setHelmet(lapis_helmet);
			
			lapis_zombie.setCustomName(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Lv7" + ChatColor.DARK_GRAY + "]" + ChatColor.RED + "Lapis Zombie " + ChatColor.GREEN + "200/200" + ChatColor.RED + "❤");
			break;
		}
	}

}
