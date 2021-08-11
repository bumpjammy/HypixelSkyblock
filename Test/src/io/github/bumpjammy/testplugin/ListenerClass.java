package io.github.bumpjammy.testplugin;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.network.PlayerConnection;

public class ListenerClass implements Listener {
	
	public PlayerConnection b;
	
	Random rand = new Random();
	
	net.minecraft.world.item.ItemStack nmsStack;
	net.minecraft.nbt.NBTTagCompound compound;
	
	ScoreboardManager manager = Bukkit.getScoreboardManager();
	public HashMap<String, Scoreboard> boards = new HashMap<String, Scoreboard>();
	
	public static ListenerClass TL;

	int coins = 0;
	int bankLimit = 0;
	
	ArrayList<String> lore = new ArrayList<String>();

	// Constructor
	public ListenerClass(Main plugin) {
		TL = this; // To reference class in other classes, avoiding static abuse
	}
	
	int id = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), new Runnable() {
	    public void run() {
	    	for(Player p : Bukkit.getOnlinePlayers()){ // Custom regen system
	    		Combat.HealthRegen(p);
	    		ScoreboardManagers.createScoreboard(p);
	    	}
	    }}, 0, 20); // Run above script every 20 ticks (1 second)
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getBlock().getType() == Material.STONE || event.getBlock().getType() == Material.COBBLESTONE) { // Check if block is stone
			event.setDropItems(false); // Cancel the drops
			lore.clear(); // Clears reused variable for setting the lore of items
			ItemStack item = ItemSmith.makeItem("cobblestone", 1); // Creates item using ItemSmith.java
			event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), item);
		}
	}
	
	@EventHandler
	public void OnPortal(PlayerTeleportEvent event) {
		if(event.getCause() == TeleportCause.END_PORTAL) {
			event.setCancelled(true);
			if(event.getPlayer().getLocation().getX() < 2 && event.getPlayer().getLocation().getX() > -7 && event.getPlayer().getLocation().getZ() < -57 && event.getPlayer().getLocation().getZ() > -67) {
				Teleport.island((Player) event.getPlayer());
			}
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) throws IOException, InvalidConfigurationException { // Throws exceptions as we have checks in place
		Player player = event.getPlayer();
		player.spigot().sendMessage( ChatMessageType.ACTION_BAR, new TextComponent("This message will be in the Action Bar"));
		Teleport.Hub(player); // Teleport to hub
		event.getPlayer().getInventory().setItem(8, ItemSmith.menuItem()); // SBMenu item
		PlayerInfo.findStats(player, ""); // Get stats of player
		FileConfiguration config = null;
		File file = new File(Main.getPlugin(Main.class).getDataFolder() + File.separator + event.getPlayer().getName() + ".yml");
		if(!file.exists()){ // If the player file doesn't exist (First log-in)
		        file.createNewFile(); // Creates new file
		        config = YamlConfiguration.loadConfiguration(file);
		        config.set("Player.Active Profile", "Apple");
		        config.set("Player.Rank", "None");
		        config.set("Player.Apple.Money", 0);
		        config.set("Player.Apple.Bank", 0);
		        config.set("Player.Apple.BankLimit", 50000000); // Creates new profile "Apple" with everything set to default
		        config.set("Player.Apple.island", player.getUniqueId().toString() + rand.nextInt(99999999)); // Sets island name so no duplicate islands happen
		        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "mvclone Default_Island " + config.getString("Player.Apple.island")); // Sends out a command to MultiVerse to create a new world for this profile
				Location island = new Location(Bukkit.getWorld(config.getString("Player.Apple.island")), 6,100,41,180f,0f);
				player.teleport(island); // Teleports to island
				Bukkit.getWorld(config.getString("Player.Apple.island")).setGameRule(GameRule.DO_MOB_SPAWNING, false); // Set mob spawning to false
				//TODO CUSTOM MOB SPAWNING ON ISLAND
		        config.save(file);
		}
		config = YamlConfiguration.loadConfiguration(file);
		String profile = config.getString("Player.Active Profile");
		coins = config.getInt("Player." + profile +".Money");
	}
	
	@EventHandler
	public void EntityDeathEvent(EntityDeathEvent event) {
		event.getDrops().clear();
	}

	@EventHandler
	public void EntityDamageEvent(EntityDamageEvent event) throws IOException {
		if(event.getCause().equals(DamageCause.FALL)) {
			event.setCancelled(true);
		}
		if(event.getCause().equals(DamageCause.VOID) && event.getEntity() instanceof Player) {
			event.setCancelled(true);
			File file = new File(Main.getPlugin(Main.class).getDataFolder() + File.separator + event.getEntity().getName() + ".yml");
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			String profile = config.getString("Player.Active Profile");
			coins = config.getInt("Player." + profile + ".Money");
			death((Player) event.getEntity(), coins);
		}
	}
	
	@EventHandler
	public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		
		if(event.getDamager() instanceof Player && event.getEntity() instanceof Player){
			event.setCancelled(true);
		}
		
		
		if(event.getDamager() instanceof Arrow){
			Projectile arrow = (Projectile) event.getDamager();
			if(arrow.getShooter() instanceof Player && event.getEntity() instanceof Player) {
				event.setCancelled(true);
			}
		}
		
		String name = event.getDamager().getName().toString().replaceAll("[§][a].*", "");
		Bukkit.getLogger().info(name);
		if(name.contains("Lapis Zombie")){
			event.setDamage(40);
		}
		
		if(event.getDamager() instanceof Player && !(event.getEntity() instanceof Player)) {
			Player player = (Player) event.getDamager();
			int strength = PlayerInfo.findStats(player, "strength");
			int damage = 0;
			if(player.getInventory().getItemInMainHand() != null) {
				ItemStack itemInHand = player.getInventory().getItemInMainHand();
				List<String> itemLore = itemInHand.getItemMeta().getLore();
				for(int i = 0; i < itemLore.size(); i++) {
					if(itemLore.get(i).contains("Damage")) {
						String numberOnly = itemLore.get(i).replaceAll("[^0-9]", "");
						numberOnly = numberOnly.substring(1);
						damage = Integer.parseInt(numberOnly);
					}
				}
			}
			event.setCancelled(false);
			int damageDealt = (5+damage) * (1 + (strength/100));
			event.setDamage(0);
			if(event.getEntity().getName().contains("❤")) {
				String truncatedName = event.getEntity().getName().replaceAll("\\s+\\S*$", "");
				int HealthBeforeDamage = (int) ((Damageable) event.getEntity()).getHealth();
				if(HealthBeforeDamage - damageDealt < 1) {
					event.getEntity().setCustomName(truncatedName + " " + ChatColor.GREEN + "0/" + (int) ((Damageable) event.getEntity()).getMaxHealth() + ChatColor.RED + "❤");
					((Damageable) event.getEntity()).damage(9999999);
				}else {
					event.getEntity().setCustomName(truncatedName + " " + ChatColor.GREEN + (HealthBeforeDamage - damageDealt)+ "/" + (int) ((Damageable) event.getEntity()).getMaxHealth() + ChatColor.RED + "❤");
					((Damageable) event.getEntity()).setHealth(HealthBeforeDamage - damageDealt);
				}
			}
		}
	}
	
	
	@EventHandler
	public void onHealthChange(EntityRegainHealthEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void InventoryClickEvent(InventoryClickEvent event) throws IOException {
		if(event.getInventory().equals(event.getWhoClicked().getInventory())) {
			if(event.getSlot() == 8) {
				event.setCancelled(true);
				loadSBMenu((Player) event.getWhoClicked());
			}
		}
		if(event.getView().getTitle().equals("Potions")) {
			event.setCancelled(true);
		}
		if(event.getView().getTitle().equals("Deposit Coins")) {
			event.setCancelled(true);
			File file = new File(Main.getPlugin(Main.class).getDataFolder() + File.separator + event.getWhoClicked().getName() + ".yml");
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			String profile = config.getString("Player.Active Profile");
			int bankBal = config.getInt("Player." + profile + ".Bank");
			coins = config.getInt("Player." + profile + ".Money");
			bankLimit = config.getInt("Player." + profile + ".BankLimit");
			switch(event.getSlot()) {
			case 11:
				if(bankBal + coins > bankLimit) {
					config.set("Player." + profile + ".Bank", bankLimit);
					config.set("Player." + profile + ".Money", coins - (bankLimit - bankBal));
					config.save(file);
					event.getWhoClicked().closeInventory();
					event.getWhoClicked().sendMessage(ChatColor.GREEN + "Deposited " + ChatColor.GOLD + String.format("%,d", bankLimit - bankBal) + " coins" + ChatColor.GREEN + "! There's now " + ChatColor.GOLD + String.format("%,d", bankLimit) + " coins" + ChatColor.GREEN + " in the account!");
					return;
				}
				config.set("Player." + profile + ".Bank", coins + bankBal);
				config.set("Player." + profile + ".Money", 0);
				config.save(file);
				event.getWhoClicked().closeInventory();
				event.getWhoClicked().sendMessage(ChatColor.GREEN + "Deposited " + ChatColor.GOLD + String.format("%,d", coins) + " coins" + ChatColor.GREEN + "! There's now " + ChatColor.GOLD + String.format("%,d", coins + bankBal) + " coins" + ChatColor.GREEN + " in the account!");
				break;
			case 13:
				if(bankBal + (coins/2) > bankLimit) {
					config.set("Player." + profile + ".Bank", bankLimit);
					config.set("Player." + profile + ".Money", coins - (bankLimit - bankBal));
					config.save(file);
					event.getWhoClicked().closeInventory();
					event.getWhoClicked().sendMessage(ChatColor.GREEN + "Deposited " + ChatColor.GOLD + String.format("%,d", bankLimit - bankBal) + " coins" + ChatColor.GREEN + "! There's now " + ChatColor.GOLD + String.format("%,d", bankLimit) + " coins" + ChatColor.GREEN + " in the account!");
					return;
				}
				coins = config.getInt("Player." + profile + ".Money");
				config.set("Player." + profile + ".Bank", coins/2 + bankBal);
				config.set("Player." + profile + ".Money", coins/2);
				config.save(file);
				event.getWhoClicked().closeInventory();
				event.getWhoClicked().sendMessage(ChatColor.GREEN + "Deposited " + ChatColor.GOLD + String.format("%,d", coins/2) + " coins" + ChatColor.GREEN + "! There's now " + ChatColor.GOLD + String.format("%,d", coins/2 + bankBal) + " coins" + ChatColor.GREEN + " in the account!");
				break;
			}
			
		}
		
		if(event.getView().getTitle().equals("Withdraw Coins")) {
			event.setCancelled(true);
			File file = new File(Main.getPlugin(Main.class).getDataFolder() + File.separator + event.getWhoClicked().getName() + ".yml");
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			String profile = config.getString("Player.Active Profile");
			int bankBal = config.getInt("Player." + profile + ".Bank");
			coins = config.getInt("Player" + profile + ".Money");
			switch(event.getSlot()) {
			
			case 11:
				config.set("Player." + profile + ".Bank", 0);
				config.set("Player." + profile + ".Money", coins + bankBal);
				config.save(file);
				event.getWhoClicked().closeInventory();
				event.getWhoClicked().sendMessage(ChatColor.GREEN + "You have withdrawn " + ChatColor.GOLD + String.format("%,d", bankBal) + " coins" + ChatColor.GREEN + "! You now have " + ChatColor.GOLD + "0 coins" + ChatColor.GREEN + " in your account!");
				break;
			case 13:
				coins = config.getInt("Player." + profile + ".Money");
				config.set("Player." + profile + ".Bank", bankBal/2);
				config.set("Player." + profile + ".Money", bankBal/2 + coins);
				config.save(file);
				event.getWhoClicked().closeInventory();
				event.getWhoClicked().sendMessage(ChatColor.GREEN + "You have withdrawn " + ChatColor.GOLD + String.format("%,d", bankBal/2) + " coins" + ChatColor.GREEN + "! You now have " + ChatColor.GOLD + String.format("%,d", bankBal/2) + " coins" + ChatColor.GREEN + " in your account!");
				break;
			}
			
		}
		if(event.getView().getTitle().equals("Bank")) {
			event.setCancelled(true);
			switch(event.getSlot()) {
				case 11:
					Inventory BankDeposit = Bukkit.createInventory(event.getWhoClicked(), 36, "Deposit Coins");
					File file = new File(Main.getPlugin(Main.class).getDataFolder() + File.separator + event.getWhoClicked().getName() + ".yml");
					
					FileConfiguration config = YamlConfiguration.loadConfiguration(file);
					String profile = config.getString("Player.Active Profile");
					int bankBal = config.getInt("Player." + profile + ".Bank");
					coins = config.getInt("Player." + profile + ".Money");
					
					lore.clear();
					lore.add(ChatColor.DARK_GRAY + "Bank deposit");
					lore.add("");
					lore.add(ChatColor.GRAY + "Current balance: " + ChatColor.GOLD + String.format("%,d", bankBal));
					lore.add(ChatColor.GRAY + "Amount to deposit: " + ChatColor.GOLD + String.format("%,d", coins));
					lore.add("");
					lore.add(ChatColor.YELLOW + "Click to deposit coins!");
					BankDeposit.setItem(11, ItemSmith.makeVanillaItem(Material.CHEST, ChatColor.GREEN + "Your Whole Purse", 64, lore));
					
					lore.clear();
					lore.add(ChatColor.DARK_GRAY + "Bank deposit");
					lore.add("");
					lore.add(ChatColor.GRAY + "Current balance: " + ChatColor.GOLD + String.format("%,d", bankBal));
					lore.add(ChatColor.GRAY + "Amount to deposit: " + ChatColor.GOLD + String.format("%,d", coins/2));
					lore.add("");
					lore.add(ChatColor.YELLOW + "Click to deposit coins!");
					BankDeposit.setItem(13, ItemSmith.makeVanillaItem(Material.CHEST, ChatColor.GREEN + "Half Your Purse", 32, lore));
					
					event.getWhoClicked().openInventory(BankDeposit);
					break;
				case 13:
					Inventory BankWithdraw = Bukkit.createInventory(event.getWhoClicked(), 36, "Withdraw Coins");
					File file2 = new File(Main.getPlugin(Main.class).getDataFolder() + File.separator + event.getWhoClicked().getName() + ".yml");
					
					FileConfiguration config2 = YamlConfiguration.loadConfiguration(file2);
					String profile2 = config2.getString("Player.Active Profile");
					int bankBal2 = config2.getInt("Player." + profile2 + ".Bank");
					coins = config2.getInt("Player." + profile2 + ".Money");
					
					lore.clear();
					lore.add(ChatColor.DARK_GRAY + "Bank withdrawal");
					lore.add("");
					lore.add(ChatColor.GRAY + "Current balance: " + ChatColor.GOLD + String.format("%,d", bankBal2));
					lore.add(ChatColor.GRAY + "Amount to withdraw: " + ChatColor.GOLD + String.format("%,d", bankBal2));
					lore.add("");
					lore.add(ChatColor.YELLOW + "Click to withdraw coins!");
					BankWithdraw.setItem(11, ItemSmith.makeVanillaItem(Material.FURNACE, ChatColor.GREEN + "Everything in the account", 64, lore));
					
					lore.clear();
					lore.add(ChatColor.DARK_GRAY + "Bank withdrawal");
					lore.add("");
					lore.add(ChatColor.GRAY + "Current balance: " + ChatColor.GOLD + String.format("%,d", bankBal2));
					lore.add(ChatColor.GRAY + "Amount to withdraw: " + ChatColor.GOLD + String.format("%,d", bankBal2/2));
					lore.add("");
					lore.add(ChatColor.YELLOW + "Click to withdraw coins!");
					BankWithdraw.setItem(13, ItemSmith.makeVanillaItem(Material.FURNACE, ChatColor.GREEN + "Half the account", 32, lore));
					
					event.getWhoClicked().openInventory(BankWithdraw);
					break;
				case 31:
					event.getWhoClicked().closeInventory();
					break;
				case 35:
					Inventory BankUpgrade = Bukkit.createInventory(event.getWhoClicked(), 36, "Upgrade Account");
					File file3 = new File(Main.getPlugin(Main.class).getDataFolder() + File.separator + event.getWhoClicked().getName() + ".yml");
					
					FileConfiguration config3 = YamlConfiguration.loadConfiguration(file3);
					String profile3 = config3.getString("Player.Active Profile");
					coins = config3.getInt("Player." + profile3 + ".Money");
					bankLimit = config3.getInt("Player." + profile3 + ".BankLimit");
					
					lore.clear();
					lore.add(ChatColor.GOLD + "Interest Tranches");
					lore.add(ChatColor.YELLOW + "First " + ChatColor.GOLD + 10 + ChatColor.YELLOW + " million coins yields " + ChatColor.AQUA + " 2%" + ChatColor.YELLOW + " interest");
					lore.add(ChatColor.YELLOW + "From " + ChatColor.GOLD + 10 + ChatColor.YELLOW + " to" + ChatColor.GOLD + " 15" + ChatColor.YELLOW + " million coins yields " + ChatColor.AQUA + " 1%" + ChatColor.YELLOW + " interest");
					lore.add("");
					lore.add(ChatColor.GRAY + "Max interest: " + ChatColor.GOLD + "250,000");
					lore.add(ChatColor.DARK_GRAY + " (With 15,000,000 balance)");
					lore.add("");
					lore.add(ChatColor.GRAY + "Max Balance: " + ChatColor.GOLD + "50,000,000");
					lore.add("");
					lore.add(ChatColor.GRAY + "Cost: " + ChatColor.GOLD + "Free");
					lore.add("");
					
					if(bankLimit > 50000000) {
						lore.add(ChatColor.RED + "You have a better account!");
					}
					if(bankLimit == 50000000) {
						lore.add(ChatColor.GREEN + "This is your account!");
					}
					
					BankUpgrade.setItem(11, ItemSmith.makeVanillaItem(Material.WHEAT_SEEDS, ChatColor.GREEN + "Starter Account", 1, lore));
					
					event.getWhoClicked().openInventory(BankUpgrade);
					break;
				}
			}
			if(event.getView().getTitle().equals("Upgrade Account")) {
				event.setCancelled(true);
			}
			if(event.getView().getTitle().equals("Skyblock Menu")) {
				switch(event.getSlot()){
				case 25:
					event.getWhoClicked().openInventory(event.getWhoClicked().getEnderChest());
					event.setCancelled(true);
					break;
				case 29:
					Collection<PotionEffect> potions = event.getWhoClicked().getActivePotionEffects();
					ArrayList<PotionEffect> potionlist = new ArrayList<PotionEffect>(potions);
					Inventory PotionMenu = Bukkit.createInventory(event.getWhoClicked(), 54, "Potions");
					lore.clear();
					lore.add(ChatColor.GRAY + "View and manage all of your");
					lore.add(ChatColor.GRAY + "active potion effects.");
					lore.add("");
					lore.add(ChatColor.GRAY + "Drink potions or splash them");
					lore.add(ChatColor.GRAY + "on the ground to buff yourself!");
					lore.add("");
					lore.add(ChatColor.GRAY + "Currently active: " + ChatColor.YELLOW + potions.size());
					PotionMenu.setItem(4, ItemSmith.makeVanillaItem(Material.GLASS_BOTTLE, ChatColor.GREEN + "Active Effects", 1, lore));
					for(int i = 0; i < potionlist.size(); i++) {
						lore.clear();
						lore.add(ChatColor.GRAY + "Time remaining: " + ChatColor.YELLOW + potionlist.get(i).getDuration()/20 + " seconds");
						PotionMenu.setItem(i+9, ItemSmith.makeVanillaItem(Material.GLASS_BOTTLE, ChatColor.GREEN + potionlist.get(i).getType().getName() + " " + potionlist.get(i).getAmplifier(), 1, lore));
					}
					event.getWhoClicked().openInventory(PotionMenu);
					event.setCancelled(true);
					break;
				case 31:
					event.getWhoClicked().openWorkbench(null, true);
					event.setCancelled(true);
					break;
				default:
					event.setCancelled(true);
					break;
			}
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), new Runnable() {
		    @Override
		    public void run() {
		    	PlayerInfo.findStats((Player) event.getWhoClicked(), "");
		    }
		}, 1L);
	}
	
	@EventHandler
	public void onInventoryDropEvent(PlayerDropItemEvent e) {
		if(e.getItemDrop().getItemStack().equals(ItemSmith.menuItem())) {
			Player p = (Player)e.getPlayer();
			ItemStack item = e.getItemDrop().getItemStack().clone();
			item.setAmount(p.getInventory().getItemInHand().getAmount() - 1);
			e.getItemDrop().remove();
			p.getInventory().setItem(8, ItemSmith.menuItem());
			p.updateInventory();
			loadSBMenu(e.getPlayer());
		}
    }
	
	@EventHandler
	public void FoodLevelChangeEvent(org.bukkit.event.entity.FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void PlayerInteractEvent(PlayerInteractEvent event) throws InterruptedException {
		PlayerInfo.findStats((Player) event.getPlayer(), "");
		if(event.getItem() == null) return;
			ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
			if (item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Skyblock Menu" + ChatColor.GRAY + " (Right Click)") && event.getAction().toString().contains("RIGHT_CLICK")) {
				Player player = event.getPlayer();
				event.setCancelled(true);
				loadSBMenu(player);
			}
			if(event.getAction().toString().contains("RIGHT_CLICK")) {
				nmsStack = CraftItemStack.asNMSCopy(item);
				compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
				if(compound.hasKey("id")) {
					String itemID = compound.getString("id");
					switch(itemID) {
					case("Aspect_Of_The_End"):
						abilities.aote(event.getPlayer());
						break;
					case("Hyperion"):
						abilities.hyperion(event.getPlayer());
						break;
					case("Rogue_Sword"):
						abilities.rogueSword(event.getPlayer());
						break;
					case("enchanted_ender_pearl"):
						event.setCancelled(true);
						break;
					}
				}
			}
	}
	
	@EventHandler
	public void PrepareItemCraftEvent(org.bukkit.event.inventory.PrepareItemCraftEvent event) {
		CraftingInventory inv = event.getInventory();
		if(inv.getResult() != null && inv.getResult().equals(ItemSmith.makeItem("enchanted_ender_pearl", 1))) {
			  for(ItemStack item : inv.getMatrix()){

	                // If the crafting slot has an item, and it is not similar...
	                if(item!=null && !item.isSimilar(ItemSmith.makeItem("ender_pearl", 1))){

	                    // Prevent the result (crafted item) from being shown
	                    inv.setResult(null);

	                }else {
	                	if(item.getAmount() < 4) {
	                		inv.setResult(null);
	                	}
	                }
	            }
		}
	};
	
	@EventHandler
	public void InventoryInteractEvent(InventoryInteractEvent event){
		if(event.getInventory().getType().equals(InventoryType.CRAFTING)) {
			
		}
	}
	
	@EventHandler
	public void ItemCraftEvent(CraftItemEvent event) {
		CraftingInventory inv = event.getInventory();
		if(inv.getResult().isSimilar(ItemSmith.makeItem("enchanted_ender_pearl", 1))) {
			for(ItemStack item : inv.getMatrix()){
				item.setAmount(item.getAmount() - 4);
			}
		}
	}
	
	
	public String displayTime(Long time) {
	    Long ticks = time;
	    double hh = Math.floor(ticks / 3600);
	    double mm = Math.floor((ticks % 3600) / 60);

	    DecimalFormat remove0 = new DecimalFormat("##");
	    String hhString = remove0.format(hh);
	    String mmString = remove0.format(mm);
	    if(hhString.length() == 1) {
	    	hhString = "0" + hhString;
	    }
	    if(mmString.length() == 1) {
	    	mmString = "0" + mmString;
	    }

	    return (hhString + ":" + mmString);
	}
	
	public void giveMoney(Player player, int amount) throws IOException {
		FileConfiguration config = null;
		File file = new File(Main.getPlugin(Main.class).getDataFolder() + File.separator + player.getName() + ".yml");
		config = YamlConfiguration.loadConfiguration(file);
		String profile = config.getString("Player.Active Profile");
		config.set("Player." + profile + ".Money", config.getInt("Player." + profile + ".Money") + amount);
		config.save(file);
	}
	
	public void death(Player player, int totalBalance) throws IOException {
		FileConfiguration config = null;
		File file = new File(Main.getPlugin(Main.class).getDataFolder() + File.separator + player.getName() + ".yml");
		config = YamlConfiguration.loadConfiguration(file);
		String profile = config.getString("Player.Active Profile");
		config.set("Player." + profile + ".Money", totalBalance / 2);
		config.save(file);
		
		if(player.getWorld().equals(Bukkit.getWorld("world"))) {
			Teleport.island(player);
		}
		if(player.getWorld().equals(Bukkit.getWorld(config.getString("Player." + profile + ".island")))) {
			Teleport.island(player);
		}
		player.sendMessage(ChatColor.RED + "You died and lost " + String.format("%,d", totalBalance/2) + " coins!");
	}
	
	public void setMoney(Player player, int amount) throws IOException {
		FileConfiguration config = null;
		File file = new File(Main.getPlugin(Main.class).getDataFolder() + File.separator + player.getName() + ".yml");
		config = YamlConfiguration.loadConfiguration(file);
		String profile = config.getString("Player.Active Profile");
		config.set("Player." + profile + ".Money", amount);
		config.save(file);
	}
	
	public void loadSBMenu(Player player) {
		
		PlayerInfo.findStats(player, "");
		
		Inventory SkyblockMenu = Bukkit.createInventory(player, 54, "Skyblock Menu");
		player.openInventory(SkyblockMenu);
		
		lore.clear();
		lore.add(ChatColor.RED + "❤ Health " + ChatColor.WHITE + PlayerInfo.findStats(player, "health") + " HP");
		lore.add(ChatColor.GREEN + "❈ Defense " + ChatColor.WHITE + PlayerInfo.findStats(player, "defense"));
		lore.add(ChatColor.RED + "❁ Strength " + ChatColor.WHITE + PlayerInfo.findStats(player, "strength"));
		SkyblockMenu.setItem(13, ItemSmith.getPlayerHead(player, lore, ChatColor.GREEN + "Your Skyblock Profile"));
		
		lore.clear();
		lore.add(ChatColor.GRAY + "View your Skill progression and");
		lore.add(ChatColor.GRAY + "rewards.");
		lore.add("");
		lore.add(ChatColor.YELLOW + "Click to view!");
		SkyblockMenu.setItem(19, ItemSmith.makeVanillaItem(Material.DIAMOND_SWORD, ChatColor.GREEN + "Your Skills", 1, lore));
		
		lore.clear();
		lore.add(ChatColor.GRAY + "View all of the items available");
		lore.add(ChatColor.GRAY + "in SkyBlock. Collect more of an");
		lore.add(ChatColor.GRAY + "item to unlock rewards on your");
		lore.add(ChatColor.GRAY + "way to becoming a master of");
		lore.add(ChatColor.GRAY + "SkyBlock!");
		lore.add("");
		lore.add(ChatColor.YELLOW + "Click to view!");
		SkyblockMenu.setItem(20, ItemSmith.makeVanillaItem(Material.PAINTING, ChatColor.GREEN + "Collection", 1, lore));
		
		lore.clear();
		lore.add(ChatColor.GRAY + "Through your adventure, you will");
		lore.add(ChatColor.GRAY + "unlock recipes for all kinds of");
		lore.add(ChatColor.GRAY + "special items! You can view how");
		lore.add(ChatColor.GRAY + "to craft these items here.");
		lore.add("");
		lore.add(ChatColor.YELLOW + "Click to view!");
		SkyblockMenu.setItem(21, ItemSmith.makeVanillaItem(Material.BOOK, ChatColor.GREEN + "Recipes", 1, lore));
		
		lore.clear();
		lore.add(ChatColor.GRAY + "View your available trades.");
		lore.add(ChatColor.GRAY + "These trades are always");
		lore.add(ChatColor.GRAY + "available and accessible through");
		lore.add(ChatColor.GRAY + "the SkyBlock Menu.");
		lore.add("");
		lore.add(ChatColor.YELLOW + "Click to view!");
		SkyblockMenu.setItem(22, ItemSmith.makeVanillaItem(Material.EMERALD, ChatColor.GREEN + "Trades", 1, lore));
		
		lore.clear();
		lore.add(ChatColor.GRAY + "View your active quests,");
		lore.add(ChatColor.GRAY + "progress, and rewards.");
		lore.add("");
		lore.add(ChatColor.YELLOW + "Click to view!");
		SkyblockMenu.setItem(23, ItemSmith.makeVanillaItem(Material.WRITABLE_BOOK, ChatColor.GREEN + "Quest Log", 1, lore));
		
		lore.clear();
		lore.add(ChatColor.GRAY + "View the SkyBlock Calendar,");
		lore.add(ChatColor.GRAY + "upcoming events, and event");
		lore.add(ChatColor.GRAY + "rewards!");
		lore.add("");
		lore.add(ChatColor.YELLOW + "Click to view!");
		SkyblockMenu.setItem(24, ItemSmith.makeVanillaItem(Material.CLOCK, ChatColor.GREEN + "Calendar and Events", 1, lore));
		
		lore.clear();
		lore.add(ChatColor.GRAY + "Store global items that you want");
		lore.add(ChatColor.GRAY + "to access at any time from");
		lore.add(ChatColor.GRAY + "anywhere here.");
		lore.add("");
		lore.add(ChatColor.YELLOW + "Click to view!");
		SkyblockMenu.setItem(25, ItemSmith.makeVanillaItem(Material.ENDER_CHEST, ChatColor.GREEN + "Ender Chest", 1, lore));
		
		lore.clear();
		lore.add(ChatColor.GRAY + "View and manage all of your");
		lore.add(ChatColor.GRAY + "active potion effects.");
		lore.add("");
		lore.add(ChatColor.GRAY + "Drink potions or splash them");
		lore.add(ChatColor.GRAY + "on the ground to buff yourself!");
		lore.add("");
		lore.add(ChatColor.YELLOW + "Click to view!");
		SkyblockMenu.setItem(29, ItemSmith.makeVanillaItem(Material.GLASS_BOTTLE, ChatColor.GREEN + "Active Effects", 1, lore));
		
		lore.clear();
		lore.add(ChatColor.GRAY + "View and manage all of your");
		lore.add(ChatColor.GRAY + "pets.");
		lore.add("");
		lore.add(ChatColor.GRAY + "Level up your pets faster by");
		lore.add(ChatColor.GRAY + "gaining xp in their favorite");
		lore.add(ChatColor.GRAY + "skill!");
		lore.add("");
		lore.add(ChatColor.YELLOW + "Click to view!");
		SkyblockMenu.setItem(30, ItemSmith.makeVanillaItem(Material.BONE, ChatColor.GREEN + "Pets", 1, lore));
	
		lore.clear();
		lore.add(ChatColor.GRAY + "Opens the crafting grid.");
		lore.add("");
		lore.add(ChatColor.YELLOW + "Click to view!");
		SkyblockMenu.setItem(31, ItemSmith.makeVanillaItem(Material.CRAFTING_TABLE, ChatColor.GREEN + "Crafting table", 1, lore));
	
		lore.clear();
		lore.add(ChatColor.GRAY + "Store armor sets and quickly");
		lore.add(ChatColor.GRAY + "switch between them.");
		lore.add("");
		lore.add(ChatColor.YELLOW + "Click to view!");
		SkyblockMenu.setItem(32, ItemSmith.makeVanillaItem(Material.LEATHER_CHESTPLATE, ChatColor.GREEN + "Wardrobe", 1, lore));
	
		lore.clear();
		lore.add(ChatColor.GRAY + "Teleport to islands you've");
		lore.add(ChatColor.GRAY + "already visited.");
		lore.add("");
		lore.add(ChatColor.YELLOW + "Click to pick location!");
		ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1, (short) SkullType.PLAYER.ordinal());
		 SkullMeta meta = (SkullMeta) skull.getItemMeta();
		meta.setOwner("Earth");
		meta.setDisplayName(ChatColor.AQUA + "Quick Travel");
		meta.setLore(lore);
		skull.setItemMeta(meta);
		SkyblockMenu.setItem(47, skull);
		
		lore.clear();
		lore.add(ChatColor.GRAY + "Coming soon!");
		lore.add("");
		lore.add(ChatColor.YELLOW + "Click to manage profiles!");
		SkyblockMenu.setItem(48, ItemSmith.makeVanillaItem(Material.NAME_TAG, ChatColor.AQUA + "Profile Management", 1, lore));
		
		lore.clear();
		lore.add(ChatColor.GRAY + "Obtain the " + ChatColor.LIGHT_PURPLE + "Cookie Buff");
		lore.add(ChatColor.GRAY + "from booster cookies in the");
		lore.add(ChatColor.GRAY + "hub's community shop.");
		lore.add("");
		lore.add(ChatColor.YELLOW + "Click to see all the info!");
		SkyblockMenu.setItem(49, ItemSmith.makeVanillaItem(Material.COOKIE, ChatColor.GOLD + "Booster Cookie", 1, lore));
		
		lore.clear();
		lore.add(ChatColor.GRAY + "View and edit your Skyblock settings.");
		lore.add("");
		lore.add(ChatColor.YELLOW + "Click to view!");
		SkyblockMenu.setItem(50, ItemSmith.makeVanillaItem(Material.REDSTONE_TORCH, ChatColor.GREEN + "Settings", 1, lore));
	}
	
	public void bankOpen(Player player) {
		Inventory Bank = Bukkit.createInventory(player, 36, "Bank");
		
		File file = new File(Main.getPlugin(Main.class).getDataFolder() + File.separator + player.getName() + ".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		String profile = config.getString("Player.Active Profile");
		int bankBal = config.getInt("Player." + profile + ".Bank");
		bankLimit = config.getInt("Player." + profile + ".BankLimit");
		
		lore.clear();
		lore.add(ChatColor.GRAY + "Current balance: " + ChatColor.GOLD + String.format("%,d", bankBal));
		lore.add("");
		lore.add(ChatColor.YELLOW + "Click to make a deposit!");
		Bank.setItem(11, ItemSmith.makeVanillaItem(Material.CHEST, ChatColor.GREEN + "Deposit Coins", 1, lore));
		
		lore.clear();
		lore.add(ChatColor.GRAY + "Current balance: " + ChatColor.GOLD + String.format("%,d", bankBal));
		lore.add("");
		lore.add(ChatColor.YELLOW + "Click to withdraw some coins!");
		Bank.setItem(13, ItemSmith.makeVanillaItem(Material.FURNACE, ChatColor.GREEN + "Withdraw Coins", 1, lore));
		
		lore.clear();
		Bank.setItem(31, ItemSmith.makeVanillaItem(Material.BARRIER, ChatColor.RED + "Close", 1, lore));
		
		lore.clear();
		lore.add(ChatColor.GRAY + "Keep your coins safe in the bank!");
		lore.add(ChatColor.GRAY + "You lose half the coins in your");
		lore.add(ChatColor.GRAY + "purse when dying in combat.");
		lore.add("");
		lore.add(ChatColor.GRAY + "Balance limit: " + ChatColor.GOLD + String.format("%,d", bankLimit));
		lore.add("");
		lore.add(ChatColor.GRAY + "The banker rewards you every 31");
		lore.add(ChatColor.GRAY + "hours with " + ChatColor.AQUA + "interest " + ChatColor.GRAY + "for the");
		lore.add(ChatColor.GRAY + "coins in your bank balance.");
		lore.add("");
		lore.add(ChatColor.GRAY + "Interest in: ");
		Bank.setItem(32, ItemSmith.makeVanillaItem(Material.REDSTONE_TORCH, ChatColor.GREEN + "Information", 1, lore));
		
		lore.clear();
		lore.add(ChatColor.GRAY + "Are you so rich that you can't");
		lore.add(ChatColor.GRAY + "even store your coins?");
		lore.add("");
		String accountType = null;
		switch(bankLimit) {
		case 50000000:
			accountType = "Starter";
			break;
		case 2000000000:
			accountType = ChatColor.RED + "Admin";
			break;
		default:
			accountType = ChatColor.DARK_RED + "????????????";
			break;
		}
		
		lore.add(ChatColor.GRAY + "Current account: " + ChatColor.GOLD + accountType);
		lore.add(ChatColor.GRAY + "Bank limit: " + ChatColor.GOLD + String.format("%,d", bankLimit));
		lore.add("");
		lore.add(ChatColor.YELLOW + "Click to view upgrades!");
		Bank.setItem(35, ItemSmith.makeVanillaItem(Material.GOLD_BLOCK, ChatColor.GOLD + "Bank Upgrades", 1, lore));
		
		player.openInventory(Bank);
	}
	
 

}
