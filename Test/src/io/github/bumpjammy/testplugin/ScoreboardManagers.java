package io.github.bumpjammy.testplugin;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.V10lator.lib24time.lib24time;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class ScoreboardManagers {

	public static void createScoreboard(Player p) {
		int health = (int) p.getHealth();
		int maxHealth = (int) p.getMaxHealth();
		String message = health + "/" + maxHealth;
		p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
		File file = new File(Main.getPlugin(Main.class).getDataFolder() + File.separator + p.getName() + ".yml"); // Get player's data file
	    FileConfiguration config = YamlConfiguration.loadConfiguration(file); // Load as a YML file
	    String profile = config.getString("Player.Active Profile"); // Get current profile
	    int coins = config.getInt("Player." + profile + ".Money"); // Get coins
	    Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
	    Objective objective = scoreboard.registerNewObjective("scoreboard", "dummy");

	    objective.setDisplayName(ChatColor.YELLOW + ChatColor.BOLD.toString() + "SKYBLOCK");
	    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
	    
	    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/YY");  
	    LocalDateTime now = LocalDateTime.now();  

	    Score first= objective.getScore(ChatColor.GRAY + dtf.format(now).toString());
	    Score second = objective.getScore("");
	    
	    Score third = objective.getScore(ChatColor.WHITE + " Early January 1st");
	    Score fourth = objective.getScore(" " + ChatColor.GRAY + lib24time.getTime(Bukkit.getWorld("World")));
	    Score fifth = objective.getScore(ChatColor.GRAY + " ⏣ " + ChatColor.AQUA + "Village");
	    Score sixth = objective.getScore(" ");
	    Score seventh = objective.getScore(ChatColor.WHITE + "Purse: " + ChatColor.GOLD + String.format("%,d", coins));
	    Score eigth = objective.getScore("  ");
	    Score ninth = objective.getScore(ChatColor.WHITE + "Objective:");
	    Score tenth = objective.getScore(ChatColor.YELLOW + "Craft a wood pickaxe");
	    Score eleventh = objective.getScore("   ");
	    Score twelth = objective.getScore(ChatColor.YELLOW + "bumpjammy.github.io");

	    first.setScore(12);
	    second.setScore(11);
	    third.setScore(10);
	    fourth.setScore(9);
	    fifth.setScore(8);
	    sixth.setScore(7);
	    seventh.setScore(6);
	    eigth.setScore(5);
	    ninth.setScore(4);
	    tenth.setScore(3);
	    eleventh.setScore(2);
	    twelth.setScore(1);
	    p.setScoreboard(scoreboard);
	}
}
