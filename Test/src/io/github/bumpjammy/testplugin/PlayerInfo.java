package io.github.bumpjammy.testplugin;

import java.io.File;

import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class PlayerInfo {
	public static String getProfileData(Player player, String option) {
		File file = new File(Main.getPlugin(Main.class).getDataFolder() + File.separator + player.getName() + ".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		String profile = config.getString("Player.Active Profile");
		String output = null;
		switch (option) {
		case "island":
			output = config.getString("Player." + profile + ".island");
		}
		return output;
	}
	public static int findStats(Player player, String option) throws NullPointerException{
		int healthAdd = 0;
		int defenseAdd = 0;
		int strengthAdd = 0;
		int health = 100;
		int defense = 0;
		int strength = 0;
		
		net.minecraft.world.item.ItemStack nmsStack;
		NBTTagCompound compound;
		
		ItemStack helmet = player.getInventory().getHelmet();
		if(helmet != null) {
			nmsStack = CraftItemStack.asNMSCopy(helmet);
			compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
			if(compound.hasKey("Strength")) {
				strengthAdd = compound.getInt("Strength");
			}
			if(compound.hasKey("Defense")) {
				defenseAdd = compound.getInt("Defense");
			}
			if(compound.hasKey("Health")) {
				defenseAdd = compound.getInt("Health");
			}
		}
		
		health += healthAdd;
		healthAdd = 0;
		defense += defenseAdd;
		defenseAdd = 0;
		strength += strengthAdd;
		strengthAdd = 0;
		
		ItemStack chestplate = player.getInventory().getChestplate();
		if(chestplate != null) {
			nmsStack = CraftItemStack.asNMSCopy(chestplate);
			compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
			if(compound.hasKey("Strength")) {
				strengthAdd = compound.getInt("Strength");
			}
			if(compound.hasKey("Defense")) {
				defenseAdd = compound.getInt("Defense");
			}
			if(compound.hasKey("Health")) {
				defenseAdd = compound.getInt("Health");
			}
		}
		
		health += healthAdd;
		healthAdd = 0;
		defense += defenseAdd;
		defenseAdd = 0;
		strength += strengthAdd;
		strengthAdd = 0;
		
		ItemStack leggings = player.getInventory().getLeggings();
		if(leggings != null) {
			nmsStack = CraftItemStack.asNMSCopy(helmet);
			compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
			if(compound.hasKey("Strength")) {
				strengthAdd = compound.getInt("Strength");
			}
			if(compound.hasKey("Defense")) {
				defenseAdd = compound.getInt("Defense");
			}
			if(compound.hasKey("Health")) {
				defenseAdd = compound.getInt("Health");
			}
		}
		
		health += healthAdd;
		healthAdd = 0;
		defense += defenseAdd;
		defenseAdd = 0;
		strength += strengthAdd;
		strengthAdd = 0;
		
		ItemStack boots = player.getInventory().getBoots();
		if(boots != null) {
			nmsStack = CraftItemStack.asNMSCopy(boots);
			compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
			if(compound.hasKey("Strength")) {
				strengthAdd = compound.getInt("Strength");
			}
			if(compound.hasKey("Defense")) {
				defenseAdd = compound.getInt("Defense");
			}
			if(compound.hasKey("Health")) {
				defenseAdd = compound.getInt("Health");
			}
		}
		
		health += healthAdd;
		healthAdd = 0;
		defense += defenseAdd;
		defenseAdd = 0;
		strength += strengthAdd;
		strengthAdd = 0;
		
		ItemStack hand = player.getInventory().getItemInMainHand();
		if(hand.hasItemMeta()) {
			nmsStack = CraftItemStack.asNMSCopy(hand);
			compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
			if(compound.hasKey("Strength")) {
				strengthAdd = compound.getInt("Strength");
			}
			if(compound.hasKey("Defense")) {
				defenseAdd = compound.getInt("Defense");
			}
			if(compound.hasKey("Health")) {
				defenseAdd = compound.getInt("Health");
			}
		}
		
		health += healthAdd;
		healthAdd = 0;
		defense += defenseAdd;
		defenseAdd = 0;
		strength += strengthAdd;
		strengthAdd = 0;
		
		player.setHealthScale(20);
		player.setHealthScaled(true);
		Double HealthPercentage = (player.getHealth()/player.getMaxHealth());
		player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
		player.setMaxHealth(health);
		player.setHealth((double) HealthPercentage * player.getMaxHealth());
		switch(option) {
		case "health":
			return health;
		case "defense":
			return defense;
		case "strength":
			return strength;
		default:
			return 0;
		}
	}
}
