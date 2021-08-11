package io.github.bumpjammy.testplugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.SkullType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
	
	
	private static JavaPlugin parent;
	public static Main plugin;
	 
	public void ListenerClass(JavaPlugin parent) {
		this.setParent(parent);
	}
	public void ScoreboardManager(JavaPlugin parent) {
		this.setParent(parent);
	}
	
	@Override
	public void onEnable() {
		getLogger().info("ENABLED");
		PluginManager pm = getServer().getPluginManager();
		ListenerClass listener = new ListenerClass(this);
		pm.registerEvents(listener, this);
		plugin = this;
		ItemStack enchantedEnderPearl = ItemSmith.makeItem("enchanted_ender_pearl", 1);
		NamespacedKey key = new NamespacedKey(this, "enchanted_ender_pearl");
		ShapedRecipe recipe = new ShapedRecipe(key, enchantedEnderPearl);
		recipe.shape("EEE", "EE ", "   ");
		recipe.setIngredient('E', Material.ENDER_PEARL);
		Bukkit.addRecipe(recipe);
		
		File file = new File(getDataFolder()+File.separator+"inventories.yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		if(file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	@Override
	public void onDisable() {
		
		getLogger().info("Test Plugin disabled");
		
	}
	
	
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		Player player = null;
		
		if(sender instanceof Player) {
			player = (Player) sender;
		}
		
		ArrayList<String> lore = new ArrayList<String>();
		
		if(true) {
			String lowerCmd = cmd.getName().toLowerCase();
			
			switch (lowerCmd) {
			
			case "bank":
				Player bankReciever = Bukkit.getPlayer(args[0]);
				ListenerClass.TL.bankOpen(bankReciever);
				break;
				
			case "is":
				Teleport.island(player);
				break;
				
			case "hub":
				Teleport.Hub(player);
				break;
			
			case "givemoney":
				try {
					ListenerClass.TL.giveMoney(player, Integer.parseInt(args[0]));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
				
			case "setmoney":
				try {
					ListenerClass.TL.setMoney(player, Integer.parseInt(args[0]));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			
			case "sbmenu":
				ListenerClass.TL.loadSBMenu(player);
				break;
			
			case "summoncustom":
				switch(args[0]) {
				case "zombie":
					MobSpawner.spawnSpecial(player, "zombie");
					break;
				case "lapis_zombie":
					MobSpawner.spawnSpecial(player, "lapis_zombie");
					break;
				}
				break;
			
			case "giveme":
				if(player.isOp()) {
					ItemStack item;
					if(args.length == 1) {
						item = ItemSmith.makeItem(args[0], 1);
					}else {
						item = ItemSmith.makeItem(args[0], Integer.parseInt(args[1]));
					}
					player.getInventory().addItem(item);
				
					return true;
				}else {
					player.sendMessage("You require op for this command!");
				}
				
				break;
				
			default:
				return true;
			
			}
		}
		
		return true;
	}

	public static JavaPlugin getParent() {
		return parent;
	}

	public static void setParent(JavaPlugin parent) {
		Main.parent = parent;
	}

	
}
