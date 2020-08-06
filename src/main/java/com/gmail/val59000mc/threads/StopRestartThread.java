package com.gmail.val59000mc.threads;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.languages.Lang;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class StopRestartThread implements Runnable{

	private long timeBeforeStop;
	
	public StopRestartThread(){
		this.timeBeforeStop = GameManager.getGameManager().getConfiguration().getTimeBeforeRestartAfterEnd();
	}
	
	@Override
	public void run() {
		// Firestarter start :: generate a new world at the end of the game without disconnecting
		GameManager gm = GameManager.getGameManager();
		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&lUHC: &fGet ready for next round- a new world is now generating."));

		for (Player player : Bukkit.getOnlinePlayers()) {
			player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard()); // clear player's existing scoreboard

			player.setGameMode(GameMode.SPECTATOR);
			player.teleport(gm.getLobbyLocation());
			player.setMaxHealth(20);
			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 99999, 0, true, false));
		}

		gm.loadNewGame();
		// Firestarter end
	}

}