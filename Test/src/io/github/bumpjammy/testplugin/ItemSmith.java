package io.github.bumpjammy.testplugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;


public class ItemSmith extends JavaPlugin{
	
	public static ItemStack makeItem(String id, int amount) {
		FileConfiguration config = null;
		File file = new File(Main.getPlugin(Main.class).getDataFolder() + File.separator + ".items.yml");
		config = YamlConfiguration.loadConfiguration(file);
		String material = config.getString(id + ".material");
		String enchanted = config.getString(id + ".enchanted?");
		ItemStack item = new ItemStack (Material.getMaterial(material), 1);
		net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
		NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
		ArrayList<String> lore = new ArrayList<String>();
		compound.setString("id", config.getString(id + ".custom_id"));
		Set<String> keysInId = config.getConfigurationSection(id).getKeys(false);
		if(keysInId.contains("Damage")) {
			compound.setInt("Damage", config.getInt(id + ".Damage"));
			lore.add(ChatColor.GRAY + "Damage: " + ChatColor.RED + "+" + compound.getInt("Damage"));
		}
		if(keysInId.contains("Strength")) {
			compound.setInt("Strength", config.getInt(id + ".Strength"));
			lore.add(ChatColor.GRAY + "Strength: " + ChatColor.RED + "+" + compound.getInt("Strength"));
		}
		if(keysInId.contains("Defense")) {
			compound.setInt("Strength", config.getInt(id + ".Strength"));
			lore.add(ChatColor.GRAY + "Strength: " + ChatColor.RED + "+" + compound.getInt("Strength"));
		}
		if(keysInId.contains("Health")) {
			compound.setInt("Health", config.getInt(id + ".Health"));
			lore.add(ChatColor.GRAY + "Health: " + ChatColor.RED + "+" + compound.getInt("Health"));
		}
		nmsStack.setTag(compound);
		item = CraftItemStack.asBukkitCopy(nmsStack);
		//Create the item's meta data
		ItemMeta im = item.getItemMeta();
		//Create Lore
		if(!lore.isEmpty()) {
			lore.add("");
		}
		ArrayList<String> CustomLore = (ArrayList<String>) config.getStringList(id + ".lore");
		lore.addAll(CustomLore);
		if(!lore.isEmpty()) {
			lore.add("");
		}
		String rarity = config.getString(id + ".rarity");
		String name = config.getString(id + ".name");
		switch(rarity){
		case "Common":
			lore.add(ChatColor.WHITE + ChatColor.BOLD.toString() + "COMMON");
			im.setDisplayName(ChatColor.WHITE + name);
			break;
		case "Uncommon":
			lore.add(ChatColor.GREEN + ChatColor.BOLD.toString() + "UNCOMMON");
			im.setDisplayName(ChatColor.GREEN + name);
			break;
		case "Rare":
			lore.add(ChatColor.BLUE + ChatColor.BOLD.toString() + "RARE");
			im.setDisplayName(ChatColor.BLUE + name);
			break;
		case "Epic":
			lore.add(ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "EPIC");
			im.setDisplayName(ChatColor.DARK_PURPLE + name);
			break;
		case "Legendary":
			lore.add(ChatColor.GOLD + ChatColor.BOLD.toString() + "LEGENDARY");
			im.setDisplayName(ChatColor.GOLD + name);
			break;
		case "Mythic":
			lore.add(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "MYTHIC");
			im.setDisplayName(ChatColor.LIGHT_PURPLE + name);
			break;
		case "Supreme":
			lore.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "SUPREME");
			im.setDisplayName(ChatColor.DARK_RED + name);
			break;
		case "Special":
			lore.add(ChatColor.RED + ChatColor.BOLD.toString() + "SPECIAL");
			im.setDisplayName(ChatColor.RED + name);
			break;
		case "Very Special":
			lore.add(ChatColor.RED + ChatColor.BOLD.toString() + "VERY SPECIAL");
			im.setDisplayName(ChatColor.RED + name);
			break;
		}
		im.setLore(lore);
		//Hide vanilla tooltips
		im.setUnbreakable(true);
		im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		im.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		//Set the item's metadata
		if(enchanted.equals("true")) {
			im.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, false);
			im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		item.setItemMeta(im);
		item.setAmount(amount);
		return item;
	}
	
	public static ItemStack menuItem() {
		ItemStack item = new ItemStack(Material.NETHER_STAR, 1);
		ItemMeta im = item.getItemMeta();
		ArrayList<String> lore = new ArrayList<String>();
		im.setDisplayName(ChatColor.GREEN + "Skyblock Menu" + ChatColor.GRAY + " (Right Click)");
		lore.add(ChatColor.GRAY + "View all of your Skyblock");
		lore.add(ChatColor.GRAY + "progress, including your Skills,");
		lore.add(ChatColor.GRAY + "Collections, Recipes and more!");
		lore.add(" ");
		lore.add(ChatColor.YELLOW + "Click to open!");
		im.setLore(lore);
		im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(im);
		
		return item;
	}
	
	public static ItemStack getPlayerHead(Player player, ArrayList<String> CustomLore, String name) {
		ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta im = (SkullMeta) item.getItemMeta();
		ArrayList<String> lore = new ArrayList<String>();
		for(int i = 0; i < CustomLore.size(); i++){
			lore.add(CustomLore.get(i));
		}
		im.setLore(lore);
		im.setDisplayName(name);
		im.setOwner(player.getDisplayName());
		item.setItemMeta(im);
		
		return item;
	}
	
public static ItemStack makeVanillaItem(Material m, String name, int amount, ArrayList<String> CustomLore) {
		
		ItemStack item = new ItemStack (m, amount);
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(name);
		ArrayList<String> lore = new ArrayList<String>();
		for(int i = 0; i < CustomLore.size(); i++){
			lore.add(CustomLore.get(i));
		}
		im.setLore(lore);
		im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(im);
		return item;
	}

public static ItemStack addGlow(ItemStack item){
    net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
    NBTTagCompound tag = null;
    if (!nmsStack.hasTag()) {
        tag = new NBTTagCompound();
        nmsStack.setTag(tag);
    }
    if (tag == null) tag = nmsStack.getTag();
    NBTTagList ench = new NBTTagList();
    tag.set("ench", ench);
    nmsStack.setTag(tag);
    return CraftItemStack.asCraftMirror(nmsStack);
}

}
